package battleship;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Uma jogada guardada na base de dados.
 */
public record StoredMove(long gameId, int number, LocalDateTime playedAt, List<StoredShot> shots) {
    @Override
    public String toString() {
        return "Jogo " + gameId + " | Jogada nº" + number + " | " + playedAt.withNano(0) + " | " + shots;
    }
}
