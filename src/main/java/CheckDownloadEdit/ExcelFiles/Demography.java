package CheckDownloadEdit.ExcelFiles;

import CheckDownloadEdit.FilesUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class Demography {

    //мужчины и женщины
    private static final String LINK1 = "http://www.gks.ru/free_doc/new_site/population/demo/demo13.xls";
    //всё население
    private static final String LINK2 = "http://www.gks.ru/free_doc/new_site/population/demo/demo14.xls";

    public void makeMagic() {
        File menwomen = FilesUtil.downloadFile(LINK1);
        File people = FilesUtil.downloadFile(LINK2);
        if (menwomen != null && people != null) {
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
                //стиль
                XSSFCellStyle style = wbMKR.createCellStyle();
                style.setBorderRight(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderBottom(BorderStyle.THIN);
                //циклы
                for (int i = 0; i < 3; i++) {
                    cellMKR = worksheetMKR.getRow(FilesUtil.getYear()+10).createCell(i+1);
                    cellSX = worksheetSX.getRow(FilesUtil.getYear()+14).getCell(i+1);
                    cellMKR.setCellStyle(style);
                    cellMKR.setCellValue(cellSX.getNumericCellValue());
                }
                for (int i = 0; i < 17; i++) {
                    cellMKR = worksheetMKR.getRow(FilesUtil.getYear()+4).createCell(i+10);
                    cellPPL = worksheetPPL.getRow(i+6).getCell(FilesUtil.getYear()+4);
                    cellMKR.setCellStyle(style);
                    if ( cellPPL != null && cellPPL.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        cellMKR.setCellValue(cellPPL.getNumericCellValue());
                    }
                }
                for (int i = 0; i < 3; i++) {
                    cellMKR = worksheetMKR.getRow(FilesUtil.getYear()+4).createCell(28 +i*2);
                    cellPPL = worksheetPPL.getRow(i+24).getCell(FilesUtil.getYear()+4);
                    cellMKR.setCellStyle(style);
                    cellMKR.setCellValue(cellPPL.getNumericCellValue());
                }
                FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                wbMKR.write(mokruha);
                mokruha.close();
                System.out.println("Редактирование страницы Демография завершено");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}