package CheckDownloadEdit.Util;

import java.util.ArrayList;
import java.util.List;

public class PdfsUtil {

    public static int pdfSize = 0;

    //сбор ссылки по частям
    private static final String PART1 = "http://www.gks.ru/free_doc/doc_20";
    //здесь год
    private static final String PART2 = "/info/oper-";
    //здесь месяц
    private static final String PART3 = "-20";
    //и снова год
    private static final String PART4 = ".pdf";

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
        String a = s.replaceAll("[A-Za-zА-Яа-я;()·.]", "").trim().replaceAll(",", ".");
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
}
