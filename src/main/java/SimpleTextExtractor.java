
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.IOException;

public class SimpleTextExtractor {

    private static String dest = "C:\\Users\\Vallian\\IdeaProjects\\MokruhaMaven\\pdfs\\oper-09-2018.pdf";

    public static void main(String[] args) throws IOException {
        // считаем, что программе передается один аргумент - имя файла
        PdfReader reader = new PdfReader(dest);

        // не забываем, что нумерация страниц в PDF начинается с единицы.
        for (int i = 1; i <= 7; ++i) {
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            String[] test = text.split("\n");
            for (int j = 0; j < test.length; j++) {

                if (test[j].contains("Валовой внутренний")) {
                    System.out.println(test[j - 1]);
                    System.out.println(test[j] + test[j + 1]);
                    System.out.println("testing");
                }
                if (test[j].startsWith("Валовая добавленная"))
                    System.out.println(test[j] + test[j + 1]);
                //System.out.println("TEST");
            }
            //System.out.println("TEST");
            //System.out.println(text);
            System.out.println("END OF TEST");
        }

        // убираем за собой
        reader.close();
    }
}
