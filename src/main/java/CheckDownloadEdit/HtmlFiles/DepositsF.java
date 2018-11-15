package CheckDownloadEdit.HtmlFiles;

import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.HtmlUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DepositsF {

    private static final String LINK = "https://www.cbr.ru/statistics/print.aspx?file=bank_system/4-2-1a_" +
            FilesUtil.getYear() +
            ".htm&pid=pdko_sub&sid=dpbvf";
    private static int VOLUME = 0;


    public void makeMagic() {
        try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL))) {
            List<Double[]> data = getLinesOfNumbers();
            XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
            XSSFSheet worksheetMKR = wbMKR.getSheetAt(11);

            XSSFCellStyle style = wbMKR.createCellStyle();
            style.setBorderRight(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setBorderBottom(BorderStyle.MEDIUM);
            for (int r = 3; r < 22; r++) {
                for (int c = 1; c <= VOLUME; c++) {
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
            }
            FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
            wbMKR.write(mokruha);
            mokruha.close();
            System.out.println("Редактирование страницы Депозиты физиков завершено");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getRawLines() throws IOException {
        Elements links = HtmlUtil.getElementsByTd(LINK);
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

    private List<Double> getNumbers() throws IOException {
        List<String> rawLines = getRawLines();
        List<Double> numbers = new ArrayList<>();
        for (String rawLine : rawLines) {
            if (HtmlUtil.isDouble(rawLine)) {
                numbers.add(Double.valueOf(rawLine));
            }
        }
        return numbers;
    }

    private List<Double[]> getLinesOfNumbers() throws IOException {
        List<Double> numbers = getNumbers();
        List<Double[]> linesOfNumbers = new ArrayList<>();
        VOLUME = numbers.size() / 17;
        for (int i = 0; i < 17; i++) {
            Double[] monthNum = new Double[VOLUME];
            for (int j = 0; j < VOLUME; j++) {
                monthNum[j] = numbers.get(j + VOLUME * i);
            }
            linesOfNumbers.add(monthNum);
        }
        return linesOfNumbers;
    }
}
