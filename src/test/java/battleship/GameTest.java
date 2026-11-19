package battleship;

import java.util.List;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Scanner;

/**
 * Test class for Game.
 * Author: britoeabreu
 * Date: 2024-03-19
 * Time: 15:30
 * Cyclomatic Complexity for each method:
 * - Game (constructor): 1
 * - fire: 7
 * - getShots: 1
 * - getRepeatedShots: 1
 * - getInvalidShots: 1
 * - getHits: 1
 * - getSunkShips: 1
 * - getRemainingShips: 1
 * - validShot: 3
 * - repeatedShot: 2
 * - printBoard: 1
 * - printValidShots: 1
 * - printFleet: 1
 */
public class GameTest {

	private Game game;

	@BeforeEach
	void setUp() {
		game = new Game(new Fleet()); // Assuming Fleet is a concrete implementation of IFleet
	}

	@AfterEach
	void tearDown() {
		game = null;
	}

	@Test
	void constructor() {
		assertNotNull(game, "Game instance should not be null after construction.");
		assertNotNull(game.getAlienMoves(), "Shots list should not be null after initialization.");
		assertTrue(game.getAlienMoves().isEmpty(), "Shots list should be empty upon initialization.");
		assertEquals(0, game.getInvalidShots(), "Invalid shots count should be zero upon initialization.");
		assertEquals(0, game.getRepeatedShots(), "Repeated shots count should be zero upon initialization.");
		assertEquals(0, game.getHits(), "Hits count should be zero upon initialization.");
		assertEquals(0, game.getSunkShips(), "Sunk ships count should be zero upon initialization.");
	}

	@Test
	void fire2() {
		Position invalidPosition = new Position(-1, 5);
		game.fireSingleShot(invalidPosition, false);
		assertEquals(1, game.getInvalidShots(), "Invalid shots counter should increase for an invalid shot.");
	}

	@Test
	void fire3() {
		Position position = new Position(2, 3);
		game.fireSingleShot(position, false);
		game.fireSingleShot(position, true);
		assertEquals(1, game.getRepeatedShots(), "Repeated shots counter should increase for a repeated shot.");
	}

	@Test
	void repeatedShot1() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		Position position = new Position(2, 3);
		assertTrue(game.repeatedShot(position), "Position (2,3) should be marked as repeated after firing.");
	}

	@Test
	void repeatedShot2() {
		Position position = new Position(2, 3);
		assertFalse(game.repeatedShot(position), "Position (2,3) should not be marked as repeated before firing.");
	}

	@Test
	void getAlienMoves() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		assertEquals(1, game.getAlienMoves().size(), "Shots list should contain one shot after firing once.");
	}

	@Test
	void getRemainingShips() {
		IFleet fleet = game.getMyFleet();
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));

		fleet.addShip(ship1);
		assertEquals(1, game.getRemainingShips(), "Just one ship was created!");
		fleet.addShip(ship2);
		assertEquals(2, game.getRemainingShips(), "Two ships were created!");
		ship2.sink();
		assertEquals(1, game.getRemainingShips(), "Remaining ships count should be 1 after sinking one of two ships.");
	}

	@Test
	void fireSingleShotSinksBarge() {
		Ship ship = new Barge(Compass.NORTH, new Position(1, 1));
		game.getMyFleet().addShip(ship);

		assertEquals(1, game.getRemainingShips());

		game.fireSingleShot(new Position(1, 1), false);

		assertEquals(1, game.getHits());
		assertEquals(1, game.getSunkShips());
		assertEquals(0, game.getRemainingShips());
		assertFalse(ship.stillFloating());
	}

	@Test
	void fireSingleShotMissesShip() {
		Ship ship = new Barge(Compass.NORTH, new Position(1, 1));
		game.getMyFleet().addShip(ship);

		game.fireSingleShot(new Position(5, 5), false);

		assertEquals(0, game.getHits());
		assertEquals(0, game.getSunkShips());
		assertEquals(1, game.getRemainingShips());
		assertEquals(0, game.getInvalidShots());
		assertEquals(0, game.getRepeatedShots());
		assertTrue(ship.stillFloating());
	}

	@Test
	void fireSingleShotHitsWithoutSinking() {
		Ship ship = new Frigate(Compass.EAST, new Position(5, 5));
		game.getMyFleet().addShip(ship);

		game.fireSingleShot(new Position(5, 5), false);

		assertEquals(1, game.getHits());
		assertEquals(0, game.getSunkShips());
		assertEquals(1, game.getRemainingShips());
		assertTrue(ship.stillFloating());
	}

	@Test
	void fireShotsWithInvalidNumberOfShots() {
		List<IPosition> shots = List.of(
				new Position(1, 1),
				new Position(2, 2)
		);

		assertThrows(IllegalArgumentException.class,
				() -> game.fireShots(shots));

		assertTrue(game.getAlienMoves().isEmpty());
	}

	@Test
	void fireShotsWithRepeatedPositions() {
		List<IPosition> shots = List.of(
				new Position(1, 1),
				new Position(1, 1),
				new Position(2, 2)
		);

		game.fireShots(shots);

		assertEquals(1, game.getRepeatedShots());
		assertEquals(1, game.getAlienMoves().size());
		assertEquals(0, game.getInvalidShots());
	}

	@Test
	void fireShotsWithInvalidPosition() {
		List<IPosition> shots = List.of(
				new Position(1, 1),
				new Position(-1, 5),
				new Position(2, 2)
		);

		game.fireShots(shots);

		assertEquals(1, game.getInvalidShots());
		assertEquals(0, game.getRepeatedShots());
		assertEquals(1, game.getAlienMoves().size());
	}

	@Test
	void readEnemyFireWithValidInput() {
		Scanner scanner = new Scanner("A1 B2 C3\n");

		String result = game.readEnemyFire(scanner);

		assertNotNull(result);
		assertEquals(1, game.getAlienMoves().size());
		assertEquals(0, game.getInvalidShots());
		assertEquals(0, game.getRepeatedShots());

		scanner.close();
	}

	@Test
	void readEnemyFireWithIncompletePosition() {
		Scanner scanner = new Scanner("A B2 C3\n");

		assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(scanner));

		assertTrue(game.getAlienMoves().isEmpty());

		scanner.close();
	}

	@Test
	void readEnemyFireWithInsufficientPositions() {
		Scanner scanner = new Scanner("A1 B2\n");

		assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(scanner));

		assertTrue(game.getAlienMoves().isEmpty());
		assertEquals(0, game.getHits());

		scanner.close();
	}

	@Test
	void readEnemyFireWithSeparatedCoordinates() {
		Scanner scanner = new Scanner("A 1 B 2 C 3\n");

		String result = game.readEnemyFire(scanner);

		assertNotNull(result);
		assertEquals(1, game.getAlienMoves().size());
		assertEquals(0, game.getInvalidShots());
		assertEquals(0, game.getRepeatedShots());

		scanner.close();
	}

	@Test
	void jsonShotsWithValidPositions() {
		List<IPosition> shots = List.of(
				new Position(0, 0),
				new Position(1, 1),
				new Position(2, 2)
		);

		String json = Game.jsonShots(shots);

		assertNotNull(json);
		assertTrue(json.contains("\"row\""));
		assertTrue(json.contains("\"column\""));
		assertTrue(json.contains("\"A\""));
		assertTrue(json.contains("\"B\""));
		assertTrue(json.contains("\"C\""));
	}

	@Test
	void printMyBoardWithLegend() {
		Ship ship = new Barge(Compass.NORTH, new Position(1, 1));
		game.getMyFleet().addShip(ship);

		assertDoesNotThrow(() -> game.printMyBoard(true, true));
	}

	@Test
	void printMyBoardWithoutLegend() {
		assertDoesNotThrow(() -> game.printMyBoard(false, false));
	}

	@Test
	void printAlienBoardWithLegend() {
		assertDoesNotThrow(() -> game.printAlienBoard(true, true));
	}

	@Test
	void printAlienBoardWithoutLegend() {
		assertDoesNotThrow(() -> game.printAlienBoard(false, false));
	}

	@Test
	void printBoardWithHitAndMiss() {
		Ship ship = new Barge(Compass.NORTH, new Position(1, 1));
		game.getMyFleet().addShip(ship);

		game.fireShots(List.of(
				new Position(1, 1),
				new Position(5, 5),
				new Position(-1, 0)
		));

		assertDoesNotThrow(() -> game.printMyBoard(true, true));
	}

	@Test
	void printBoardWithSunkShip() {
		Ship ship = new Barge(Compass.NORTH, new Position(5, 5));
		game.getMyFleet().addShip(ship);

		game.fireShots(List.of(
				new Position(5, 5),
				new Position(0, 0),
				new Position(0, 1)
		));

		assertEquals(1, game.getSunkShips());
		assertDoesNotThrow(() -> game.printMyBoard(true, false));
	}

	@Test
	void gameIdWithoutRepository() {
		assertEquals(0, game.getGameId());
	}

	@Test
	void overDoesNotThrowException() {
		assertDoesNotThrow(() -> game.over());
	}

	@Test
	void randomEnemyFireGeneratesMove() {
		String result = game.randomEnemyFire();

		assertNotNull(result);
		assertEquals(1, game.getAlienMoves().size());
		assertEquals(0, game.getInvalidShots());
		assertEquals(0, game.getRepeatedShots());
	}

	@Test
	void randomEnemyFireTwice() {
		game.randomEnemyFire();
		game.randomEnemyFire();

		assertEquals(2, game.getAlienMoves().size());
		assertEquals(0, game.getRepeatedShots());
		assertEquals(0, game.getInvalidShots());
	}


	@Test
	void jsonShotsWithEmptyList() throws Exception {
		String result = Game.jsonShots(List.of());

		assertNotNull(result);

		com.fasterxml.jackson.databind.ObjectMapper mapper =
				new com.fasterxml.jackson.databind.ObjectMapper();

		assertTrue(mapper.readTree(result).isArray());
		assertEquals(0, mapper.readTree(result).size());
	}

	@Test
	void getAlienFleetReturnsFleet() {
		assertNotNull(game.getAlienFleet());
	}

	@Test
	void getMyMovesReturnsEmptyList() {
		assertNotNull(game.getMyMoves());
		assertTrue(game.getMyMoves().isEmpty());
	}

	@Test
	void constructorWithRepository() throws Exception {
		try (MoveRepository repository =
					 new MoveRepository("jdbc:h2:mem:game_constructor_test")) {

			Game dbGame = new Game(new Fleet(), repository);

			assertTrue(dbGame.getGameId() > 0);
			assertTrue(repository.findAllGameIds()
					.contains(dbGame.getGameId()));
			assertTrue(dbGame.getAlienMoves().isEmpty());
		}
	}

	@Test
	void fireShotsSavesMoveToRepository() throws Exception {
		try (MoveRepository repository =
					 new MoveRepository("jdbc:h2:mem:game_save_test")) {

			Game dbGame = new Game(new Fleet(), repository);

			dbGame.fireShots(List.of(
					new Position(1, 1),
					new Position(2, 2),
					new Position(3, 3)
			));

			List<MoveRepository.StoredMove> savedMoves =
					repository.findMovesByGame(dbGame.getGameId());

			assertEquals(1, savedMoves.size());
			assertEquals(3, savedMoves.get(0).shots().size());
			assertEquals(1, savedMoves.get(0).number());
		}
	}

	@Test
	void fireShotsSavesMultipleMovesToRepository() throws Exception {
		try (MoveRepository repository =
					 new MoveRepository("jdbc:h2:mem:game_multiple_test")) {

			Game dbGame = new Game(new Fleet(), repository);

			dbGame.fireShots(List.of(
					new Position(1, 1),
					new Position(2, 2),
					new Position(3, 3)
			));

			dbGame.fireShots(List.of(
					new Position(4, 4),
					new Position(5, 5),
					new Position(6, 6)
			));

			List<MoveRepository.StoredMove> savedMoves =
					repository.findMovesByGame(dbGame.getGameId());

			assertEquals(2, savedMoves.size());
			assertEquals(1, savedMoves.get(0).number());
			assertEquals(2, savedMoves.get(1).number());
			assertEquals(3, savedMoves.get(0).shots().size());
			assertEquals(3, savedMoves.get(1).shots().size());
		}
	}


	@Test
	void randomEnemyFireWithTwoAvailablePositions() {
		List<IPosition> positions = new java.util.ArrayList<>();

		for (int row = 0; row < Game.BOARD_SIZE; row++) {
			for (int col = 0; col < Game.BOARD_SIZE; col++) {
				if (!(row == 0 && (col == 0 || col == 1))) {
					positions.add(new Position(row, col));
				}
			}
		}

		for (int i = 0; i + Game.NUMBER_SHOTS <= positions.size();
			 i += Game.NUMBER_SHOTS) {
			game.fireShots(positions.subList(i, i + Game.NUMBER_SHOTS));
		}

		assertDoesNotThrow(() -> game.randomEnemyFire());
	}

	@Test
	void randomEnemyFireWithOneAvailablePosition() {
		List<IPosition> positions = new java.util.ArrayList<>();

		for (int row = 0; row < Game.BOARD_SIZE; row++) {
			for (int col = 0; col < Game.BOARD_SIZE; col++) {
				if (!(row == 0 && col == 0)) {
					positions.add(new Position(row, col));
				}
			}
		}

		for (int i = 0; i + Game.NUMBER_SHOTS <= positions.size();
			 i += Game.NUMBER_SHOTS) {
			game.fireShots(positions.subList(i, i + Game.NUMBER_SHOTS));
		}

		assertDoesNotThrow(() -> game.randomEnemyFire());
	}

	@Test
	void fireShotsHandlesDatabaseError() throws Exception {
		MoveRepository repository =
				new MoveRepository("jdbc:h2:mem:game_error_test");

		Game dbGame = new Game(new Fleet(), repository);

		repository.close();

		assertDoesNotThrow(() -> dbGame.fireShots(List.of(
				new Position(1, 1),
				new Position(2, 2),
				new Position(3, 3)
		)));

		assertEquals(1, dbGame.getAlienMoves().size());
	}

	@Test
	void fireSingleShotWithExplicitRepeatedFlag() {
		Position pos = new Position(0, 0);

		IGame.ShotResult result = game.fireSingleShot(pos, true);

		assertTrue(result.valid());
		assertTrue(result.repeated());
		assertNull(result.ship());
		assertFalse(result.sunk());
	}

	@Test
	void fireSingleShotRepeatedFromPreviousMove() {
		Position pos = new Position(0, 0);

		game.fireShots(List.of(
				pos,
				new Position(1, 1),
				new Position(2, 2)
		));

		IGame.ShotResult result = game.fireSingleShot(pos, false);

		assertTrue(result.valid());
		assertTrue(result.repeated());
	}

	@Test
	void readEnemyFireRejectsMissingRow() {
		try (Scanner scanner = new Scanner("A\n")) {
			assertThrows(
					IllegalArgumentException.class,
					() -> game.readEnemyFire(scanner)
			);
		}
	}

	@Test
	void fireShotsRejectsTooManyShots() {
		List<IPosition> shots = List.of(
				new Position(0, 0),
				new Position(1, 1),
				new Position(2, 2),
				new Position(3, 3)
		);

		assertThrows(
				IllegalArgumentException.class,
				() -> game.fireShots(shots)
		);
	}

	@Test
	void jsonShotsRejectsNull() {
		assertThrows(AssertionError.class,
				() -> Game.jsonShots(null));
	}

	@Test
	void readEnemyFireRejectsNullScanner() {
		assertThrows(AssertionError.class,
				() -> game.readEnemyFire(null));
	}

	@Test
	void fireShotsRejectsNullList() {
		assertThrows(AssertionError.class,
				() -> game.fireShots(null));
	}

	@Test
	void fireSingleShotRejectsNullPosition() {
		assertThrows(AssertionError.class,
				() -> game.fireSingleShot(null, false));
	}

	@Test
	void repeatedShotRejectsNullPosition() {
		assertThrows(AssertionError.class,
				() -> game.repeatedShot(null));
	}

	@Test
	void printBoardRejectsNullFleet() {
		assertThrows(AssertionError.class,
				() -> Game.printBoard(null, List.of(), false, false));
	}

	@Test
	void printBoardRejectsNullMoves() {
		assertThrows(AssertionError.class,
				() -> Game.printBoard(new Fleet(), null, false, false));
	}

	@Test
	void randomEnemyFireWithTwoAvailablePositionsRepeatedly() {
		for (int i = 0; i < 100; i++){
			Game testGame = new Game(new Fleet());

			for (int row = 0; row < Game.BOARD_SIZE; row++) {
				for (int col = 0; col < Game.BOARD_SIZE; col++) {
					if (!((row == 0 && col == 0)
							|| (row == 0 && col == 1))) {
						testGame.fireSingleShot(
								new Position(row, col), false);
					}
				}
			}

			assertNotNull(testGame.randomEnemyFire());
		}
	}






}