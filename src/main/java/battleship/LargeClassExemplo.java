package battleship;

/**
 * Classe criada deliberadamente para provocar o cheiro "Large Class" - 129785.
 * <p>
 * Tem 22 métodos, cada um com complexidade ciclomática 3 (um if com &&),
 * o que dá WMC = 66 + 1 (construtor) e ultrapassa o limite ClassComplexity (m_limit 46).
 */
public class LargeClassExemplo {

	private int contador;

	public LargeClassExemplo(int contador) {
		this.contador = contador;
	}

	public int metodo01(int x) { if (x > 0 && contador > 0) { return x + 1; } return 0; }
	public int metodo02(int x) { if (x > 0 && contador > 0) { return x + 2; } return 0; }
	public int metodo03(int x) { if (x > 0 && contador > 0) { return x + 3; } return 0; }
	public int metodo04(int x) { if (x > 0 && contador > 0) { return x + 4; } return 0; }
	public int metodo05(int x) { if (x > 0 && contador > 0) { return x + 5; } return 0; }
	public int metodo06(int x) { if (x > 0 && contador > 0) { return x + 6; } return 0; }
	public int metodo07(int x) { if (x > 0 && contador > 0) { return x + 7; } return 0; }
	public int metodo08(int x) { if (x > 0 && contador > 0) { return x + 8; } return 0; }
	public int metodo09(int x) { if (x > 0 && contador > 0) { return x + 9; } return 0; }
	public int metodo10(int x) { if (x > 0 && contador > 0) { return x + 10; } return 0; }
	public int metodo11(int x) { if (x > 0 && contador > 0) { return x + 11; } return 0; }
	public int metodo12(int x) { if (x > 0 && contador > 0) { return x + 12; } return 0; }
	public int metodo13(int x) { if (x > 0 && contador > 0) { return x + 13; } return 0; }
	public int metodo14(int x) { if (x > 0 && contador > 0) { return x + 14; } return 0; }
	public int metodo15(int x) { if (x > 0 && contador > 0) { return x + 15; } return 0; }
	public int metodo16(int x) { if (x > 0 && contador > 0) { return x + 16; } return 0; }
	public int metodo17(int x) { if (x > 0 && contador > 0) { return x + 17; } return 0; }
	public int metodo18(int x) { if (x > 0 && contador > 0) { return x + 18; } return 0; }
	public int metodo19(int x) { if (x > 0 && contador > 0) { return x + 19; } return 0; }
	public int metodo20(int x) { if (x > 0 && contador > 0) { return x + 20; } return 0; }
	public int metodo21(int x) { if (x > 0 && contador > 0) { return x + 21; } return 0; }
	public int metodo22(int x) { if (x > 0 && contador > 0) { return x + 22; } return 0; }
}
