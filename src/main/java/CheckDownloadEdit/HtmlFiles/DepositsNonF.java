package CheckDownloadEdit.HtmlFiles;

import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.HtmlUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DepositsNonF {

    private static final String LINK = "https://www.cbr.ru/statistics/print.aspx?file=bank_system/4-2-2_" +
            FilesUtil.getYear() +
            ".htm&pid=pdko_sub&sid=dpbvo";
    private static int VOLUME = 0;

    public void makeMagic() {
        System.out.println("Редактирую страницу Депозиты юриков");
        try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL))) {
            List<Double[]> data = getLinesOfNumbers();
            XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
            XSSFSheet worksheetMKR = wbMKR.getSheetAt(12);

            XSSFCellStyle style = wbMKR.createCellStyle();
            style.setBorderRight(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setBorderBottom(BorderStyle.MEDIUM);

            int dif = 0;
            for (int r = 3; r < 28; r++) {
                for (int c = 1; c <= VOLUME; c++) {
                    if (r == 5 || r == 14) {
                        dif++;
                        r++;
                    }
                    if (worksheetMKR.getRow(r) == null) {
                        worksheetMKR.createRow(r);
                    }
                    if (worksheetMKR.getRow(r).getCell(c) == null) {
                        worksheetMKR.getRow(r).createCell(c);
                    }
                    worksheetMKR.getRow(r).getCell(c).setCellStyle(style);
                    worksheetMKR.getRow(r).getCell(c).setCellValue(data.get(r - 3 - dif)[c - 1]);
                }
            }
            FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
            wbMKR.write(mokruha);
            mokruha.close();
            System.out.println("Редактирование страницы Депозиты юриков завершено");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private List<Double[]> getLinesOfNumbers() throws IOException {
        List<Double> numbers = HtmlUtil.getNumbers(LINK);
        List<Double[]> linesOfNumbers = new ArrayList<>();
        VOLUME = numbers.size() / 23;
        for (int i = 0; i < 23; i++) {
            Double[] monthNum = new Double[VOLUME];
            for (int j = 0; j < VOLUME; j++) {
                monthNum[j] = numbers.get(j + VOLUME * i);
            }
            linesOfNumbers.add(monthNum);
        }
        return linesOfNumbers;
    }


}
