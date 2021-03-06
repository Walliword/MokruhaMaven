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

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Wage {


    private static int[] months = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private static List<String> pdfs = PdfsUtil.getUrlStrings(months, PdfsUtil.getYear());
    private static int pdfSize = pdfs.size();

    public void makeMagic() {
        //ПОПРАВИТЬ
        if (pdfSize < PdfsUtil.getMonth() + 1) {
            PdfsUtil.LOG.info("No new info for Wage page is available");
        } else {
            PdfsUtil.LOG.debug("Updating Wage page..");
            try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL))) {
                List<Double> data = getNumbers(getRawLine());

                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(16);

                XSSFCellStyle style1 = wbMKR.createCellStyle();
                style1.setBorderLeft(BorderStyle.THIN);
                XSSFCellStyle style2 = wbMKR.createCellStyle();
                style2.setBorderRight(BorderStyle.THIN);

                int row
                        = 1 + (PdfsUtil.getYear() - 17) * 12 + PdfsUtil.getMonth();
//                = 17;
                Cell cellMKR;
                for (int c = 1; c <= 3; c++) {
                    cellMKR = worksheetMKR.getRow(row).createCell(c);
                    cellMKR.setCellValue(data.get(c - 1));
                    if (c == 1) {
                        cellMKR.setCellStyle(style1);
                    }
                    if (c == 3) {
                        cellMKR.setCellStyle(style2);
                    }
                }
                FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                wbMKR.write(mokruha);
                mokruha.close();
                PdfsUtil.LOG.debug("Update is completed.");
            } catch (IOException e) {
                PdfsUtil.LOG.error("Error happened.");
                e.printStackTrace();
            }
        }
    }

    private String getRawLine() {
        int stopWord = 0;
        String rawLine = "";
        boolean tableStarted = false;
        File file = FilesUtil.downloadFile(pdfs.get(pdfSize - 1));
//        System.out.println(file);
        try {
            assert file != null;
            PdfReader reader = new PdfReader(file.getAbsolutePath());
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, 3, strategy);
//                        System.out.println(text);
            int page = PdfsUtil.getPage(text, "УРОВЕНЬ ЖИЗНИ НАСЕЛЕНИЯ…");
//            System.out.println(page);
            for (int i = page + 2; i <= page + 3; i++) {
                TextExtractionStrategy pageStrategy = new SimpleTextExtractionStrategy();
                String tableText = PdfTextExtractor.getTextFromPage(reader, i, pageStrategy);
//                System.out.println(tableText);
                String[] lines = tableText.split("\n");
//                System.out.println();
                for (int j = 0; j < lines.length; j++) {
                    if (lines[j].contains("Таблица 3")) {
                        tableStarted = true;
                    }
//                    System.out.println(PdfsUtil.getMonthName(PdfsUtil.getMonth() + 1));
                    if (stopWord == 2) {
                        break;
                    }
                    if (lines[j].trim().startsWith(PdfsUtil.getMonthName(PdfsUtil.getMonth() + 1))
                            && !lines[j].contains("-")) {
//                        System.out.println(lines[j]);
                        if ((j + 1) < lines.length && lines[j + 1].endsWith(")")) {
//                            System.out.println(lines[j + 2]);
                            rawLine = (lines[j + 2]);
                            if (tableStarted)
                            stopWord++;
                        } else if (!lines[j].contains(".")) {
//                            System.out.println(lines[j]);
                            rawLine = (lines[j]);
                            if (tableStarted)
                            stopWord++;
                        }
                    }
                }
            }
        } catch (IOException e) {
            PdfsUtil.LOG.error("Error with getting data.");
            e.printStackTrace();
        }
        return rawLine;
    }

    private List<Double> getNumbers(String rawLine) {
        List<Double> numbers = new ArrayList<>();
//        System.out.println(rawLine);
        String s = rawLine.replaceAll("[\\s]{2,}", " ");
//        System.out.println(s);
        numbers.add(PdfsUtil.getNumber(s, 0));
        numbers.add(PdfsUtil.getNumber(s, 3));
        numbers.add(PdfsUtil.getNumber(s, 4));
//        System.out.println(numbers);
        return numbers;
    }
}
