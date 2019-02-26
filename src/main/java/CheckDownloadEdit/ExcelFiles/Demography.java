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

import static java.lang.String.format;

public class Demography {

    //мужчины и женщины
    private static final String LINK1 = "http://www.gks.ru/free_doc/new_site/population/demo/demo13.xls";
    //всё население
    private static final String LINK2 = "http://www.gks.ru/free_doc/new_site/population/demo/demo14.xls";

    /**
     * Данные получаются из двух эксель-файлов, обновляющихся в течение года.
     * В случае наличия в них данных за текущий год проверяется - были ли они уже записаны в файл ранее.
     * Если нет - проводится запись.
     */

    public void makeMagic() {
        File menwomen = FilesUtil.downloadFile(LINK1);
        File people = FilesUtil.downloadFile(LINK2);
        if (menwomen != null && people != null) {
            XlsxUtil.LOG.debug("Checking Demography data for this year..");
//            System.out.println("Обовляю страницу Демография");
            //получаем файлы
            try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                 FileInputStream sexes = new FileInputStream(menwomen);
                 FileInputStream pplStream = new FileInputStream(people)) {

                //создаем стримы эксель файлов
                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                HSSFWorkbook wbSX = new HSSFWorkbook(sexes);
                HSSFWorkbook wbPPL = new HSSFWorkbook(pplStream);
                //страницы демографии
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(3); //Access the worksheet, so that we can update / modify it.
                HSSFSheet worksheetSX = wbSX.getSheetAt(0);
                HSSFSheet worksheetPPL = wbPPL.getSheetAt(0);
                //ячейки с числами
                Cell cellMKR; // declare a Cell object
                Cell cellSX;
                Cell cellPPL;
                //наличие значений всего населения в текущем году
                if ((cellSX = worksheetSX.getRow(FilesUtil.CURRENT_YEAR + 14).getCell(1)) == null
                        && worksheetPPL.getRow(6).getCell(FilesUtil.CURRENT_YEAR + 4) == null) {
                    XlsxUtil.LOG.info("No Demography data for this year available.");
//                    System.out.println("Данные по демографии за текущий год отсутствуют");
                } else {
                    assert cellSX != null;
                    if ((cellMKR = worksheetMKR.getRow(FilesUtil.CURRENT_YEAR + 10).getCell(1)) != null &&
                    cellMKR.getNumericCellValue() == cellSX.getNumericCellValue()) {
                        XlsxUtil.LOG.info("Demography data for this year already added.");
//                        System.out.println("значения по демографии за текущий год уже добавлены");
                    }
                    else {
                        XlsxUtil.LOG.debug("Updating Demography..");
                        //стиль
                        XSSFCellStyle style = XlsxUtil.getSquareStyle(wbMKR);
                        //циклы
                        //первый файл
                        for (int i = 0; i < 3; i++) {
                            cellMKR = worksheetMKR.getRow(FilesUtil.CURRENT_YEAR + 10).createCell(i + 1);
                            cellSX = worksheetSX.getRow(FilesUtil.CURRENT_YEAR + 14).getCell(i + 1);
                            cellMKR.setCellStyle(style);
                            cellMKR.setCellValue(cellSX.getNumericCellValue());
                        }
                        //второй файл
                        for (int i = 0; i < 17; i++) {
                            cellMKR = worksheetMKR.getRow(FilesUtil.CURRENT_YEAR + 4).createCell(i + 10);
                            cellPPL = worksheetPPL.getRow(i + 6).getCell(FilesUtil.CURRENT_YEAR + 4);
                            cellMKR.setCellStyle(style);
                            if (cellPPL != null && cellPPL.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                cellMKR.setCellValue(cellPPL.getNumericCellValue());
                            }
                        }
                        //конец второй таблицы
                        for (int i = 0; i < 3; i++) {
                            cellMKR = worksheetMKR.getRow(FilesUtil.CURRENT_YEAR + 4).createCell(28 + i * 2);
                            cellPPL = worksheetPPL.getRow(i + 24).getCell(FilesUtil.CURRENT_YEAR + 4);
                            cellMKR.setCellStyle(style);
                            cellMKR.setCellValue(cellPPL.getNumericCellValue());
                        }
                        FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                        wbMKR.write(mokruha);
                        mokruha.close();
                        XlsxUtil.LOG.debug("Demography page updated.");
//                        System.out.println("Редактирование страницы Демография завершено");
                    }
                }
            } catch (IOException e) {
                XlsxUtil.LOG.error(format("Problem at Demography page. %s", e.getMessage()));
                e.printStackTrace();
            }
        }
    }
}