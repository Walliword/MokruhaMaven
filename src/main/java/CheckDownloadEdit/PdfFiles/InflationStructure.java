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

public class InflationStructure {


    private static int[] months = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private static List<String> pdfs = PdfsUtil.getUrlStrings(months, PdfsUtil.getYear());
    private static int pdfSize = pdfs.size();

    public void makeMagic() {
        if (pdfSize < PdfsUtil.getMonth() + 1) {
            System.out.println("Информации по Структуре Инфляции за " +
                    PdfsUtil.getMonthName(PdfsUtil.getMonth() + 1) +
                    " не поступало.");
        } else {
            System.out.println("Обновляю страницу Структура инфляции");
            try (FileInputStream mokruhaStream = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL))) {
                List<Double> data = getNumbers(getRawLines());

                XSSFWorkbook wbMKR = new XSSFWorkbook(mokruhaStream);
                XSSFSheet worksheetMKR = wbMKR.getSheetAt(17);

                XSSFCellStyle style1 = wbMKR.createCellStyle();
                style1.setBorderLeft(BorderStyle.THIN);
                XSSFCellStyle style2 = wbMKR.createCellStyle();
                style2.setBorderRight(BorderStyle.THIN);

                int c
                        = 1 + (PdfsUtil.getYear() - 17) * 12 + PdfsUtil.getMonth();
//                = 17;
                Cell cellMKR;
                for (int r = 1; r <= 36; r++) {
                    cellMKR = worksheetMKR.getRow(r).createCell(c);
                    cellMKR.setCellValue(data.get(r - 1));
                    cellMKR.setCellStyle(style1);
                }
                FileOutputStream mokruha = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                wbMKR.write(mokruha);
                mokruha.close();
                System.out.println("Редактирование страницы Структура Инфляции завершено");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
//                                    System.out.println(text);
            int page = PdfsUtil.getPage(text, "Потребительские цены…");
//            System.out.println(page);
            int num = 0;
            for (int i = page + 1; i <= page + 5; i++) {
                TextExtractionStrategy pageStrategy = new SimpleTextExtractionStrategy();
                String tableText = PdfTextExtractor.getTextFromPage(reader, i, pageStrategy);
//                System.out.println(tableText);
                String[] lines = tableText.split("\n");

                for (int j = 0; j < lines.length; j++) {
                    if (!lines[j].contains("- ") && !lines[j].contains("(") && !lines[j].contains("%")
                            && !lines[j].contains("куриные") && !lines[j].endsWith("-") && !lines[j].trim().endsWith(",")) {
                        if (lines[j].contains("без алкогольных напитков")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("хлебобулочные изделия")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("крупа и бобовые")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("макаронные изделия")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("мясо и птица")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("рыба и морепродукты")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("молоко и молочная продукция")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("масло сливочное")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("масло подсолнечное")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("яйца")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("сахар-песок")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("плодоовощная продукция")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Алкогольные напитки ")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Ткани")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Одежда и белье")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Трикотажные изделия")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Обувь")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Моющие и чистящие средства")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Табачные изделия")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("бытовые приборы")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Телерадиотовары")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Строительные материалы")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Бензин автомобильный")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Медикаменты")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Жилищно-коммунальные услуги")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Медицинские услуги")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Услуги пассажирского транспорта")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Услуги связи")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Услуги организаций культуры")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Санаторно-оздоровительные услуги")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Услуги дошкольного воспитания")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Услуги образования")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Бытовые услуги")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Услуги зарубежного туризма")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Услуги физкультуры и спорта")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
                        }
                        if (lines[j].contains("Услуги страхования")) {
                            rawLines.add(lines[j]);
//                            System.out.println(lines[j]);
//                            System.out.println(++num);
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
//            System.out.println(s);
            s = s.replaceAll("[\\s]{2,}", " ");
            numbers.add(PdfsUtil.getNumber(s, 2));
//                numbers.add(PdfsUtil.getNumber(s, 0));
        }
        return numbers;
    }

}
