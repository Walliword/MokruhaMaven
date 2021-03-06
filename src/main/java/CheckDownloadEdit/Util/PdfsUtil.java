package CheckDownloadEdit.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PdfsUtil {

    private static int year;
    private static int month;

    static {
        LocalDate today = LocalDate.now();
        int m = today.getMonthValue();
        if (m == 1) {
            year = FilesUtil.CURRENT_YEAR - 1;
            month = 11;
        }
        else {
            year = FilesUtil.CURRENT_YEAR;
            month = m - 2;
        }
    }

    public static int getYear() {
        return year;
    }

    public static int getMonth() {
        return month;
    }

    public static int pdfSize = 0;

    //сбор ссылки по частям
    private static final String PART1 = "http://www.gks.ru/free_doc/doc_20";
    //здесь год
    private static final String PART2 = "/info/oper-";
    //здесь месяц
    private static final String PART3 = "-20";
    //и снова год
    private static final String PART4 = ".pdf";

    public static final Logger LOG = LoggerFactory.getLogger(PdfsUtil.class);

    public static List<String> getUrlStrings(int[] months, int year) {
        List<String> pdfs = new ArrayList<>();
        for (int month : months) {
            StringBuilder builder = new StringBuilder();
            builder.append(PART1).append(year).append(PART2);
            if (month < 10) {
                builder.append(0);
            }
            builder.append(month).append(PART3).append(year).append(PART4);
            String urlName = builder.toString();
            if (FilesUtil.exists(urlName)) {
                pdfs.add(urlName);
            }
        }
        //от количества ссылок будем отталкиваться
        pdfSize = pdfs.size();
        return pdfs;
    }

    public static double getNumber(String s, int position) {
        String a = s.replaceAll("[A-Za-zА-Яа-я;()·.\\-]", "").trim().replaceAll(",", ".");
        String numberString = (a.split(" ")[position]);
        return Double.parseDouble(numberString);
    }

    public static int getPage(String contents, String search) {
        String[] lines = contents.split("\n");
        String page = "";
        for (String line : lines) {
            if (line.contains(search)) {
                page = line.substring(10).replaceAll("[A-Za-zА-Яа-я;.,…]", "").trim();
            }
        }
        return Integer.parseInt(page);
    }

    public static String getMonthName(int number) {
        Month month = Month.of(number);
        Locale loc = Locale.forLanguageTag("ru");
        String result = month.getDisplayName(TextStyle.FULL_STANDALONE, loc);
        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }
}
