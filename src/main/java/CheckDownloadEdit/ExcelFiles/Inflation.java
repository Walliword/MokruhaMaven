package CheckDownloadEdit.ExcelFiles;

import CheckDownloadEdit.FilesUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;


public class Inflation {

    private static final String LINK = "http://www.gks.ru/free_doc/new_site/prices/potr/I_ipc.xlsx";


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
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(2); //Access the worksheet, so that we can update / modify it.
                XSSFSheet worksheetF = wbF.getSheetAt(0);
                //ячейки с числами
                Cell cellMKR = null; // declare a Cell object
                Cell cellF = null;
                //листы для тестов
                ArrayList<Double> listD = new ArrayList<>();
                ArrayList<String> listS = new ArrayList<>();
                //цикл - сначала ячейки, потом внутри строки
                for (int c = 27; c < 38; c++) {
                    for (int r = 2; r < 14; r++) {
                        if ((worksheetMKR.getRow(r).getCell(c)) != null &&
                                (worksheetMKR.getRow(r).getCell(c).getCellType()) != Cell.CELL_TYPE_NUMERIC) {
                            if ((worksheetF.getRow(r + 3).getCell(c)) != null
                                    && (worksheetF.getRow(r + 3).getCell(c).getCellType()) == Cell.CELL_TYPE_NUMERIC) {

                                if (worksheetMKR.getRow(r).getCell(c) != null) {
                                    cellMKR = worksheetMKR.getRow(r).getCell(c);
                                } else {
                                    cellMKR = worksheetMKR.getRow(r).createCell(c);
                                }

                                cellF = worksheetF.getRow(r + 3).getCell(c);
//                                switch (cellMKR.getCellType()) {
//                                    case Cell.CELL_TYPE_STRING:
//                                        //System.out.print(cellMKR.getStringCellValue() + "\t");
//                                        listS.add(cellMKR.getStringCellValue());
//                                        break;
//                                    case Cell.CELL_TYPE_NUMERIC:
//                                        //System.out.print(cellMKR.getNumericCellValue() + "\t");
//                                        listD.add(cellMKR.getNumericCellValue());
//                                        break;
//                                    case Cell.CELL_TYPE_BOOLEAN:
//                                        System.out.print(cellMKR.getBooleanCellValue() + "\t");
//                                        break;
//                                    default:
//                                        System.out.println("unknown cell type");
//                                }

//                                cellMKR = worksheetMKR.getRow(r).createCell(c);
                                cellMKR.setCellValue((cellF.getNumericCellValue() - 100) / 100);
//                                cellMKR.setCellStyle(cellF.getCellStyle());
                            }
                        }
                    }
                }
                FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                wbMKR.write(mokruha);
                mokruha.close();
                System.out.println("Редактирование страницы Инфляция завершено");
                listD.forEach(System.out::println);
                //listS.forEach(System.out::println);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
