package CheckDownloadEdit.PdfFiles;

import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.PdfsUtil;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Transport {

    private static int year;
    private static int month;

    static {
        LocalDate today = LocalDate.now();
        int m = today.getMonthValue();
        if (m == 1) {
            year = FilesUtil.getYear() - 1;
            month = 11;
        }
        else {
            year = FilesUtil.getYear();
            month = m - 2;
        }
    }

    private static int[] months = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private static List<String> pdfs = PdfsUtil.getUrlStrings(months, year);
    private static int pdfSize = pdfs.size();

    public void makeMagic() {
        if (pdfSize < month + 1) {
            System.out.println("Информации по Транспорту за прошедший месяц не поступало.");
        }
        else {
            try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL))) {
                List<Double> data = getNumbers(getRawLines());

                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(14);

                XSSFCellStyle style1 = wbMKR.createCellStyle();
                style1.setBorderRight(BorderStyle.THIN);
                style1.setBorderLeft(BorderStyle.THIN);
                XSSFCellStyle style2 = wbMKR.createCellStyle();
                style2.setBorderRight(BorderStyle.THIN);

                int row = 1 + (year - 17) * 12 + month;
                Cell cellMKR;
                for (int c = 1; c <= 7; c++) {
                    cellMKR = worksheetMKR.getRow(row).createCell(c);
                    cellMKR.setCellValue(data.get(c - 1));
                    if (c == 1) {
                        cellMKR.setCellStyle(style1);
                    }
                    if (c == 7) {
                        cellMKR.setCellStyle(style2);
                    }
                }
                FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                wbMKR.write(mokruha);
                mokruha.close();
                System.out.println("Редактирование страницы Транспорт завершено");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getRawLines() {
        List<String> rawLines = new ArrayList<>();
        File file = FilesUtil.downloadFile(pdfs.get(month));
        try {
            assert file != null;
            PdfReader reader = new PdfReader(file.getAbsolutePath());
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, 3, strategy);
            //System.out.println(text);
            int page = getPage(text);
            //System.out.println(page);
            for (int i = page; i <= page + 1; i++) {
                TextExtractionStrategy pageStrategy = new SimpleTextExtractionStrategy();
                String tableText = PdfTextExtractor.getTextFromPage(reader, i, pageStrategy);
                //System.out.println(tableText);
                String[] lines = tableText.split("\n");
                for (int j = 0; j < lines.length; j++) {
                    if (!lines[j].contains("-")) {
                        if (lines[j].contains("Грузооборот")) {
                            rawLines.add(lines[j]);
                        }
                        if (lines[j].contains("железнодорожного")) {
                            rawLines.add(lines[j]);
                        }
                        if (lines[j].contains("автомобильного")) {
                            rawLines.add(lines[j]);
                        }
                        if (lines[j].contains("морского")) {
                            rawLines.add(lines[j]);
                        }
                        if (lines[j].contains("внутреннего водного")) {
                            rawLines.add(lines[j]);
                        }
                        if (lines[j].contains("транспортная авиация")) {
                            rawLines.add(lines[j] + lines[j + 2]);
                        }
                        if (lines[j].contains("трубопроводного")) {
                            rawLines.add(lines[j]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawLines;
    }

    private List<Double> getNumbers(List<String> rawLines) {
        List<Double> numbers = new ArrayList<>();
        for (String s : rawLines) {
            if (pdfSize == 12) {
                numbers.add(PdfsUtil.getNumber(s, 2));
            } else {
                numbers.add(PdfsUtil.getNumber(s, 0));
            }
        }
        return numbers;
    }

    private int getPage(String contents) {
        String[] lines = contents.split("\n");
        String page = "";
        for (String line : lines) {
            if (line.contains("Транспорт…")) {
                page = line.substring(10).replaceAll("[A-Za-zА-Яа-я;.,…]", "").trim();
            }
        }
        return Integer.parseInt(page);
    }

}
