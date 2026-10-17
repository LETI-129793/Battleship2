package battleship;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Armazena os jogos e as jogadas numa base de dados H2 (via JDBC).
 * <p>
 * Esquema:
 * <ul>
 *     <li>GAMES  - um registo por jogo iniciado</li>
 *     <li>MOVES  - uma jogada (rajada de {@link Game#NUMBER_SHOTS} tiros) de um jogo</li>
 *     <li>SHOTS  - cada tiro de uma jogada, com o respetivo resultado</li>
 * </ul>
 */
public class MoveRepository implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Base de dados em ficheiro, criada na pasta de trabalho (./data/battleship.mv.db)
     */
    public static final String DEFAULT_URL = "jdbc:h2:./data/battleship";

    /**
     * Um tiro guardado na base de dados.
     */
    public record StoredShot(String position, boolean valid, boolean repeated, String shipCategory, boolean sunk) {
        @Override
        public String toString() {
            if (!valid)
                return position + " (exterior)";
            if (repeated)
                return position + " (repetido)";
            if (shipCategory == null)
                return position + " (água)";
            return position + " (" + shipCategory + (sunk ? ", afundado)" : ")");
        }
    }

    /**
     * Uma jogada guardada na base de dados.
     */
    public record StoredMove(long gameId, int number, LocalDateTime playedAt, List<StoredShot> shots) {
        @Override
        public String toString() {
            return "Jogo " + gameId + " | Jogada nº" + number + " | " + playedAt.withNano(0) + " | " + shots;
        }
    }

    private final Connection connection;

    /**
     * Abre (ou cria) a base de dados no URL indicado e garante que as tabelas existem.
     *
     * @param jdbcUrl URL JDBC da base de dados H2, p.ex. "jdbc:h2:mem:teste" ou {@link #DEFAULT_URL}
     * @throws SQLException se não for possível ligar à base de dados
     */
    public MoveRepository(String jdbcUrl) throws SQLException {
        this.connection = DriverManager.getConnection(jdbcUrl, "sa", "");
        createTables();
    }

    private void createTables() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("""
					CREATE TABLE IF NOT EXISTS GAMES (
					    ID         BIGINT AUTO_INCREMENT PRIMARY KEY,
					    STARTED_AT TIMESTAMP NOT NULL
					)""");
            st.execute("""
					CREATE TABLE IF NOT EXISTS MOVES (
					    ID          BIGINT AUTO_INCREMENT PRIMARY KEY,
					    GAME_ID     BIGINT NOT NULL REFERENCES GAMES(ID),
					    MOVE_NUMBER INT NOT NULL,
					    PLAYED_AT   TIMESTAMP NOT NULL
					)""");
            st.execute("""
					CREATE TABLE IF NOT EXISTS SHOTS (
					    ID            BIGINT AUTO_INCREMENT PRIMARY KEY,
					    MOVE_ID       BIGINT NOT NULL REFERENCES MOVES(ID),
					    SHOT_INDEX    INT NOT NULL,
					    POSITION      VARCHAR(16) NOT NULL,
					    IS_VALID      BOOLEAN NOT NULL,
					    IS_REPEATED   BOOLEAN NOT NULL,
					    SHIP_CATEGORY VARCHAR(20),
					    IS_SUNK       BOOLEAN NOT NULL
					)""");
        }
    }

    /**
     * Regista o início de um novo jogo.
     *
     * @return o identificador do jogo criado
     * @throws SQLException em caso de erro na base de dados
     */
    public long startGame() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO GAMES (STARTED_AT) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            return generatedKey(ps);
        }
    }

    /**
     * Guarda uma jogada e todos os seus tiros numa única transação.
     *
     * @param gameId identificador do jogo (obtido com {@link #startGame()})
     * @param move   a jogada a guardar
     * @throws SQLException em caso de erro na base de dados
     */
    public void saveMove(long gameId, IMove move) throws SQLException {
        assert move != null;

        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            long moveId;
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO MOVES (GAME_ID, MOVE_NUMBER, PLAYED_AT) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, gameId);
                ps.setInt(2, move.getNumber());
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                ps.executeUpdate();
                moveId = generatedKey(ps);
            }

            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO SHOTS (MOVE_ID, SHOT_INDEX, POSITION, IS_VALID, IS_REPEATED, SHIP_CATEGORY, IS_SUNK) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                List<IPosition> shots = move.getShots();
                List<IGame.ShotResult> results = move.getShotResults();
                for (int i = 0; i < shots.size(); i++) {
                    IGame.ShotResult result = results.get(i);
                    ps.setLong(1, moveId);
                    ps.setInt(2, i);
                    ps.setString(3, shots.get(i).toString());
                    ps.setBoolean(4, result.valid());
                    ps.setBoolean(5, result.repeated());
                    ps.setString(6, result.ship() == null ? null : result.ship().getCategory());
                    ps.setBoolean(7, result.sunk());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    /**
     * Obtém todas as jogadas guardadas de um jogo, ordenadas pelo número da jogada.
     *
     * @param gameId identificador do jogo
     * @return a lista de jogadas (vazia se não existirem)
     * @throws SQLException em caso de erro na base de dados
     */
    public List<StoredMove> findMovesByGame(long gameId) throws SQLException {
        List<StoredMove> moves = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT ID, MOVE_NUMBER, PLAYED_AT FROM MOVES WHERE GAME_ID = ? ORDER BY MOVE_NUMBER")) {
            ps.setLong(1, gameId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    moves.add(new StoredMove(gameId, rs.getInt("MOVE_NUMBER"),
                            rs.getTimestamp("PLAYED_AT").toLocalDateTime(), findShotsByMove(rs.getLong("ID"))));
                }
            }
        }
        return moves;
    }

    /**
     * Obtém os identificadores de todos os jogos guardados.
     *
     * @return lista de identificadores, do mais antigo para o mais recente
     * @throws SQLException em caso de erro na base de dados
     */
    public List<Long> findAllGameIds() throws SQLException {
        List<Long> ids = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT ID FROM GAMES ORDER BY ID")) {
            while (rs.next())
                ids.add(rs.getLong("ID"));
        }
        return ids;
    }

    private List<StoredShot> findShotsByMove(long moveId) throws SQLException {
        List<StoredShot> shots = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT POSITION, IS_VALID, IS_REPEATED, SHIP_CATEGORY, IS_SUNK FROM SHOTS WHERE MOVE_ID = ? ORDER BY SHOT_INDEX")) {
            ps.setLong(1, moveId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    shots.add(new StoredShot(rs.getString("POSITION"), rs.getBoolean("IS_VALID"),
                            rs.getBoolean("IS_REPEATED"), rs.getString("SHIP_CATEGORY"), rs.getBoolean("IS_SUNK")));
                }
            }
        }
        return shots;
    }

    private static long generatedKey(Statement st) throws SQLException {
        try (ResultSet keys = st.getGeneratedKeys()) {
            if (!keys.next())
                throw new SQLException("Nenhuma chave gerada pela base de dados");
            return keys.getLong(1);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.warn("Erro ao fechar a base de dados", e);
        }
    }
}

