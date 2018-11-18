package CheckDownloadEdit.inProgress;

import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.PdfsUtil;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Wage {


    private static int[] months = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private static List<String> pdfs = PdfsUtil.getUrlStrings(months, FilesUtil.getYear());
    private static int pdfSize = pdfs.size();

    public static void main(String[] args) {
makeMagic();
    }

    public static void makeMagic() {
getRawLines();
    }

    private static List<String> getRawLines() {
        List<String> rawLines = new ArrayList<>();
        File file = FilesUtil.downloadFile(pdfs.get(pdfSize - 1));
        try {
            assert file != null;
            PdfReader reader = new PdfReader(file.getAbsolutePath());
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, 3, strategy);
//                        System.out.println(text);
            int page = PdfsUtil.getPage(text, "УРОВЕНЬ ЖИЗНИ НАСЕЛЕНИЯ…");
            System.out.println(page);
            TextExtractionStrategy tableStrategy = new SimpleTextExtractionStrategy();
            for (int i = page + 2; i <= page + 3; i++) {
                TextExtractionStrategy pageStrategy = new SimpleTextExtractionStrategy();
                String tableText = PdfTextExtractor.getTextFromPage(reader, i, pageStrategy);
                System.out.println(tableText);
                String[] lines = tableText.split("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawLines;
    }


}
