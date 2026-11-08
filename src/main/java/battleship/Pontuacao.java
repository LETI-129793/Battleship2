package battleship;

/**
 * Classe criada para provocar uma violação da regra LocalVariableName - 129785.
 */
public class Pontuacao {

	public int calcularPontos(IFleet fleet) {
		int totalPontos = 0;
		for (IShip ship : fleet.getSunkShips()) {
			totalPontos += ship.getSize() * 10;
		}
		return totalPontos;
	}
}
