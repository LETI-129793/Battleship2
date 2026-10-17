package battleship;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.util.List;

public class PDFExporter {

    public static void exportMovesToPDF(List<String> moves, String filename) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();

                // Titulo
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                content.newLineAtOffset(50, 750);
                content.showText("Relatorio de Jogadas - Batalha Naval");
                content.newLineAtOffset(0, -30);

                // Imprimir cada jogada
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                int count = 0;
                for (String move : moves) {
                    String line = move.replaceAll("[^\\x00-\\x7F]", "");
                    content.showText("- " + line);
                    content.newLineAtOffset(0, -16);

                    count++;
                    if (count >= 38) break;
                }
                content.endText();
            }

            document.save(filename);
            System.out.println("Ficheiro PDF gerado com sucesso: " + filename);

        } catch (IOException e) {
            System.err.println("Erro ao gerar o PDF: " + e.getMessage());
        }
    }
}