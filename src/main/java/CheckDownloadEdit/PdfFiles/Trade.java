package CheckDownloadEdit.PdfFiles;

import CheckDownloadEdit.Util.FilesUtil;
import CheckDownloadEdit.Util.PdfsUtil;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

public class Trade {

    private static int year = 0;
    private static int[] months = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private static int pageNumber = 0;
    private static List<String> check = PdfsUtil.getUrlStrings(months, FilesUtil.getYear());


    public void makeMagic() {
        //List<String> check = PdfsUtil.getUrlStrings(months);
        File file;
        if (check.size() == 0) {
            file = FilesUtil.downloadFile("http://www.gks.ru/free_doc/doc_20"
                    + (FilesUtil.getYear() - 1) + "/info/oper-12-20" + (FilesUtil.getYear() - 1) + ".pdf");
            if (file == null) {
                System.out.println("Нет данных по Торговле для текущего года");
            }
        } else {
            System.out.println("Обновляю страницу Торговля..");
            file = FilesUtil.downloadFile(check.get(PdfsUtil.pdfSize - 1));
//            assert file != null;
        }
        if (file != null) {

            try (FileInputStream fsIP = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                 XSSFWorkbook wb = new XSSFWorkbook(fsIP)) {
                XSSFSheet worksheet = wb.getSheetAt(6); //Access the worksheet, so that we can update / modify it.
                double[] values = getValues(file.getAbsolutePath());
                if (values[0] == values[1]) {
                    System.out.println("Нет данных для страницы Торговля для предыдущего месяца");
                }
                else {
                    //System.out.println(Arrays.toString(values));

                    Cell cell1;
                    Cell cell2;
                    cell1 = worksheet.getRow(121 + (FilesUtil.getYear() - 17) * 13 + getMonth()).createCell(2);
                    cell2 = worksheet.getRow(121 + (FilesUtil.getYear() - 17) * 13 + getMonth()).createCell(3);
                    cell1.setCellValue(values[0]);
                    cell2.setCellValue(values[1]);

                    fsIP.close(); //Close the InputStream
                    //Open FileOutputStream to write updates
                    FileOutputStream output_file = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                    wb.write(output_file); //write changes
                    output_file.close();  //close the stream

                    System.out.println("Редактирование страницы Торговля завершено");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private double[] getValues(String filePath) throws IOException {
        double[] values = new double[2];

        PdfReader reader = new PdfReader(filePath);
        TextExtractionStrategy strategyPage = new SimpleTextExtractionStrategy();
        String textPage = PdfTextExtractor.getTextFromPage(reader, 3, strategyPage);
//                        System.out.println(text);
        pageNumber = PdfsUtil.getPage(textPage, "РЫНОК ТОВАРОВ…");
        System.out.println(pageNumber);
//        for (int i = 51; i <= 75; ++i) {
//            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
//            String text = PdfTextExtractor.getTextFromPage(reader, i, strategy);
//            String[] test = text.split("\n");
//
//            if (test[2].contains("РЫНОК ТОВАРОВ")) {
//                pageNumber = i;
//                System.out.println(pageNumber);
//                break;
//            }
//        }

        for (int j = pageNumber; j < pageNumber + getPage(); j++) {
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, j, strategy);
            String[] test = text.split("\n");
            for (int i = 0; i < test.length; i++) {
                if (test[i].trim().equals("20" + FilesUtil.getYear() + "г.") && test[i + 1].trim().startsWith("Январь ")) {
//                        System.out.println(test[i]);
//                        System.out.println(test[i+1]);
//                        System.out.println(j);
//                        System.out.println(i);
                    year++;
                }
                //получаем месяц
                if (test[i].trim().startsWith(getMonthName() + " ")) {
                    if (year == 2) {
                        System.out.println(test[i]);
                        System.out.println(j);
                        System.out.println(PdfsUtil.getNumber(test[i], 0));
                        values[0] = PdfsUtil.getNumber(test[i], 0);
                        System.out.println(PdfsUtil.getNumber(test[i], 3));
                        values[1] = PdfsUtil.getNumber(test[i], 3);
                    }
                }
            }
        }
        // убираем за собой
        reader.close();
        //возвращаем массив значений
        return values;
    }

    private int getPage() {
        if (getMonth() == 1)
            return 3;
        else
        return 4;
    }

    private int getMonth() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        if (month == 1)
            return 12;
        else
            return today.getMonthValue() - 1;
    }

    private String getMonthName() {
        Month month = Month.of(getMonth());
        Locale loc = Locale.forLanguageTag("ru");
        return month.getDisplayName(TextStyle.FULL_STANDALONE, loc);
    }


}
