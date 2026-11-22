package battleship;

import java.util.Scanner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

/**
 * TasksTest
 *
 * @author Your Name
 * Date: 07/01/2026
 * Time: 00:53
 */
class TasksTest {

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void taskA() {

		Scanner scanner = new Scanner("A3");

		IPosition position = Tasks.readClassicPosition(scanner);

		assertEquals(new Position(0, 2), position);

		scanner.close();
	}

	@Test
	void taskB() {
		Scanner scanner1 = new Scanner("AB3");

		assertThrows(IllegalArgumentException.class,
				() -> Tasks.readClassicPosition(scanner1));

		scanner1.close();

		Scanner scanner2 = new Scanner("");

		assertThrows(IllegalArgumentException.class,
				() -> Tasks.readClassicPosition(scanner2));

		scanner2.close();
	}

	@Test
	void taskC() {
		Scanner scanner = new Scanner("A 3");

		IPosition position = Tasks.readClassicPosition(scanner);

		assertEquals(new Position(0, 2), position);

		scanner.close();
	}

	@Test
	void taskD() {
		Scanner scanner = new Scanner("a3");

		IPosition position = Tasks.readClassicPosition(scanner);

		assertEquals(new Position(0, 2), position);

		scanner.close();
	}

	@Test
	void buildFleet() {
		Scanner scanner = new Scanner(
				"barca 0 0 n " +
						"barca 0 3 n " +
						"barca 0 6 n " +
						"barca 0 9 n " +
						"barca 3 0 n " +
						"barca 3 3 n " +
						"barca 3 6 n " +
						"barca 3 9 n " +
						"barca 6 0 n " +
						"barca 6 3 n " +
						"barca 6 6 n"
		);

		Fleet fleet = Tasks.buildFleet(scanner);

		assertNotNull(fleet);
		assertEquals(11, fleet.getShips().size());

		scanner.close();
	}

	@Test
	@DisplayName("Testar leitura de um navio")
	void readShip() {
		Scanner scanner = new Scanner("barca 5 5 n");

		Ship ship = Tasks.readShip(scanner);

		assertNotNull(ship);
		assertEquals("Barca", ship.getCategory());
		assertEquals(new Position(5, 5), ship.getPosition());
		assertEquals(Compass.NORTH, ship.getBearing());

		scanner.close();
	}

	@Test
	void readPosition() {

			Scanner scanner = new Scanner("3 5");

			Position position = Tasks.readPosition(scanner);

			assertEquals(new Position(3, 5), position);

			scanner.close();
		}

	@Test
	void firingRound() {
		Game game = new Game(new Fleet());

		Scanner scanner = new Scanner("A 1 B 3 C 5");

		String result = game.readEnemyFire(scanner);

		assertNotNull(result);
		assertEquals(1, game.getAlienMoves().size());
		assertEquals(3, game.getAlienMoves().get(0).getShots().size());

		scanner.close();
	}
	@Test
	@DisplayName("Testar apresentação do menu de ajuda")
	void menuHelp() {

		Tasks.menuHelp();

		assertDoesNotThrow(() -> Tasks.menuHelp());
	}
	@Test
	@DisplayName("Testar o menu com comando de saída")
	void menu() {

		java.io.InputStream original = System.in;
		java.io.ByteArrayInputStream input =
				new java.io.ByteArrayInputStream("desisto\n".getBytes());

		try {
			System.setIn(input);

			assertDoesNotThrow(() -> Tasks.menu());

		} finally {
			System.setIn(original);
		}
	}

	@Test
	@DisplayName("Testar comandos do menu sem jogo iniciado")
	void menuCommands() {

		java.io.InputStream original = System.in;

		String commands =
				"estado\n" +
						"mapa\n" +
						"tiros\n" +
						"pdf\n" +
						"historico\n" +
						"ajuda\n" +
						"comandoInvalido\n" +
						"desisto\n";

		try {
			System.setIn(new java.io.ByteArrayInputStream(
					commands.getBytes()));

			Tasks.menu();

		} finally {
			System.setIn(original);
		}
	}

	@Test
	@DisplayName("Testar comandos do menu com jogo iniciado")
	void menuWithGame() {

		java.io.InputStream original = System.in;

		String commands =
				"gerafrota\n" +
						"estado\n" +
						"mapa\n" +
						"tiros\n" +
						"pdf\n" +
						"historico\n" +
						"desisto\n";

		try {
			System.setIn(new java.io.ByteArrayInputStream(
					commands.getBytes()));

			Tasks.menu();

		} finally {
			System.setIn(original);
		}
	}

	@Test
	@DisplayName("Testar criação de frota através do menu")
	void menuLoadFleet() {

		java.io.InputStream original = System.in;

		String commands =
				"lefrota\n" +
						"barca 0 0 n\n" +
						"barca 0 3 n\n" +
						"barca 0 6 n\n" +
						"barca 0 9 n\n" +
						"barca 3 0 n\n" +
						"barca 3 3 n\n" +
						"barca 3 6 n\n" +
						"barca 3 9 n\n" +
						"barca 6 0 n\n" +
						"barca 6 3 n\n" +
						"barca 6 6 n\n" +
						"desisto\n";

		try {
			System.setIn(new java.io.ByteArrayInputStream(
					commands.getBytes()));

			Tasks.menu();

		} finally {
			System.setIn(original);
		}
	}

	@Test
	@DisplayName("Testar rajada através do menu")
	void menuRajada() {

		java.io.InputStream original = System.in;

		String commands =
				"gerafrota\n" +
						"rajada A1 B3 C5\n" +
						"desisto\n";

		try {
			System.setIn(new java.io.ByteArrayInputStream(
					commands.getBytes()));

			Tasks.menu();

		} finally {
			System.setIn(original);
		}
	}

	@Test
	@DisplayName("Testar navio desconhecido e direção inválida")
	void readShipInvalid() {

		Scanner scanner1 = new Scanner("desconhecido 5 5 n");

		Ship ship1 = Tasks.readShip(scanner1);

		assertNull(ship1);

		scanner1.close();

		Scanner scanner2 = new Scanner("barca 5 5 x");

		assertThrows(AssertionError.class,
				() -> Tasks.readShip(scanner2));

		scanner2.close();
	}
	@Test
	@DisplayName("Testar frota com navio fora do tabuleiro")
	void buildFleetInvalid() {

		Scanner scanner = new Scanner(
				"barca 20 20 n " +
						"barca 0 0 n " +
						"barca 0 3 n " +
						"barca 0 6 n " +
						"barca 0 9 n " +
						"barca 3 0 n " +
						"barca 3 3 n " +
						"barca 3 6 n " +
						"barca 3 9 n " +
						"barca 6 0 n " +
						"barca 6 3 n " +
						"barca 6 6 n"
		);

		Fleet fleet = Tasks.buildFleet(scanner);

		assertEquals(11, fleet.getShips().size());

		scanner.close();
	}


}