package CheckDownloadEdit.PdfFiles;

import CheckDownloadEdit.FilesUtil;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Trade {

    private static int year = 0;
    private static int[] months = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private static int pageNumber = 0;


    public static void main(String[] args) {
       // System.out.println(PdfsUtil.getUrlStrings(months));
       // System.out.println(PdfsUtil.pdfSize);
        getValues();
        System.out.println(getMonth());
    }

    public static void getValues() {
        List<Double> values = new LinkedList<>();

        try {
            PdfReader reader = new PdfReader("C:\\Users\\Vallian\\IdeaProjects\\MokruhaMaven\\pdfs\\oper-09-2018.pdf");
            for (int i = 51; i <=75; ++i) {
                TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
                String text = PdfTextExtractor.getTextFromPage(reader, i, strategy);
                String[] test = text.split("\n");

                    if (test[2].contains("РЫНОК ТОВАРОВ")) {
                        pageNumber = i;
                        break;
                    }
            }

            for (int j = pageNumber; j < pageNumber + getPage(); j++) {
                TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
                String text = PdfTextExtractor.getTextFromPage(reader, j, strategy);
                String[] test = text.split("\n");
                for (int i = 0; i < test.length; i++) {
                    if (test[i].trim().equals("2018г.") && test[i+1].trim().startsWith("Январь ")) {
//                        System.out.println(test[i]);
//                        System.out.println(test[i+1]);
//                        System.out.println(j);
//                        System.out.println(i);
                        year++;
                    }
                    if (test[i].trim().startsWith(getMonth())) {
                        if (year == 2) {
                            System.out.println(test[i]);
                            System.out.println(j);
                            System.out.println(PdfsUtil.getNumber(test[i], 0));
                            //PdfsUtil.getNumber(test[i], 0);
                            System.out.println(PdfsUtil.getNumber(test[i], 3));
                            //PdfsUtil.getNumber(test[i], 3);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getPage() {
        return 4;
    }

    public static String getMonth() {
        LocalDate today = LocalDate.now();
        Month month = Month.of(today.getMonthValue()-2);
        Locale loc = Locale.forLanguageTag("ru");
        return month.getDisplayName(TextStyle.FULL_STANDALONE, loc);
    }



}
