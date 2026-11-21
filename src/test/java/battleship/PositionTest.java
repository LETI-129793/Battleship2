package battleship;

import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Position.
 * Author: britoeabreu
 * Date: 2024-03-19 15:30
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getRow: 1
 * - getColumn: 1
 * - isValid: 4
 * - isAdjacentTo: 4
 * - isOccupied: 1
 * - isHit: 1
 * - occupy: 1
 * - shoot: 1
 * - equals: 3
 * - hashCode: 1
 * - toString: 1
 */
@DisplayName("Testes unitários para a classe Position")
public class PositionTest {
	private Position position;

	@BeforeEach
	void setUp() {
		position = new Position(2, 3);
	}

	@AfterEach
	void tearDown() {
		position = null;
	}

	@Test
	@DisplayName("Deve gerar uma posição aleatória válida com randomPosition()")
	void randomPositionTest() {
		Position randomPos = Position.randomPosition();
		assertNotNull(randomPos);
		assertTrue(randomPos.isInside());
	}

	@Test
	@DisplayName("Deve construir Position com linha e coluna clássicas e obter getClassicColumn()")
	void classicConstructorAndClassicColumnTest() {
		Position pos = new Position('c', 4);
		assertEquals(2, pos.getRow());
		assertEquals(3, pos.getColumn());
		assertEquals('C', pos.getClassicRow());
		assertEquals(4, pos.getClassicColumn());
		assertFalse(pos.isOccupied());
		assertFalse(pos.isHit());
	}

	@Test
	@DisplayName("Deve construir Position a partir de linha e coluna numéricas")
	void constructor() {
		Position pos = new Position(1, 1);
		assertNotNull(pos, "Failed to create Position: object is null");
		assertEquals(1, pos.getRow(), "Failed to set row: expected 1 but got " + pos.getRow());
		assertEquals(1, pos.getColumn(), "Failed to set column: expected 1 but got " + pos.getColumn());
		assertFalse(pos.isOccupied(), "New position should not be occupied");
		assertFalse(pos.isHit(), "New position should not be hit");
	}

	@Test
	@DisplayName("Deve devolver a linha numérica correta")
	void getRow() {
		assertEquals(2, position.getRow(), "Failed to get row: expected 2 but got " + position.getRow());
	}

	@Test
	@DisplayName("Deve devolver a coluna numérica correta")
	void getColumn() {
		assertEquals(3, position.getColumn(), "Failed to get column: expected 3 but got " + position.getColumn());
	}

	@Test
	@DisplayName("Deve devolver a linha clássica correta")
	void getClassicRow() {
		assertEquals('C', position.getClassicRow(), "Failed to get row: expected 2 but got " + position.getRow());
	}

	@Test
	@DisplayName("Deve devolver a coluna clássica correta")
	void getClassicColumn() {
		assertEquals(4, position.getClassicColumn(), "Failed to get classic column: expected 4 but got " + position.getClassicColumn());
	}

	@Test
	@DisplayName("Deve validar posição (0,0) dentro dos limites")
	void isValid1() {
		position = new Position(0, 0);
		assertTrue(position.isInside(), "Position (0,0) should be valid");
	}

	@Test
	@DisplayName("Deve rejeitar posição com linha negativa")
	void isValid2() {
		position = new Position(-1, 5);
		assertFalse(position.isInside(), "Position with negative row should be invalid");
	}

	@Test
	@DisplayName("Deve rejeitar posição com coluna negativa")
	void isValid3() {
		position = new Position(5, -1);
		assertFalse(position.isInside(), "Position with negative column should be invalid");
	}

	@Test
	@DisplayName("Deve rejeitar posição com linha >= BOARD_SIZE")
	void isValid4() {
		position = new Position(Game.BOARD_SIZE, 5);
		assertFalse(position.isInside(), "Position with row >= BOARD_SIZE should be invalid");
	}

	@Test
	@DisplayName("Deve rejeitar posição com coluna >= BOARD_SIZE")
	void isValid5() {
		position = new Position(5, Game.BOARD_SIZE);
		assertFalse(position.isInside(), "Position with column >= BOARD_SIZE should be invalid");
	}

	@Test
	@DisplayName("Deve detetar posição adjacente na horizontal")
	void isAdjacentTo1() {
		Position other = new Position(2, 4);
		assertTrue(position.isAdjacentTo(other), "Failed to detect horizontally adjacent position");
	}

	@Test
	@DisplayName("Deve detetar posição adjacente na vertical")
	void isAdjacentTo2() {
		Position other = new Position(3, 3);
		assertTrue(position.isAdjacentTo(other), "Failed to detect vertically adjacent position");
	}

	@Test
	@DisplayName("Deve detetar posição adjacente na diagonal")
	void isAdjacentTo3() {
		Position other = new Position(3, 4);
		assertTrue(position.isAdjacentTo(other), "Failed to detect diagonally adjacent position");
	}

	@Test
	@DisplayName("Deve rejeitar posição não adjacente")
	void isAdjacentTo4() {
		Position other = new Position(4, 5);
		assertFalse(position.isAdjacentTo(other), "Non-adjacent position incorrectly identified as adjacent");
	}

	@Test
	@DisplayName("Deve cobrir ramos adicionais de isAdjacentTo")
	void isAdjacentToExtraBranches() {
		assertFalse(position.isAdjacentTo(new Position(2, 5)));
		assertFalse(position.isAdjacentTo(new Position(5, 3)));
	}

	@Test
	@DisplayName("Deve lançar NullPointerException se other for null em isAdjacentTo")
	void isAdjacentToWithNull() {
		assertThrows(NullPointerException.class, () -> position.isAdjacentTo(null),
				"isAdjacentTo should throw NullPointerException for null input");
	}

	@Test
	@DisplayName("Deve devolver posições adjacentes válidas dentro dos limites do tabuleiro")
	void adjacentPositionsTest() {
		Position corner = new Position(0, 0);
		List<IPosition> cornerAdjacents = corner.adjacentPositions();
		assertEquals(3, cornerAdjacents.size());

		List<IPosition> centerAdjacents = position.adjacentPositions();
		assertEquals(8, centerAdjacents.size());
	}

	@Test
	@DisplayName("Deve marcar posição como ocupada com occupy()")
	void isOccupied() {
		assertFalse(position.isOccupied(), "New position should not be occupied");
		position.occupy();
		assertTrue(position.isOccupied(), "Position should be occupied after occupy()");
	}

	@Test
	@DisplayName("Deve marcar posição como atingida com shoot()")
	void isHit() {
		assertFalse(position.isHit(), "New position should not be hit");
		position.shoot();
		assertTrue(position.isHit(), "Position should be hit after shoot()");
	}

	@Test
	@DisplayName("Deve considerar posições iguais quando têm mesmas coordenadas")
	void equals1() {
		Position same = new Position(2, 3);
		assertTrue(position.equals(same), "Equal positions not identified as equal");
	}

	@Test
	@DisplayName("Deve devolver false ao comparar com null")
	void equals2() {
		assertFalse(position.equals(null), "Position should not equal null");
	}

	@Test
	@DisplayName("Deve devolver false ao comparar com objeto de outro tipo")
	void equals3() {
		Object other = new Object();
		assertFalse(position.equals(other), "Position should not equal non-Position object");
	}

	@Test
	@DisplayName("Deve devolver false quando a coluna é diferente")
	void equals4() {
		Position other = new Position(2, 4);
		assertFalse(position.equals(other), "Positions with the same row but different column should not be equal");
	}

	@Test
	@DisplayName("Deve devolver true quando comparado consigo próprio")
	void equals5() {
		assertTrue(position.equals(position), "A position should be equal to itself");
	}

	@Test
	@DisplayName("Deve devolver false quando a linha é diferente na comparação de igualdade")
	void equalsDifferentRow() {
		assertFalse(position.equals(new Position(5, 3)),
				"Positions with different row should not be equal");
	}

	@Test
	@DisplayName("Deve manter hashCode consistente para posições iguais")
	void hashCodeConsistency() {
		Position same = new Position(2, 3);
		assertEquals(position.hashCode(), same.hashCode(),
				"Hash codes not consistent for equal positions");
	}

	@Test
	@DisplayName("Deve formatar toString como coordenada clássica")
	void toStringFormat() {
		String expected = "C4";
		assertEquals(expected, position.toString(),
				"Incorrect string representation: expected '" + expected +
						"' but got '" + position.toString() + "'");
	}
}