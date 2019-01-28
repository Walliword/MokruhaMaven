package CheckDownloadEdit.ExcelFiles;

import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.XlsxUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class Income {

    private static final String LINK = "http://www.gks.ru/free_doc/new_site/population/urov/urov_13kv.xls";

    /**
     * Данные - из одного эксель-файла. Данные копируются из каждой ячейки источника
     * за предыдущий год при их наличии. Пока неизвестно - появляются ли они за весь год, или поквартально.
     * Будет выяснено за 19 год.
     */

    @SuppressWarnings("deprecated")
    public void makeMagic() {
        File file = FilesUtil.downloadFile(LINK);
        if (file != null) {
            XlsxUtil.LOG.debug("Проверяю наличие данных для страницы Доходы..");
//            System.out.println("Обвновляю страницу Доходы населения");
            //получаем файлы
            try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                 FileInputStream fileStream = new FileInputStream(file)) {
                //создаем стримы эксель файлов
                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                HSSFWorkbook wbIN = new HSSFWorkbook(fileStream);
                //страницы доходов
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(4);
                HSSFSheet worksheetIN = wbIN.getSheetAt(0);
                //ячейки с числами
                Cell cellMKR;
                Cell cellIN;
                //стиль
                XSSFCellStyle style = XlsxUtil.getSquareStyle(wbMKR);

                //проверка наличия данных
                if ((cellIN = worksheetIN.getRow(52 + (FilesUtil.CURRENT_YEAR-16)*6).getCell(1)) != null &&
                        (cellIN.getCellType() == Cell.CELL_TYPE_STRING)) {
                    if ((cellMKR = worksheetMKR.getRow(51 + (FilesUtil.CURRENT_YEAR - 16) * 6).getCell(1)) != null &&
                            cellIN.getStringCellValue().equals(cellMKR.getStringCellValue())) {
                        XlsxUtil.LOG.info("Данные по доходам за текущий год уже добавлены");
//                        System.out.println("Данные по доходам за текущий год уже добавлены");
                    }
                    else {
                        XlsxUtil.LOG.debug("Обновляю страницу Доходы..");
                        //год
                        cellMKR = worksheetMKR.getRow(51 + (FilesUtil.CURRENT_YEAR - 16) * 6).createCell(1);
                        cellMKR.setCellValue(cellIN.getStringCellValue());
                        //рабочий цикл
                        for (int r = 0; r < 5; r++) {
                            for (int c = 0; c < 7; c++) {
                                if (worksheetIN.getRow(52 + (FilesUtil.CURRENT_YEAR - 16) * 6 + 1 + r) != null &&
                                        worksheetIN.getRow(52 + (FilesUtil.CURRENT_YEAR - 16) * 6 + 1 + r).getCell(1 + c) != null) {

                                    cellMKR = worksheetMKR.getRow(51 + (FilesUtil.CURRENT_YEAR - 16) * 6 + 1 + r).createCell(1 + c);
                                    cellIN = worksheetIN.getRow(52 + (FilesUtil.CURRENT_YEAR - 16) * 6 + 1 + r).getCell(1 + c);
                                    cellMKR.setCellStyle(style);
                                    if (cellIN.getCellType() == Cell.CELL_TYPE_STRING) {
                                        cellMKR.setCellValue(cellIN.getStringCellValue());
                                    }
                                    if (cellIN.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        cellMKR.setCellValue(cellIN.getNumericCellValue());
                                    }
                                }
                                else {
                                    break;
                                }
                            }
                        }
                        FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                        wbMKR.write(mokruha);
                        mokruha.close();
                        XlsxUtil.LOG.debug("Редактирование страницы Доходы население завершено.");
//                        System.out.println("Редактирование страницы Доходы население завершено");
                    }
                }
                else {
                    XlsxUtil.LOG.info("Данных по доходам населения за предыдущий год ещё не поступило.");
//                    System.out.println("Данных по доходам населения за предыдущий год ещё не поступило");
                }
            } catch (IOException e) {
                XlsxUtil.LOG.error("Ошибка чтения-записи страницы Доходы.");
                e.printStackTrace();
            }
            catch (NullPointerException x) {
                XlsxUtil.LOG.error("Проблемы с данными на странице Доходы. Необходимы исправления.");
                x.printStackTrace();
            }
        }
    }
}
