package CheckDownloadEdit.ExcelFiles;

import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.XlsxUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExtTrade {

    private static final String LINK = "http://www.customs.ru/attachments/article/24926/WEB_UTSA_04.xls";

    public void makeMagic() {
        File file = FilesUtil.downloadFile(LINK);
        if (file == null) {
            System.out.println("Файл для страницы Внешняя торговля отсутствует.");
        } else {
            copyCells(file);
        }
    }

    private void copyCells(File file) {
        //получаем файлы
        try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
             FileInputStream fileStream = new FileInputStream(file)) {
            //создаем стримы эксель файлов
            XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
            HSSFWorkbook wbExt = new HSSFWorkbook(fileStream);
            //страницы доходов
            XSSFSheet worksheetMKR = wbMKR.getSheetAt(10);
            HSSFSheet worksheetExt = wbExt.getSheetAt(0);
            //ячейки с числами
            Cell cellMKR;
            Cell cellExt;
            for (int r = 6; r < 17; r++) {
                for (int c = 1; c < 10; c++) {
                    if (worksheetExt.getRow(r + 40) == null) {
                        break;
                    } else if (worksheetExt.getRow(r + 40).getCell(c) == null) {
                        c++;
                    } else {
                        cellExt = worksheetExt.getRow(r + 40).getCell(c);
                        cellMKR = worksheetMKR.getRow(r).getCell(c);
                        XlsxUtil.fillCells(cellExt, cellMKR);
                    }
                }
            }
            FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
            wbMKR.write(mokruha);
            mokruha.close();
            System.out.println("Редактирование страницы Внешняя торговля завершено");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
