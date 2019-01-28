package CheckDownloadEdit.HtmlFiles;

import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.HtmlUtil;
import CheckDownloadEdit.Util.XlsxUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Credits {

    private String LINK_CURRENT = "https://www.cbr.ru/statistics/print.aspx?file=bank_system/4-3-1_" +
            FilesUtil.CURRENT_YEAR +
            ".htm&pid=pdko_sub&sid=dopk";
    private String LINK_PREV = "https://www.cbr.ru/statistics/print.aspx?file=bank_system/4-3-1_" +
            (FilesUtil.CURRENT_YEAR -1) +
            ".htm&pid=pdko_sub&sid=dopk";

    public void makeMagic() {
        HtmlUtil.LOG.debug("Редактирую страницу Кредиты..");
        try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL))) {
            List<Double[]> data = HtmlUtil.getLinesOfNumbers(LINK_CURRENT, LINK_PREV, 20);
            if (data != null) {
                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(13);

                XSSFCellStyle style = XlsxUtil.getSquareFatStyle(wbMKR);

                int dif = 0;
                int move = HtmlUtil.getMove();

                for (int r = 3; r < 27; r++) {
                    for (int c = 1; c <= HtmlUtil.VOLUME; c++) {
                        if (r == 4 || r == 7 || r == 16 || r == 19) {
                            dif++;
                            r++;
                        }
                        if (worksheetMKR.getRow(r) == null) {
                            worksheetMKR.createRow(r);
                        }
                        if (worksheetMKR.getRow(r).getCell(c+move) == null) {
                            worksheetMKR.getRow(r).createCell(c+move);
                        }
                        worksheetMKR.getRow(r).getCell(c+move).setCellStyle(style);
                        worksheetMKR.getRow(r).getCell(c+move).setCellValue(data.get(r - 3 - dif)[c - 1]);
                    }
                }
                FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                wbMKR.write(mokruha);
                mokruha.close();
                HtmlUtil.LOG.debug("Редактирование страницы Кредиты завершено.");
            }
            else {
                HtmlUtil.LOG.info("Данные для страницы Кредиты отсутствуют.");
            }
        } catch (IOException e) {
            HtmlUtil.LOG.error("Ошибка при редактировании страницы Кредиты.");
            e.printStackTrace();
        }
    }
}
