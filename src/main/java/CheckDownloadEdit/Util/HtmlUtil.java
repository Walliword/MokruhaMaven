package CheckDownloadEdit.Util;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlUtil {

    public static Elements getElementsByTr(String linkUrl) throws IOException {
        Document doc = Jsoup.connect(linkUrl).get();
        return doc.getElementsByTag("tr");
    }

    public static Elements getElementsByTd(String linkUrl) throws IOException {
        Document doc = Jsoup.connect(linkUrl).get();
        return doc.getElementsByTag("td");
    }

    public static boolean isDouble(String s) throws NumberFormatException {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static List<String> getRawLines(String linkURL) throws IOException {
        Elements links = HtmlUtil.getElementsByTd(linkURL);
        List<String> list = new ArrayList<>();
        for (Element link : links) {
            list.add(link.text());
        }
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i).replaceAll(" ", ""));
            if (list.get(i).length() == 0) {
                list.remove(i);
                i--;
            }
        }
        return list;
    }

    public static List<Double> getNumbers(String linkURL) throws IOException {
        List<String> rawLines = getRawLines(linkURL);
        List<Double> numbers = new ArrayList<>();
        for (String rawLine : rawLines) {
            if (HtmlUtil.isDouble(rawLine)) {
                numbers.add(Double.valueOf(rawLine));
            }
        }
        return numbers;
    }

    public static int getR(List<Double[]> data, XSSFSheet worksheetMKR, XSSFCellStyle style, int r, int volume) {
        for (int c = 1; c <= volume; c++) {
            if (r == 5 || r == 14) {
                r++;
            }
            if (worksheetMKR.getRow(r) == null) {
                worksheetMKR.createRow(r);
            }
            if (worksheetMKR.getRow(r).getCell(c) == null) {
                worksheetMKR.getRow(r).createCell(c);
            }
            worksheetMKR.getRow(r).getCell(c).setCellStyle(style);
            if (r < 5) {
                worksheetMKR.getRow(r).getCell(c).setCellValue(data.get(r - 3)[c - 1]);
            }
            else if (r>5 && r<14) {
                worksheetMKR.getRow(r).getCell(c).setCellValue(data.get(r - 4)[c - 1]);
            }
            else if (r > 15) {
                worksheetMKR.getRow(r).getCell(c).setCellValue(data.get(r - 5)[c - 1]);
            }
        }
        return r;
    }
}
