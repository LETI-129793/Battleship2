package battleship;

/**
 * Um tiro guardado na base de dados.
 */
public record StoredShot(String position, boolean valid, boolean repeated, String shipCategory, boolean sunk) {
    @Override
    public String toString() {
        if (!valid)
            return position + " (exterior)";
        if (repeated)
            return position + " (repetido)";
        if (shipCategory == null)
            return position + " (água)";
        return position + " (" + shipCategory + (sunk ? ", afundado)" : ")");
    }
}
