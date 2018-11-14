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

    public void makeMagic() {
        File file = FilesUtil.downloadFile(LINK);
        if (file != null) {
            //получаем файлы
            try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                 FileInputStream fileStream = new FileInputStream(file)) {
                //создаем стримы эксель файла
                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                XSSFWorkbook wbF = new XSSFWorkbook(fileStream);
                //страницы инфляции
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(5); //Access the worksheet, so that we can update / modify it.
                XSSFSheet worksheetF = wbF.getSheetAt(0);
                //ячейки с числами
                Cell cellMKR = worksheetMKR.getRow(3).getCell(3);
                Cell cellF = worksheetF.getRow(3).getCell(3);
                XSSFCellStyle style = wbMKR.createCellStyle();
                //проверка совпадений
                if (cellMKR.getStringCellValue().equals(cellF.getStringCellValue())) {
                    System.out.println("Изменений страницы Долг не требуется");
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
                    System.out.println("Редактирование страницы Долг завершено");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
