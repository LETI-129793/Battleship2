package battleship;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.lang3.time.StopWatch;

/**
 * The type Tasks.
 */
public class Tasks {
	/**
	 * The constant LOGGER.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The constant GOODBYE_MESSAGE.
	 */
	private static final String GOODBYE_MESSAGE = "Bons ventos!";

	/**
	 * Strings to be used by the user
	 */
	private static final String AJUDA = "ajuda";
	private static final String GERAFROTA = "gerafrota";
	private static final String LEFROTA = "lefrota";
	private static final String DESISTIR = "desisto";
	private static final String RAJADA = "rajada";
	private static final String TIROS = "tiros";
	private static final String MAPA = "mapa";
	private static final String STATUS = "estado";
	private static final String SIMULA = "simula";
	private static final String HISTORICO = "historico";
	private static final String PDF = "pdf";


	/**
	 * Repositório onde as jogadas são armazenadas (null se a base de dados não estiver disponível)
	 */
	private static MoveRepository repository;

	/**
	 * This task also tests the fighting element of a round of three shots
	 */
	public static void menu() {

		IFleet myFleet = null;
		IGame game = null;
		repository = openRepository();
		menuHelp();

		System.out.print("> ");
		Scanner in = new Scanner(System.in);
		String command = in.next();
		while (!command.equals(DESISTIR)) {

			switch (command) {
				case GERAFROTA:
					myFleet = Fleet.createRandom();
					game = newGame(myFleet);
					game.printMyBoard(false, true);
					break;
				case LEFROTA:
					myFleet = buildFleet(in);
					game = newGame(myFleet);
					game.printMyBoard(false, true);
					break;
				case HISTORICO:
					printHistory(game);
					break;
				case STATUS:
					if (myFleet != null)
						myFleet.printStatus();
					break;
				case MAPA:
					if (myFleet != null)
						game.printMyBoard(false, true);
					break;
				case RAJADA:
					if (game != null) {

						StopWatch timer = StopWatch.createStarted();

						game.readEnemyFire(in);
						myFleet.printStatus();
						game.printMyBoard(true, false);


						timer.stop();
						System.out.println("[Relógio] Tempo gasto nesta jogada: " + timer.getTime() + " ms");

						if (game.getRemainingShips() == 0) {
							game.over();
							System.exit(0);
						}
					}
					break;
				case "pdf":
					if (game != null) {
						// Converter as jogadas do jogo para uma lista de texto
						List<String> movesAsText = game.getAlienMoves().stream()
								.map(Object::toString)
								.toList();

						PDFExporter.exportMovesToPDF(movesAsText, "jogadas_partida.pdf");
					} else {
						System.out.println("Inicie um jogo primeiro!");
					}
					break;
				case SIMULA:
					if (game != null) {
						while (game.getRemainingShips() > 0){
							game.randomEnemyFire();
							myFleet.printStatus();
							game.printMyBoard(true, false);
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt(); // Best practice: restore interrupt status
							}
						}

						if (game.getRemainingShips() == 0) {
							game.over();
							closeRepository();
							System.exit(0);
						}
					}
					break;
				case TIROS:
					if (game != null)
						game.printMyBoard(true, true);
					break;
				case AJUDA:
					menuHelp();
					break;
				default:
					System.out.println("Que comando é esse??? Repete ...");
			}
			System.out.print("> ");
			command = in.next();
		}
		closeRepository();
		System.out.println(GOODBYE_MESSAGE);
	}

	/**
	 * Abre a base de dados das jogadas. Se não for possível, o jogo continua sem armazenamento.
	 *
	 * @return o repositório aberto, ou null em caso de erro
	 */
	private static MoveRepository openRepository() {
		try {
			return new MoveRepository(MoveRepository.DEFAULT_URL);
		} catch (SQLException e) {
			LOGGER.warn("Base de dados indisponível, as jogadas não serão guardadas: {}", e.getMessage());
			return null;
		}
	}

	private static void closeRepository() {
		if (repository != null) {
			repository.close();
			repository = null;
		}
	}

	/**
	 * Cria um novo jogo, guardando as jogadas na base de dados quando esta estiver disponível.
	 *
	 * @param fleet a frota do jogo
	 * @return o novo jogo
	 */
	private static IGame newGame(IFleet fleet) {
		if (repository != null) {
			try {
				Game game = new Game(fleet, repository);
				LOGGER.info("Jogo nº{} registado na base de dados", game.getGameId());
				return game;
			} catch (SQLException e) {
				LOGGER.warn("Não foi possível registar o jogo na base de dados: {}", e.getMessage());
			}
		}
		return new Game(fleet);
	}

	/**
	 * Mostra as jogadas do jogo atual guardadas na base de dados.
	 *
	 * @param game o jogo atual
	 */
	private static void printHistory(IGame game) {
		if (repository == null || !(game instanceof Game g) || g.getGameId() == 0) {
			System.out.println("Não há jogadas guardadas na base de dados para este jogo.");
			return;
		}
		try {
			List<MoveRepository.StoredMove> moves = repository.findMovesByGame(g.getGameId());
			System.out.println("Jogadas guardadas do jogo nº" + g.getGameId() + ": " + moves.size());
			for (MoveRepository.StoredMove move : moves)
				System.out.println(move);
		} catch (SQLException e) {
			LOGGER.error("Erro ao ler o histórico de jogadas", e);
		}
	}

	/**
	 * This function provides help information about the menu commands.
	 */
	public static void menuHelp() {
		System.out.println("======================= AJUDA DO MENU =========================");
		System.out.println("Digite um dos comandos abaixo para interagir com o jogo:");
		System.out.println("- " + GERAFROTA + ": Gera uma frota aleatória de navios.");
		System.out.println("- " + LEFROTA + ": Permite criar e carregar uma frota personalizada.");
		System.out.println("- " + STATUS + ": Mostra o status atual da frota.)");
		System.out.println("- " + MAPA + ": Exibe o mapa da frota.");
		System.out.println("- " + RAJADA + ": Realiza uma rajada de disparos.");
		System.out.println("- " + SIMULA + ": Simula um jogo completo.");
		System.out.println("- " + HISTORICO + ": Mostra as jogadas do jogo atual guardadas na base de dados.");
		System.out.println("- " + TIROS + ": Lista os tiros válidos realizados (* = tiro em navio, o = tiro na água)");
		System.out.println("- " + DESISTIR + ": Encerra o jogo.");
		System.out.println("- " + PDF + ":Gera um relatorio das jogadas da partida em formato PDF.");
		System.out.println("===============================================================");
	}
	/**
	 * This operation allows the build up of a fleet, given user data
	 *
	 * @param in The scanner to read from
	 * @return The fleet that has been built
	 */
	public static Fleet buildFleet(Scanner in) {

		assert in != null;

		Fleet fleet = new Fleet();
		int i = 0; // i represents the total of successfully created ships
		while (i < Fleet.FLEET_SIZE) {
			IShip s = readShip(in);
			if (s != null) {
				boolean success = fleet.addShip(s);
				if (success)
					i++;
				else
					LOGGER.info("Falha na criacao de {} {} {}", s.getCategory(), s.getBearing(), s.getPosition());
			} else {
				LOGGER.info("Navio desconhecido!");
			}
		}
		LOGGER.info("{} navios adicionados com sucesso!", i);
		return fleet;
	}

	/**
	 * This operation reads data about a ship, build it and returns it
	 *
	 * @param in The scanner to read from
	 * @return The created ship based on the data that has been read
	 */
	public static Ship readShip(Scanner in) {

		assert in != null;

		String shipKind = in.next();
		Position pos = readPosition(in);
		char c = in.next().charAt(0);
		Compass bearing = Compass.charToCompass(c);
		return Ship.buildShip(shipKind, bearing, pos);
	}

	/**
	 * This operation allows reading a position in the map
	 *
	 * @param in The scanner to read from
	 * @return The position that has been read
	 */
	public static Position readPosition(Scanner in) {

		assert in != null;

		int row = in.nextInt();
		int column = in.nextInt();
		return new Position(row, column);
	}

	/**
	 * This operation allows reading a position in the map
	 *
	 * @param in The scanner to read from
	 * @return The classic position that has been read
	 */
	public static IPosition readClassicPosition(@NotNull Scanner in) {
		// Verifica se ainda há tokens disponíveis
		if (!in.hasNext()) {
			throw new IllegalArgumentException("Nenhuma posição válida encontrada!");
		}

		String part1 = in.next(); // Primeiro token
		String part2 = null;

		if (in.hasNextInt()) {
			part2 = in.next(); // Segundo token, se disponível
		}

		String input = (part2 != null) ? part1 + part2 : part1;

		// Normalizar o input para tratar letras maiúsculas e minúsculas
		input = input.toUpperCase();

		// Verificar os dois formatos possíveis: compactos e com espaço
		if (input.matches("[A-Z]\\d+")) {
			char column = input.charAt(0); // Extrair a coluna
			int row = Integer.parseInt(input.substring(1)); // Extrair a linha
			return new Position(column, row);
		} else if (part2 != null && part1.matches("[A-Z]") && part2.matches("\\d+")) {
			char column = part1.charAt(0); // Extrair a coluna
			int row = Integer.parseInt(part2); // Extrair a linha
			return new Position(column, row);
		} else {
			throw new IllegalArgumentException("Formato inválido. Use 'A3', 'A 3' ou similar.");
		}
	}

}