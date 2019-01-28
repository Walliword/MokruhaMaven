package CheckDownloadEdit.ExcelFiles;

import CheckDownloadEdit.Util.FilesUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

import static java.lang.String.format;


public class Inflation {

    private static final String LINK = "http://www.gks.ru/free_doc/new_site/prices/potr/I_ipc.xlsx";
    private static final Logger LOG = LoggerFactory.getLogger(Inflation.class);

    /**
     * Данные считываюся из эксель-файла, целиком копируется информация из ячеек, начиная с 18 года.
     * На первой пустой ячейке запись прекращается
     */

    @SuppressWarnings("deprecated")
    public void makeMagic() {
        File file = FilesUtil.downloadFile(LINK);
        if (file != null) {
            LOG.debug("Обновляю страницу Инфляция..");
//            System.out.println("Обновляю страницу Инфляция");
            //получаем файлы
            try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                 FileInputStream fileStream = new FileInputStream(file)) {
                //создаем стримы эксель файла
                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                XSSFWorkbook wbF = new XSSFWorkbook(fileStream);
                //страницы инфляции
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(2);
                XSSFSheet worksheetF = wbF.getSheetAt(0);
                //ячейки с числами
                Cell cellMKR;
                Cell cellF;
                //цикл - сначала ячейки, потом внутри строки
                for (int c = 27; c < 38; c++) {
                    for (int r = 2; r < 14; r++) {
                            if ((worksheetF.getRow(r + 3).getCell(c)) != null
                                    && (worksheetF.getRow(r + 3).getCell(c).getCellType()) == Cell.CELL_TYPE_NUMERIC) {

                                if (worksheetMKR.getRow(r) == null) {
                                    worksheetMKR.createRow(r);
                                }

                                if (worksheetMKR.getRow(r).getCell(c) != null) {
                                    cellMKR = worksheetMKR.getRow(r).getCell(c);
                                } else {
                                    cellMKR = worksheetMKR.getRow(r).createCell(c);
                                }

                                cellF = worksheetF.getRow(r + 3).getCell(c);
                                cellMKR.setCellValue((cellF.getNumericCellValue() - 100) / 100);
                            }
                    }
                }
                FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                wbMKR.write(mokruha);
                mokruha.close();
                LOG.debug("Актуальные данные для страницы Инфляция добавлены.");
//                System.out.println("Актуальные данные для страницы Инфляция добавлены");

            } catch (IOException e) {
                LOG.error(format("Проблема с чтением и записью файлов для страницы Инфляция. %s", e.getMessage()));
                e.printStackTrace();
            }
        }
        else {
            LOG.info("Файла для страницы Инфляция не существует.");
//            System.out.println("Файла для страницы Инфляция не существует");
        }
    }

}
