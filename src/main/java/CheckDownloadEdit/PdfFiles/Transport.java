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
import java.util.ArrayList;
import java.util.List;

public class Transport {

    /**
     * Данные по месяцам появляются в конце следующего месяца после нужного. Данные за декабрь
     * появляются в январе следующего, поэтому необходимо переключение на прошлый год и соответствующий месяц
     * в утильном классе.
     */

    private static int[] months = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private static List<String> pdfs = PdfsUtil.getUrlStrings(months, PdfsUtil.getYear());
    private static int pdfSize = pdfs.size();

    public void makeMagic() {
        if (pdfSize < PdfsUtil.getMonth() + 1) {
            PdfsUtil.LOG.info("No new data for Transport page is available.");
        }
        else {
            PdfsUtil.LOG.debug("Updating Transport page..");
            try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL))) {
                List<Double> data = getNumbers(getRawLines());

                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(14);

                XSSFCellStyle style1 = wbMKR.createCellStyle();
                style1.setBorderRight(BorderStyle.THIN);
                style1.setBorderLeft(BorderStyle.THIN);
                XSSFCellStyle style2 = wbMKR.createCellStyle();
                style2.setBorderRight(BorderStyle.THIN);

                // Здесь модифицируется номер строки для заполнения в ручном режиме
                int row = 1 + (PdfsUtil.getYear() - 17) * 12 + PdfsUtil.getMonth();

                Cell cellMKR;
                for (int c = 1; c <= 7; c++) {
                    if (worksheetMKR.getRow(row) == null) {
                        worksheetMKR.createRow(row);
                    }
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
                PdfsUtil.LOG.debug("Update is completed.");
            } catch (IOException e) {
                PdfsUtil.LOG.error("Error happened.");
                e.printStackTrace();
            }
        }
    }

    private List<String> getRawLines() {
        List<String> rawLines = new ArrayList<>();

        //Эта строка отвечает за выбор месяца из доступного списка - ДЛЯ РУЧНОГО ЗАПОЛНЕНИЯ
        File file = FilesUtil.downloadFile(pdfs.get(PdfsUtil.getMonth()));

        try {
            assert file != null;
            PdfReader reader = new PdfReader(file.getAbsolutePath());
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, 3, strategy);
            int page = PdfsUtil.getPage(text, "Транспорт…");
            for (int i = page; i <= page + 1; i++) {
                TextExtractionStrategy pageStrategy = new SimpleTextExtractionStrategy();
                String tableText = PdfTextExtractor.getTextFromPage(reader, i, pageStrategy);
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
            PdfsUtil.LOG.error("Problem with getting data.");
            e.printStackTrace();
        }
//        System.out.println(rawLines);
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
//        System.out.println(numbers);
        return numbers;
    }
}
