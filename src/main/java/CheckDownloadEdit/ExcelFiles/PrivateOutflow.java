package CheckDownloadEdit.ExcelFiles;

import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.XlsxUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PrivateOutflow {

    private static final String LINK = "https://www.cbr.ru/vfs/statistics/credit_statistics/bop/outflow.xlsx";
    private static final String[] months = new String[] {"январь", "февраль", "март", "апрель", "май", "июнь", "июль",
"август", "сентябрь", "октябрь", "ноябрь", "декабрь"};

    public void makeMagic() {
        File file = FilesUtil.downloadFile(LINK);
        if (file == null) {
            System.out.println("Файл для Вывоза капитала частным сектором отсутствует.");
        }
        else {
            System.out.println("Редактирую страницу Вывоза капитала");
            butchFile(file);
        }
    }

    private void butchFile(File file) {
        //получаем файлы
        try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
             FileInputStream fileStream = new FileInputStream(file)) {
//создаем стримы эксель файла
            XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
            XSSFWorkbook wbF = new XSSFWorkbook(fileStream);
            //страницы инфляции
            XSSFSheet worksheetMKR = wbMKR.getSheetAt(8); //Access the worksheet, so that we can update / modify it.
            XSSFSheet worksheetF = wbF.getSheetAt(0);
            //ячейки с числами
            Cell cellMKR;
            Cell cellF;
            XSSFCellStyle style = wbMKR.createCellStyle();
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.HAIR);

            int rowNum = 157 + 17*(FilesUtil.getYear()-17);
            //идем по всем потенциальным стркоам года
            for (int i = 0; i < 17; i++) {
                //если строка существует
                if (worksheetF.getRow(rowNum + i) != null) {
                    //бежим по массиву
                    for (int j = 0; j < 12; j++) {
                        if (worksheetF.getRow(rowNum + i).
                                getCell(0).getStringCellValue().contains(months[j])) {
                            for (int c = 0; c < 9; c++) {
                                cellF = worksheetF.getRow(rowNum + i).
                                        getCell(c);
                                int rownumMKR = 31 + 12 * (FilesUtil.getYear() - 18) + j;
                                if (worksheetMKR.getRow(rownumMKR) == null) {
                                    cellMKR = worksheetMKR.createRow(rownumMKR).createCell(c);
                                } else {
                                    cellMKR = worksheetMKR.getRow(rownumMKR).createCell(c);
                                }
                                cellMKR.setCellStyle(style);
                                XlsxUtil.fillCells(cellF, cellMKR);
                            }
                        }
                    }
                }
                else break;
            }
            FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
            wbMKR.write(mokruha);
            mokruha.close();
            System.out.println("Редактирование страницы Вывод капитала частным сектором завершено");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
