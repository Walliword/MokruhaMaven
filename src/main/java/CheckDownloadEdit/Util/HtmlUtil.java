package CheckDownloadEdit.Util;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlUtil {

    public static final Logger LOG = LoggerFactory.getLogger(HtmlUtil.class);
    public static boolean isNow = false;
    public static int VOLUME = 0;

    /**
     * Метод для получения автоматического сдвига по годам.
     *
     * @return - возвращает значение сдвига в зависимости от наличия данных за текущий год.
     */
    public static int getMove() {
        if (HtmlUtil.isNow) {
            return 12 * (FilesUtil.CURRENT_YEAR - 18);
        } else {
            return 12 * (FilesUtil.CURRENT_YEAR - 19);
        }
    }

    /**
     * Метод, проверяющий - появилась ли страница для текущего года, или ещё заполняется предыдущий.
     *
     * @param link1 - предполагаемая ссылка на данные текущего года;
     * @param link2 - ссылка на данные предыдущего года;
     * @return - возвращает соответствующую строку или null, если возникло внеплановое событие;
     * @throws IOException - бросает исключение, если возникли какие-то проблемы при работе со ссылками.
     */

    public static String chooseYear(String link1, String link2) throws IOException {
        if (doesURLExist(new URL(link1))) {
            isNow = true;
            return link1;
        } else if (doesURLExist(new URL(link2))) {
            isNow = false;
            return link2;
        } else return null;
    }

    /**
     * Метод, преобразующий ряд чисел в лист массивов по строкам, в зависимости от числа доступных месяцев.
     *
     * @param link1       - предполагаемая ссылка на текущий год;
     * @param link2       - ссылка на предыдущий год;
     * @param linesAmount - количество параметров, значения которых необходимо достать для каждого месяца;
     * @return - возвращает список с нужными массивами данных или null, если возникли сложности;
     * @throws IOException - бросает исключение, если возникли непредвиденные обстоятельства.
     */
    public static List<Double[]> getLinesOfNumbers(String link1, String link2, int linesAmount) throws IOException {
        String link;
        if ((link = HtmlUtil.chooseYear(link1, link2)) != null) {
            List<Double> numbers = getNumbers(link);
            List<Double[]> linesOfNumbers = new ArrayList<>();
            VOLUME = numbers.size() / linesAmount;
            for (int i = 0; i < linesAmount; i++) {
                Double[] monthNum = new Double[VOLUME];
                for (int j = 0; j < VOLUME; j++) {
                    monthNum[j] = numbers.get(j + VOLUME * i);
                }
                linesOfNumbers.add(monthNum);
            }
            return linesOfNumbers;
        } else return null;
    }

    /**
     * Метод, проверяющий актуальность ссылки.
     *
     * @param url - непосредственно ссылка;
     * @return - возвращает true, если страница существует и false - если не существует;
     * @throws IOException - бросает исключение в случае непредвиденных обстоятельств.
     */
    public static boolean doesURLExist(URL url) throws IOException {
        // We want to check the current URL
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        // We don't need to get data
        httpURLConnection.setRequestMethod("HEAD");
        // Some websites don't like programmatic access so pretend to be a browser
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
        int responseCode = httpURLConnection.getResponseCode();
        // We only accept response code 200
        return responseCode == HttpURLConnection.HTTP_OK;
    }

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
            } else if (r > 5 && r < 14) {
                worksheetMKR.getRow(r).getCell(c).setCellValue(data.get(r - 4)[c - 1]);
            } else if (r > 15) {
                worksheetMKR.getRow(r).getCell(c).setCellValue(data.get(r - 5)[c - 1]);
            }
        }
        return r;
    }
}
