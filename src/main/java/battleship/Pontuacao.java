package battleship;

/**
 * Classe criada para provocar uma violação da regra LocalVariableName - 129785.
 */
public class Pontuacao {

	public int calcularPontos(IFleet fleet) {
		int total_pontos = 0;
		for (IShip ship : fleet.getSunkShips()) {
			total_pontos += ship.getSize() * 10;
		}
		return total_pontos;
	}
}
