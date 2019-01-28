package CheckDownloadEdit.ExcelFiles;

import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.XlsxUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PayBalance {

    private static final String LINK = "https://www.cbr.ru/vfs/statistics/credit_statistics/bop/bop_est.xlsx";

    public void makeMagic() {
        File file = FilesUtil.downloadFile(LINK);
        if (file == null) {
            XlsxUtil.LOG.info("Файл для страницы Платежный баланс отсутствует.");
//            System.out.println("Файл для страницы Платежный баланс отсутствует.");
        } else {
            XlsxUtil.LOG.debug("Редактирую страницу Платежный баланс..");
//            System.out.println("Редактирую страницу Платежный баланс");
            copyCells(file);
        }
    }

    private void copyCells(File file) {
        //получаем файлы
        try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
             FileInputStream fileStream = new FileInputStream(file)) {
//создаем стримы эксель файла
            XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
            XSSFWorkbook wbF = new XSSFWorkbook(fileStream);
            //страницы инфляции
            XSSFSheet worksheetMKR = wbMKR.getSheetAt(9); //Access the worksheet, so that we can update / modify it.
            XSSFSheet worksheetF = wbF.getSheetAt(0);
            Cell cellMKR = worksheetMKR.getRow(0).getCell(0);
            Cell cellF = worksheetF.getRow(0).getCell(0);
            //проверка совпадений
            if (cellMKR.getStringCellValue().equals(cellF.getStringCellValue())) {
                XlsxUtil.LOG.info("Изменений страницы Платёжный баланс не требуется.");
//                System.out.println("Изменений страницы Платёжный баланс не требуется");
            } else {
                cellMKR.setCellValue(cellF.getStringCellValue());
                for (int r = 1; r < 79; r++) {
                    for (int c = 1; c < 12; c++) {
                        if (r == 78) {
                            r++;
                        }
                        if ((cellF = worksheetF.getRow(r+2).getCell(c)) == null ||
                        worksheetF.getRow(r+2) == null ||
                        worksheetMKR.getRow(r) == null) {
                            break;
                        }
                        else {
                            if (worksheetMKR.getRow(r).getCell(c) == null) {
                                cellMKR = worksheetMKR.getRow(r).createCell(c);
                            }
                            else {
                                cellMKR = worksheetMKR.getRow(r).getCell(c);
                            }
                            XlsxUtil.fillCells(cellF, cellMKR);
                        }
                    }
                }
                FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                wbMKR.write(mokruha);
                mokruha.close();
                XlsxUtil.LOG.debug("Редактирование страницы Платёжный баланс завершено.");
//                System.out.println("Редактирование страницы Платёжный баланс завершено");
            }

        } catch (IOException e) {
            XlsxUtil.LOG.error("Ошибка чтения-записи на странице Платёжный баланс.");
            e.printStackTrace();
        }
    }

}
