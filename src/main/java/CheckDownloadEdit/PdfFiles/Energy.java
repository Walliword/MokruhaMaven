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

public class Energy {


    private static int[] months = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private static List<String> pdfs = PdfsUtil.getUrlStrings(months, PdfsUtil.getYear());
    private static int pdfSize = pdfs.size();

    public void makeMagic() {
        if (pdfSize < PdfsUtil.getMonth() + 1) {
            System.out.println("Информации по Энергии за " +
                    PdfsUtil.getMonthName(PdfsUtil.getMonth() + 1) +
                    " не поступало.");
        } else {
            try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL))) {
                List<Double> data = getNumbers(getRawLines());

                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(15);

                XSSFCellStyle style1 = wbMKR.createCellStyle();
                style1.setBorderLeft(BorderStyle.THIN);
                XSSFCellStyle style2 = wbMKR.createCellStyle();
                style2.setBorderRight(BorderStyle.THIN);

                int row
                        = 1 + (PdfsUtil.getYear() - 17) * 12 + PdfsUtil.getMonth();
//                = 17;
                Cell cellMKR;
                for (int c = 1; c <= 8; c++) {
                    cellMKR = worksheetMKR.getRow(row).createCell(c);
                    cellMKR.setCellValue(data.get(c - 1));
                    if (c == 1 || c == 5) {
                        cellMKR.setCellStyle(style1);
                    }
                    if (c == 8) {
                        cellMKR.setCellStyle(style2);
                    }
                }
                FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                wbMKR.write(mokruha);
                mokruha.close();
                System.out.println("Редактирование страницы Энергия завершено");
            } catch (IOException e) {
                e.printStackTrace();
            }
//        getRawLines();
        }
    }

    private List<String> getRawLines() {
        List<String> rawLines = new ArrayList<>();
        File file = FilesUtil.downloadFile(pdfs.get(pdfSize - 1));
        try {
            assert file != null;
            PdfReader reader = new PdfReader(file.getAbsolutePath());
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, 3, strategy);
//            System.out.println(text);
            int page = PdfsUtil.getPage(text, "кондиционирование воздуха…");
            System.out.println(page);
            for (int i = page; i <= page + 2; i++) {
                TextExtractionStrategy pageStrategy = new SimpleTextExtractionStrategy();
                String tableText = PdfTextExtractor.getTextFromPage(reader, i, pageStrategy);
//                System.out.println(tableText);
                String[] lines = tableText.split("\n");
                for (int j = 0; j < lines.length; j++) {
                    if (!lines[j].contains("-") && !lines[j].contains("%")) {
                        if (lines[j].contains("Электроэнергия,")) {
                            rawLines.add(lines[j].substring(24));
                            System.out.println(lines[j]);
                        }
                        if (lines[j].contains("атомными")) {
                            if (!lines[j].contains("атомными э")) {
                                rawLines.add(lines[j]);
                                System.out.println(lines[j]);
                            }
                        }
                        if (lines[j].contains("тепловыми")) {
                            if (!lines[j].contains("тепловыми э")) {
                                rawLines.add(lines[j]);
                                System.out.println(lines[j]);
                            }
                        }
                        if (lines[j].contains("гидроэлектростанциями")) {
                            rawLines.add(lines[j]);
                            System.out.println(lines[j]);
                        }
                        if (lines[j].contains("Пар и горячая вода,")) {
                            rawLines.add(lines[j].substring(24));
                            System.out.println(lines[j]);
                        }
                        if (lines[j].contains(" электростанциями ")) {
                            rawLines.add(lines[j]);
                            System.out.println(lines[j]);
                        }
                        if (lines[j].contains("котельными")) {
                            rawLines.add(lines[j]);
                            System.out.println(lines[j]);
                        }
                        if (lines[j].contains("промышленными утилизационными установками")) {
                            rawLines.add(lines[j]);
                            System.out.println(lines[j]);
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
            //ИСПРАВИТЬ
//            System.out.println(s);
            s = s.replaceAll("[\\s]{2,}", " ");
            if (pdfSize == 12) {
                numbers.add(PdfsUtil.getNumber(s, 2));
            } else {
                numbers.add(PdfsUtil.getNumber(s, 0));
            }
        }
        return numbers;
    }
}
