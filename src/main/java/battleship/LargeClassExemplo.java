package battleship;

/**
 * Classe criada para provocar o cheiro "Large Class" - 129785 (já corrigida).
 * <p>
 * Os métodos deixaram de ter ramificações: cada um tem complexidade 1,
 * pelo que o WMC desce para 23 (22 métodos + construtor), abaixo do limite
 * ClassComplexity (m_limit 46).
 */
public class LargeClassExemplo {

	private int contador;

	public LargeClassExemplo(int contador) {
		this.contador = contador;
	}

	public int metodo01(int x) { return x + 1 + contador; }
	public int metodo02(int x) { return x + 2 + contador; }
	public int metodo03(int x) { return x + 3 + contador; }
	public int metodo04(int x) { return x + 4 + contador; }
	public int metodo05(int x) { return x + 5 + contador; }
	public int metodo06(int x) { return x + 6 + contador; }
	public int metodo07(int x) { return x + 7 + contador; }
	public int metodo08(int x) { return x + 8 + contador; }
	public int metodo09(int x) { return x + 9 + contador; }
	public int metodo10(int x) { return x + 10 + contador; }
	public int metodo11(int x) { return x + 11 + contador; }
	public int metodo12(int x) { return x + 12 + contador; }
	public int metodo13(int x) { return x + 13 + contador; }
	public int metodo14(int x) { return x + 14 + contador; }
	public int metodo15(int x) { return x + 15 + contador; }
	public int metodo16(int x) { return x + 16 + contador; }
	public int metodo17(int x) { return x + 17 + contador; }
	public int metodo18(int x) { return x + 18 + contador; }
	public int metodo19(int x) { return x + 19 + contador; }
	public int metodo20(int x) { return x + 20 + contador; }
	public int metodo21(int x) { return x + 21 + contador; }
	public int metodo22(int x) { return x + 22 + contador; }
}
