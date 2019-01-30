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
    private static List<String> check = PdfsUtil.getUrlStrings(months, FilesUtil.CURRENT_YEAR);

    /**
     * Используется последний вышедший за год файл статистики, из которого достается информация за предыдущий месяц.
     * Если на дворе январь - загружается файл с инфо за декабрь прошедшего года.
     * Необходимо тестирование, когда выйдут декабрьский и январский файлы.
     */

    public void makeMagic() {
        File file;
        //Если на дворе январь
        PdfsUtil.LOG.debug("Проверяю наличие данных для страницы Торговля за предыдущий месяц..");
        if (check.size() == 0) {
            file = FilesUtil.downloadFile("http://www.gks.ru/free_doc/doc_20"
                    + (FilesUtil.CURRENT_YEAR - 1) + "/info/oper-12-20" + (FilesUtil.CURRENT_YEAR - 1) + ".pdf");
            if (file == null) {
                PdfsUtil.LOG.info("Нет данных по Торговле для текущего года.");
//                System.out.println("Нет данных по Торговле для текущего года");
            }
        } else {
            //Если не январь
//            PdfsUtil.LOG.debug("Проверяю наличие данных для страницы Торговля за предыдущий месяц..");
//            System.out.println("Обновляю страницу Торговля..");
            file = FilesUtil.downloadFile(check.get(PdfsUtil.pdfSize - 1));
//            assert file != null;
        }
        if (file != null) {
            try (FileInputStream fsIP = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                 XSSFWorkbook wb = new XSSFWorkbook(fsIP)) {
                XSSFSheet worksheet = wb.getSheetAt(6);
                double[] values = getValues(file.getAbsolutePath());
                if (values[0] == values[1]) {
                    PdfsUtil.LOG.info("Нет данных для страницы Торговля для предыдущего месяца.");
//                    System.out.println("Нет данных для страницы Торговля для предыдущего месяца");
                }
                else {
//                    System.out.println(Arrays.toString(values));

                    Cell cell1;
                    Cell cell2;
                    if (worksheet.getRow(121 + (PdfsUtil.getYear() - 17) * 13 + getMonth()) == null) {
                        worksheet.createRow(121 + (PdfsUtil.getYear() - 17) * 13 + getMonth());
                    }
                    cell1 = worksheet.getRow(121 + (PdfsUtil.getYear() - 17) * 13 + getMonth()).createCell(2);
                    cell2 = worksheet.getRow(121 + (PdfsUtil.getYear() - 17) * 13 + getMonth()).createCell(3);
                    cell1.setCellValue(values[0]);
                    cell2.setCellValue(values[1]);

                    fsIP.close(); //Close the InputStream
                    //Open FileOutputStream to write updates
                    FileOutputStream output_file = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                    wb.write(output_file); //write changes
                    output_file.close();  //close the stream
                    PdfsUtil.LOG.debug("Редактирование страницы Торговля завершено.");
//                    System.out.println("Редактирование страницы Торговля завершено");
                }
            } catch (IOException e) {
                PdfsUtil.LOG.error("Ошибка чтения-записи данных для страницы Торговля.");
                e.printStackTrace();
            }
        }
    }

    private double[] getValues(String filePath) throws IOException {
        double[] values = new double[2];
        //получаем ридер для файла
        PdfReader reader = new PdfReader(filePath);
        //получаем страницу с заголовком
        TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
        String text = PdfTextExtractor.getTextFromPage(reader, 3, strategy);
        int page = PdfsUtil.getPage(text, "РЫНОК ТОВАРОВ…");
//        System.out.println(page);
        //находим таблицу 3
        for (int i = page; i < page + 4; i++) {
            TextExtractionStrategy strategyTable = new SimpleTextExtractionStrategy();
            String textTable = PdfTextExtractor.getTextFromPage(reader, i, strategyTable);
            String[] test = textTable.split("\n");
            for (String s : test) {
                if (s.contains("Таблица 3")) {
//                    System.out.println(s);
                    pageNumber = i;
                    break;
                }
            }
        }
        //находим нужные числа по месяцу
        for (int j = pageNumber; j < pageNumber+2; j++) {
            TextExtractionStrategy strategyMonth = new SimpleTextExtractionStrategy();
            String textMonth = PdfTextExtractor.getTextFromPage(reader, j, strategyMonth);
            String[] test = textMonth.split("\n");
            for (String s : test) {
                //получаем месяц
                if (s.trim().startsWith(PdfsUtil.getMonthName(getMonth()) + " ")) {
                    year++;
                    if (year == 2) {
//                        System.out.println(s);
//                        System.out.println(j);
//                        System.out.println(PdfsUtil.getNumber(s, 0));
                        values[0] = PdfsUtil.getNumber(s, 0);
//                        System.out.println(PdfsUtil.getNumber(s, 3));
                        values[1] = PdfsUtil.getNumber(s, 3);
                    }
                }
            }
        }
        // убираем за собой
        reader.close();
        //возвращаем массив значений
        return values;
    }

    private int getMonth() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        if (month == 1)
            return 12;
        else
            return today.getMonthValue() - 1;
    }
}
