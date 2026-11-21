package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários para a classe MoveRepository")
class MoveRepositoryTest {

    private MoveRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        repository = new MoveRepository("jdbc:h2:mem:battleship_test_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
    }

    @AfterEach
    void tearDown() {
        if (repository != null) {
            repository.close();
        }
    }

    @Test
    @DisplayName("Deve iniciar jogos e devolver os respetivos identificadores ordenados")
    void startGameAndFindAllGameIds() throws SQLException {
        assertTrue(repository.findAllGameIds().isEmpty());

        long game1 = repository.startGame();
        long game2 = repository.startGame();

        List<Long> ids = repository.findAllGameIds();
        assertEquals(2, ids.size());
        assertEquals(List.of(game1, game2), ids);
    }

    @Test
    @DisplayName("Deve guardar uma jogada com vários tipos de tiros e recuperá-la por jogo")
    void saveMoveAndFindMovesByGame() throws SQLException {
        long gameId = repository.startGame();

        IPosition posInvalid = new Position(0, 0);
        IPosition posRepeated = new Position(1, 1);
        IPosition posWater = new Position(2, 2);
        IPosition posHit = new Position(3, 3);
        IPosition posSunk = new Position(4, 4);

        IShip caravel = new Caravel(Compass.NORTH, new Position(3, 3));
        IShip barge = new Barge(Compass.NORTH, new Position(4, 4));

        List<IPosition> shots = List.of(posInvalid, posRepeated, posWater, posHit, posSunk);
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(false, false, null, false),
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, false, caravel, false),
                new IGame.ShotResult(true, false, barge, true)
        );

        IMove move = new Move(1, shots, results);
        repository.saveMove(gameId, move);

        List<StoredMove> moves = repository.findMovesByGame(gameId);
        assertEquals(1, moves.size());

        StoredMove storedMove = moves.get(0);
        assertEquals(gameId, storedMove.gameId());
        assertEquals(1, storedMove.number());
        assertNotNull(storedMove.playedAt());
        assertEquals(5, storedMove.shots().size());
        assertFalse(storedMove.shots().get(0).valid());
        assertTrue(storedMove.shots().get(1).repeated());
        assertNull(storedMove.shots().get(2).shipCategory());
        assertEquals("Caravela", storedMove.shots().get(3).shipCategory());
        assertFalse(storedMove.shots().get(3).sunk());
        assertTrue(storedMove.shots().get(4).sunk());
    }

    @Test
    @DisplayName("Deve lançar SQLException ao tentar guardar jogada para um jogo inexistente")
    void saveMoveInvalidGameIdThrowsSQLException() {
        IMove move = new Move(1, List.of(), List.of());

        assertThrows(SQLException.class, () -> repository.saveMove(999999L, move));
    }

    @Test
    @DisplayName("Deve lançar AssertionError se a jogada passada a saveMove for nula com assertions ativas")
    void saveMoveNullThrowsAssertionError() throws SQLException {
        long gameId = repository.startGame();

        assertThrows(AssertionError.class, () -> repository.saveMove(gameId, null));
    }

    @Test
    @DisplayName("Deve devolver lista vazia quando o jogo não tem jogadas guardadas")
    void findMovesByGameEmpty() throws SQLException {
        long gameId = repository.startGame();
        assertTrue(repository.findMovesByGame(gameId).isEmpty());
    }

    @Test
    @DisplayName("Deve formatar corretamente o toString de StoredShot em todos os estados")
    void storedShotToString() {
        StoredShot exterior = new StoredShot("A1", false, false, null, false);
        StoredShot repeated = new StoredShot("A2", true, true, null, false);
        StoredShot water = new StoredShot("A3", true, false, null, false);
        StoredShot hit = new StoredShot("A4", true, false, "Caravela", false);
        StoredShot sunk = new StoredShot("A5", true, false, "Barge", true);

        assertEquals("A1 (exterior)", exterior.toString());
        assertEquals("A2 (repetido)", repeated.toString());
        assertEquals("A3 (água)", water.toString());
        assertEquals("A4 (Caravela)", hit.toString());
        assertEquals("A5 (Barge, afundado)", sunk.toString());
    }

    @Test
    @DisplayName("Deve formatar corretamente o toString de StoredMove")
    void storedMoveToString() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 19, 14, 0, 0, 123456);
        List<StoredShot> shots = List.of(
                new StoredShot("B2", true, false, null, false)
        );
        StoredMove storedMove = new StoredMove(7L, 2, time, shots);

        assertEquals("Jogo 7 | Jogada nº2 | 2026-11-19T14:00 | [B2 (água)]", storedMove.toString());
    }

    @Test
    @DisplayName("Deve fechar a ligação sem lançar erro ao chamar close múltiplas vezes")
    void closeIdempotent() {
        assertDoesNotThrow(() -> {
            repository.close();
            repository.close();
        });
    }
}