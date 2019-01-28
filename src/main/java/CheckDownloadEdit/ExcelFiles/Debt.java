package CheckDownloadEdit.ExcelFiles;

import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.XlsxUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Debt {

    private static final String LINK = "https://www.cbr.ru/statistics/credit_statistics/debt/schedule_debt.xlsx";

    /**
     * Содержимое файла полностью копируется с заменой существующей информации при различии заголовка.
     * Необходимо обновление источника в 19 году для корректного тестирования.
     */

    public void makeMagic() {
        File file = FilesUtil.downloadFile(LINK);
        if (file != null) {
            XlsxUtil.LOG.debug("Проверяю наличие данных для страницы Долг..");
//            System.out.println("Обновляю страницу Долг");
            //получаем файлы
            try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                 FileInputStream fileStream = new FileInputStream(file)) {
                //создаем стримы эксель файла
                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                XSSFWorkbook wbF = new XSSFWorkbook(fileStream);
                //страницы инфляции
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(5);
                XSSFSheet worksheetF = wbF.getSheetAt(0);
                //ячейки с числами
                Cell cellMKR = worksheetMKR.getRow(3).getCell(3);
                Cell cellF = worksheetF.getRow(3).getCell(3);
                XSSFCellStyle style = wbMKR.createCellStyle();
                //проверка совпадений
                if (cellMKR.getStringCellValue().equals(cellF.getStringCellValue())) {
                    XlsxUtil.LOG.info("Изменений страницы Долг не требуется");
//                    System.out.println("Изменений страницы Долг не требуется");
                } else {
                    for (int r = 1; r < 45; r++) {
                        for (int c = 0; c < 24; c++) {
                            if (worksheetF.getRow(r).getCell(c) != null) {
                                cellF = worksheetF.getRow(r).getCell(c);

                                if (worksheetMKR.getRow(r) == null) {
                                    cellMKR = worksheetMKR.createRow(r).createCell(c);
                                }
                                else {
                                    cellMKR = worksheetMKR.getRow(r).createCell(c);
                                }
                                style.cloneStyleFrom(cellF.getCellStyle());
                                cellMKR.setCellStyle(style);

                                XlsxUtil.fillCells(cellF, cellMKR);
                            }
                        }
                    }
                    FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                    wbMKR.write(mokruha);
                    mokruha.close();
                    XlsxUtil.LOG.debug("Обновление страницы Долг завершено.");
//                    System.out.println("Редактирование страницы Долг завершено");
                }
            } catch (IOException e) {
                XlsxUtil.LOG.error("Ошибка чтения-записи страницы Долг.");
                e.printStackTrace();
            }
            catch (NullPointerException x) {
                XlsxUtil.LOG.error("Проблемы с данными на странице Долг. Необходимы исправления.");
                x.printStackTrace();
            }
        }
    }

}
