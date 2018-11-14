package CheckDownloadEdit.ExcelFiles;

import CheckDownloadEdit.Util.FilesUtil;
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

    public void makeMagic() {
        File file = FilesUtil.downloadFile(LINK);
        if (file != null) {
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
                XSSFCellStyle style = wbMKR.createCellStyle();
                style.setBorderRight(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderBottom(BorderStyle.THIN);

                //проверка наличия данных
                if ((cellIN = worksheetIN.getRow(51 + (FilesUtil.getYear()-16)*6).getCell(1)) != null &&
                        (cellIN.getCellType() == Cell.CELL_TYPE_STRING)) {
                    if ((cellMKR = worksheetMKR.getRow(51 + (FilesUtil.getYear() - 16) * 6).getCell(1)) != null &&
                            cellIN.getStringCellValue().equals(cellMKR.getStringCellValue())) {
                        System.out.println("Данные по доходам за текущий год уже добавлены");
                    }
                    else {
                        //год
                        cellMKR = worksheetMKR.getRow(51 + (FilesUtil.getYear() - 16) * 6).createCell(1);
                        cellMKR.setCellValue(cellIN.getStringCellValue());
                        //рабочий цикл
                        for (int r = 0; r < 5; r++) {
                            for (int c = 0; c < 7; c++) {
                                cellMKR = worksheetMKR.getRow(51 + (FilesUtil.getYear() - 16) * 6 + 1 + r).createCell(1 + c);
                                cellIN = worksheetIN.getRow(51 + (FilesUtil.getYear() - 16) * 6 + 1 + r).getCell(1 + c);
                                cellMKR.setCellStyle(style);
                                if (cellIN.getCellType() == Cell.CELL_TYPE_STRING) {
                                    cellMKR.setCellValue(cellIN.getStringCellValue());
                                }
                                if (cellIN.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    cellMKR.setCellValue(cellIN.getNumericCellValue());
                                }
                            }
                        }
                        FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                        wbMKR.write(mokruha);
                        mokruha.close();
                        System.out.println("Редактирование страницы Доходы завершено");
                    }
                }
                else {
                    System.out.println("Данных по доходам за предыдущий год ещё не поступило");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
