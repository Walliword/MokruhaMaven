package CheckDownloadEdit.MixedFiles;


import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.HtmlUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class FNB {

    private static final int year = FilesUtil.getYear();
    private static final int month = LocalDate.now().getMonthValue();
    private static final int rowNum = 12 * (year - 18) + month - 1;
    private static final String LINK1 = "https://www.minfin.ru/common/upload/library/20" +
            year +
            "/" +
            month +
            "/main/Obem_sredstv_Fonda_natsionalnogo_blagosostoyaniya_01_" +
            month +
            "_20" +
            year +
            ".docx";
    private static final String LINK2 = "https://www.cbr.ru/hd_base/mrrf/mrrf_m/";
    private static final String LINK3 = "http://www.cbr.ru/vfs/statistics/ms/ms_m21.xlsx";


    public void makeMagic() {
        try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL))) {
            String[] docxData = getDocxInfo();
            List<String[]> htmlData = getHtmlInfo();
            List<Cell> excelData = getExcelInfo();
            if (docxData == null && htmlData.isEmpty() && excelData.isEmpty()) {
                System.out.println("Нет данных для ФНБ/М2/ЗВР");
            } else {
                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(7);

                XSSFCellStyle style = wbMKR.createCellStyle();
                style.setBorderRight(BorderStyle.MEDIUM);
                style.setBorderLeft(BorderStyle.MEDIUM);
                style.setBorderTop(BorderStyle.MEDIUM);
                style.setBorderBottom(BorderStyle.MEDIUM);

                if (docxData != null) {
                    for (int i = 0; i < docxData.length; i++) {
                        String[] docxLine = docxData[i].split("\t");
                        for (int j = 0; j < docxLine.length; j++) {
                            worksheetMKR.getRow(i + 1).createCell(j).setCellValue(docxLine[j]);
                            worksheetMKR.getRow(i + 1).getCell(j).setCellStyle(style);
                        }
                    }
                }

                if (!htmlData.isEmpty()) {
                    for (int i = 0; i < 100; i++) {
                        String[] htmlLine = htmlData.get(i);
                        worksheetMKR.getRow(i + 1).createCell(6).setCellValue(htmlLine[0]);
                        worksheetMKR.getRow(i + 1).getCell(6).setCellStyle(style);
                        worksheetMKR.getRow(i + 1).createCell(7).setCellValue(htmlLine[1]);
                        worksheetMKR.getRow(i + 1).getCell(7).setCellStyle(style);
                        worksheetMKR.getRow(i + 1).createCell(8).setCellValue(htmlLine[2]);
                        worksheetMKR.getRow(i + 1).getCell(8).setCellStyle(style);
                        worksheetMKR.getRow(i + 1).createCell(9).setCellValue(htmlLine[6]);
                        worksheetMKR.getRow(i + 1).getCell(9).setCellStyle(style);
                    }
                }

                if (!excelData.isEmpty()) {
                    worksheetMKR.createRow(121 + rowNum);
                    XSSFCellStyle styleDate = wbMKR.createCellStyle();
                    styleDate.setDataFormat((short) 14);
                    worksheetMKR.getRow(121 + rowNum).createCell(12)
                            .setCellValue(excelData.get(0).getDateCellValue());
                    worksheetMKR.getRow(121 + rowNum).getCell(12).setCellStyle(styleDate);
                    worksheetMKR.getRow(121 + rowNum).getCell(12).setCellStyle(style);
                    for (int i = 1; i < excelData.size(); i++) {
                        worksheetMKR.getRow(121 + rowNum).createCell(12 + i)
                                .setCellValue(excelData.get(i).getNumericCellValue());
                        worksheetMKR.getRow(121 + rowNum).getCell(12 + i).setCellStyle(style);
                    }
                }

                FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                wbMKR.write(mokruha);
                mokruha.close();
                System.out.println("Редактирование страницы ФНБ/М2/ЗВР завершено");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] getDocxInfo() throws IOException {
        File file = FilesUtil.downloadFile(LINK1);
        if (file == null) {
            System.out.println("Данного docx файла для страницы ФНБ/М2/ЗВР не существует");
            return null;
        } else {
            XWPFDocument docx = new XWPFDocument(new FileInputStream(file));
            //using XWPFWordExtractor Class
            XWPFWordExtractor we = new XWPFWordExtractor(docx);
            String[] lines = we.getText().split("\n");
            String[] neededLines = new String[80];
            System.arraycopy(lines, 5, neededLines, 0, 80);
            return neededLines;
        }
    }


    private static List<String[]> getHtmlInfo() throws IOException {
        Elements links = HtmlUtil.getElements(LINK2);
        List<String[]> list = new LinkedList<>();
        for (Element link : links) {
            String[] line = new String[7];
            String[] raw = link.text().split(" ");
            if (raw.length > 12) {
                line[0] = raw[0];
                line[1] = raw[1] + raw[2];
                line[2] = raw[3] + raw[4];
                line[3] = raw[5] + raw[6];
                line[4] = raw[7] + raw[8];
                line[5] = raw[9] + raw[10];
                line[6] = raw[11] + raw[12];
                list.add(line);
            }
        }
        return list;
    }

    private static List<Cell> getExcelInfo() {
        File file = FilesUtil.downloadFile(LINK3);
        List<Cell> cells = new ArrayList<>();
        if (file == null) {
            System.out.println("данного xlsx файла для страницы ФНБ/М2/ЗВР не существует");
        } else {
            try (FileInputStream fileStream = new FileInputStream(file)) {
                XSSFWorkbook wbF = new XSSFWorkbook(fileStream);
                XSSFSheet worksheetF = wbF.getSheetAt(0);
                if ((worksheetF.getRow(305 + rowNum)) == null) {
                    System.out.println("Данных xlsx файла для страницы ФНБ/М2/ЗВР за текущий месяц не поступило");
                } else {
                    cells.add(worksheetF.getRow(305 + rowNum).getCell(0));
                    cells.add(worksheetF.getRow(305 + rowNum).getCell(1));
                    cells.add(worksheetF.getRow(305 + rowNum).getCell(2));
                    cells.add(worksheetF.getRow(305 + rowNum).getCell(3));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cells;
    }
}
