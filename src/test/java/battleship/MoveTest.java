package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários para a classe Move")
class MoveTest {

    @Test
    @DisplayName("Deve devolver o número da jogada, lista de tiros e lista de resultados")
    void gettersAndConstructor() {
        IPosition pos1 = new Position(1, 1);
        List<IPosition> shots = List.of(pos1);
        IGame.ShotResult result = new IGame.ShotResult(true, false, null, false);
        List<IGame.ShotResult> results = List.of(result);

        Move move = new Move(3, shots, results);

        assertEquals(3, move.getNumber());
        assertEquals(shots, move.getShots());
        assertEquals(results, move.getShotResults());
    }

    @Test
    @DisplayName("Deve formatar corretamente o toString com número, tiros e resultados")
    void testToString() {
        List<IPosition> shots = List.of(new Position(0, 0), new Position(1, 2));
        List<IGame.ShotResult> results = List.of(new IGame.ShotResult(true, false, null, false));
        Move move = new Move(5, shots, results);

        assertEquals("Move{number=5, shots=2, results=1}", move.toString());
    }

    @Test
    @DisplayName("Deve processar tiros na água e tiros inválidos com verbose a false")
    void processEnemyFireMissedAndInvalidVerboseFalse() {
        IGame.ShotResult invalidShot = new IGame.ShotResult(false, false, null, false);
        IGame.ShotResult missedShot = new IGame.ShotResult(true, false, null, false);

        Move move = new Move(1, List.of(), List.of(invalidShot, missedShot));
        String json = move.processEnemyFire(false);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 1"));
        assertTrue(json.contains("\"missedShots\" : 1"));
        assertTrue(json.contains("\"repeatedShots\" : 0"));
    }

    @Test
    @DisplayName("Deve processar tiros repetidos com verbose a true")
    void processEnemyFireRepeatedShotsVerboseTrue() {
        IGame.ShotResult repeated1 = new IGame.ShotResult(true, true, null, false);
        IGame.ShotResult repeated2 = new IGame.ShotResult(true, true, null, false);

        Move move = new Move(2, List.of(), List.of(repeated1, repeated2));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"repeatedShots\" : 2"));
        assertTrue(json.contains("\"validShots\" : 0"));
    }

    @Test
    @DisplayName("Deve processar um único tiro repetido com verbose a true")
    void processEnemyFireSingleRepeatedShotVerboseTrue() {
        IGame.ShotResult repeated1 = new IGame.ShotResult(true, true, null, false);

        Move move = new Move(6, List.of(), List.of(repeated1));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"repeatedShots\" : 1"));
        assertTrue(json.contains("\"validShots\" : 0"));
    }

    @Test
    @DisplayName("Deve processar acertos e navios afundados com verbose a true")
    void processEnemyFireHitsAndSunkShipsVerboseTrue() {
        IShip caravel = new Caravel(Compass.NORTH, new Position(2, 2));
        IShip barge = new Barge(Compass.NORTH, new Position(5, 5));

        IGame.ShotResult hitNotSunk = new IGame.ShotResult(true, false, caravel, false);
        IGame.ShotResult hitAndSunk = new IGame.ShotResult(true, false, barge, true);

        Move move = new Move(4, List.of(), List.of(hitNotSunk, hitAndSunk));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 2"));
        assertTrue(json.contains("\"sunkBoats\""));
        assertTrue(json.contains("\"hitsOnBoats\""));
    }

    @Test
    @DisplayName("Deve processar múltiplos acertos no mesmo navio sem o afundar com verbose a true")
    void processEnemyFireMultipleHitsSameShipNotSunkVerboseTrue() {
        IShip caravel = new Caravel(Compass.NORTH, new Position(2, 2));
        IGame.ShotResult hit1 = new IGame.ShotResult(true, false, caravel, false);
        IGame.ShotResult hit2 = new IGame.ShotResult(true, false, caravel, false);

        Move move = new Move(11, List.of(), List.of(hit1, hit2));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 2"));
        assertTrue(json.contains("\"hitsOnBoats\""));
    }

    @Test
    @DisplayName("Deve processar múltiplos navios afundados do mesmo tipo com verbose a true")
    void processEnemyFireMultipleSunkShipsSameTypeVerboseTrue() {
        IShip barge1 = new Barge(Compass.NORTH, new Position(1, 1));
        IShip barge2 = new Barge(Compass.NORTH, new Position(4, 4));
        IGame.ShotResult sunk1 = new IGame.ShotResult(true, false, barge1, true);
        IGame.ShotResult sunk2 = new IGame.ShotResult(true, false, barge2, true);

        Move move = new Move(12, List.of(), List.of(sunk1, sunk2));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 2"));
        assertTrue(json.contains("\"sunkBoats\""));
    }

    @Test
    @DisplayName("Deve formatar mensagem com acerto único sem afundar e sem tiros na água nem repetidos")
    void processEnemyFireSingleHitNotSunkVerboseTrue() {
        IShip caravel = new Caravel(Compass.NORTH, new Position(1, 1));
        IGame.ShotResult hit = new IGame.ShotResult(true, false, caravel, false);

        Move move = new Move(9, List.of(), List.of(hit));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 1"));
    }

    @Test
    @DisplayName("Deve formatar mensagem com um único navio afundado com verbose a true")
    void processEnemyFireSingleSunkShipVerboseTrue() {
        IShip barge = new Barge(Compass.NORTH, new Position(3, 3));
        IGame.ShotResult sunk = new IGame.ShotResult(true, false, barge, true);

        Move move = new Move(10, List.of(), List.of(sunk));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 1"));
        assertTrue(json.contains("\"sunkBoats\""));
    }

    @Test
    @DisplayName("Deve processar tiros na água e tiros repetidos em conjunto com verbose a true")
    void processEnemyFireMissedAndRepeatedVerboseTrue() {
        IGame.ShotResult missed1 = new IGame.ShotResult(true, false, null, false);
        IGame.ShotResult missed2 = new IGame.ShotResult(true, false, null, false);
        IGame.ShotResult repeated1 = new IGame.ShotResult(true, true, null, false);
        IGame.ShotResult repeated2 = new IGame.ShotResult(true, true, null, false);

        Move move = new Move(7, List.of(), List.of(missed1, missed2, repeated1, repeated2));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 2"));
        assertTrue(json.contains("\"missedShots\" : 2"));
        assertTrue(json.contains("\"repeatedShots\" : 2"));
    }

    @Test
    @DisplayName("Deve processar um tiro na água e um tiro repetido com verbose a true")
    void processEnemyFireSingleMissedAndSingleRepeatedVerboseTrue() {
        IGame.ShotResult missed1 = new IGame.ShotResult(true, false, null, false);
        IGame.ShotResult repeated1 = new IGame.ShotResult(true, true, null, false);

        Move move = new Move(8, List.of(), List.of(missed1, repeated1));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 1"));
        assertTrue(json.contains("\"missedShots\" : 1"));
        assertTrue(json.contains("\"repeatedShots\" : 1"));
    }

    @Test
    @DisplayName("Deve cobrir acerto único em navio sem falhas na água nem tiros repetidos com verbose a true")
    void processEnemyFireOnlySingleHitVerboseTrue() {
        IShip caravel = new Caravel(Compass.NORTH, new Position(0, 0));
        IGame.ShotResult hit = new IGame.ShotResult(true, false, caravel, false);

        Move move = new Move(13, List.of(), List.of(hit));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 1"));
    }

    @Test
    @DisplayName("Deve cobrir múltiplos navios afundados e múltiplos tiros na água com verbose a true")
    void processEnemyFireMultipleSunkAndMultipleMissedVerboseTrue() {
        IShip barge1 = new Barge(Compass.NORTH, new Position(0, 0));
        IShip barge2 = new Barge(Compass.NORTH, new Position(2, 2));
        IGame.ShotResult sunk1 = new IGame.ShotResult(true, false, barge1, true);
        IGame.ShotResult sunk2 = new IGame.ShotResult(true, false, barge2, true);
        IGame.ShotResult missed1 = new IGame.ShotResult(true, false, null, false);
        IGame.ShotResult missed2 = new IGame.ShotResult(true, false, null, false);

        Move move = new Move(14, List.of(), List.of(sunk1, sunk2, missed1, missed2));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"missedShots\" : 2"));
        assertTrue(json.contains("\"sunkBoats\""));
    }

    @Test
    @DisplayName("Deve cobrir cenário misto de navio afundado, acerto sem afundar, tiro na água e repetido")
    void processEnemyFireMixedOutcomesVerboseTrue() {
        IShip barge = new Barge(Compass.NORTH, new Position(0, 0));
        IShip caravel = new Caravel(Compass.NORTH, new Position(2, 2));

        IGame.ShotResult sunk = new IGame.ShotResult(true, false, barge, true);
        IGame.ShotResult hit = new IGame.ShotResult(true, false, caravel, false);
        IGame.ShotResult missed = new IGame.ShotResult(true, false, null, false);
        IGame.ShotResult repeated = new IGame.ShotResult(true, true, null, false);

        Move move = new Move(15, List.of(), List.of(sunk, hit, missed, repeated));
        String json = move.processEnemyFire(true);

        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 3"));
        assertTrue(json.contains("\"missedShots\" : 1"));
        assertTrue(json.contains("\"repeatedShots\" : 1"));
    }
}