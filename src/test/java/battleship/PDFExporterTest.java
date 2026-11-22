
package battleship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PDFExporterTest {

    @Test
    @DisplayName("Testar exportação de mais de 38 jogadas")
    void exportManyMoves() {

        List<String> moves = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            moves.add("Jogada " + i);
        }

        String filename = "teste_jogadas.pdf";

        PDFExporter.exportMovesToPDF(moves, filename);

        File file = new File(filename);

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        file.delete();
    }
}
