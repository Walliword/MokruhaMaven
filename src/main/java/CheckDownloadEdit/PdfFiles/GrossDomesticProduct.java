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
import java.util.*;

public class GrossDomesticProduct {

    private static int nalog = 0;
    private static int pdfSize = 0;
    private static int[] months = new int[]{3, 5, 8, 11};

//    //сбор ссылки по частям
//    private static final String PART1 = "http://www.gks.ru/free_doc/doc_20";
//    //здесь год
//    private static final String PART2 = "/info/oper-";
//    //здесь месяц
//    private static final String PART3 = "-20";
//    //и снова год
//    private static final String PART4 = ".pdf";



    public void makeMagic() {
        List<String> check = PdfsUtil.getUrlStrings(months);
        if (check.size() == 0) {
            System.out.println("Нет данных по ВВП для текущего года");
        } else {
            System.out.println("Обновляю страницу ВВП..");
            File file = FilesUtil.downloadFile(check.get(pdfSize - 1));
            assert file != null;

            try (FileInputStream fsIP = new FileInputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                 XSSFWorkbook wb = new XSSFWorkbook(fsIP)) {

                XSSFSheet worksheet = wb.getSheetAt(1); //Access the worksheet, so that we can update / modify it.
                List<Double> values = getValues(file.getAbsolutePath());
                int cellNumber = getCellNumber();
                XSSFCellStyle style = wb.createCellStyle();
                if (pdfSize == 1) {
                    style.setBorderRight(BorderStyle.THIN);
                }
                Cell cell;

                for (int i = 1; i < 24; i++) {
                    cell = worksheet.getRow(i + 1).createCell(cellNumber);
                    cell.setCellStyle(style);
                    if (i == 2) {
                        XSSFCellStyle styleLow = wb.createCellStyle();
                        styleLow.setBorderBottom(BorderStyle.THIN);
                        cell.setCellStyle(styleLow);
                    }
                    cell.setCellValue(values.get(i - 1));
                }
                fsIP.close(); //Close the InputStream
                //Open FileOutputStream to write updates
                FileOutputStream output_file = new FileOutputStream(new File(FilesUtil.MOKRUHA_ETERNAL));
                wb.write(output_file); //write changes
                output_file.close();  //close the stream

                System.out.println("Редактирование страницы ВВП завершено");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//    private List<String> getUrlStrings() {
//        List<String> pdfs = new ArrayList<>();
//        int year = FilesUtil.getYear();
//        for (int i = 0; i < 4; i++) {
//            StringBuilder builder = new StringBuilder();
//            builder.append(PART1).append(year).append(PART2);
//            if (months[i] < 10) {
//                builder.append(0);
//            }
//            builder.append(months[i]).append(PART3).append(year).append(PART4);
//            String urlName = builder.toString();
//            if (FilesUtil.exists(urlName)) {
//                pdfs.add(urlName);
//            }
//        }
//        //от количества ссылок будем отталкиваться
//        pdfSize = pdfs.size();
//        return pdfs;
//    }

    private double getNumber(String s, int position) {
        String a = s.replaceAll("[A-Za-zА-Яа-я;]", "").trim().replaceAll(",", ".");
        String numberString = (a.split(" ")[position]);
        return Double.parseDouble(numberString);
    }

    private int getCellNumber() {
        return 7 + (FilesUtil.getYear() - 18) * 4 + pdfSize;
    }

    private int limitNalog() {
        switch (pdfSize) {
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 2;
        }
        return 0;
    }

    private boolean isExactYear() {
        switch (pdfSize) {
            case 1:
                return nalog == 1;
            case 2:
                return nalog == 0;
            case 3:
                return nalog == 1;
            case 4:
                return nalog == 1;
        }
        return nalog == 0;
    }

    private int getPosition() {
        switch (pdfSize) {
            case 1:
                return 3;
            case 2:
                return 0;
            case 3:
                return 2;
            case 4:
                return 4;
        }
        return 0;
    }

    private int getProductPosition() {
        switch (pdfSize) {
            case 1:
                return 3;
            case 2:
                return 0;
            case 3:
                return 1;
            case 4:
                return 2;
        }
        return 0;
    }

    private List<Double> getValues(String filePath) throws IOException {
        List<Double> values = new LinkedList<>();
        // считаем, что программе передается один аргумент - имя файла
        PdfReader reader = new PdfReader(filePath);
        // не забываем, что нумерация страниц в PDF начинается с единицы.
        for (int i = 5; i <= 8; ++i) {
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            String[] test = text.split("\n");
            for (int j = 0; j < test.length; j++) {
                //сверка с геттером и свитчем
                if (nalog >= limitNalog()) break;
                //ВВП
                if (test[j].startsWith("Валовой внутренний")) {
                    String s = (test[j].trim() + test[j + 1]).replaceAll("[\\s]{2,}", " ");
                    if (isExactYear())
                        values.add(getNumber(s, getProductPosition()));
                }
                //ВДС
                if (test[j].startsWith("Валовая добавленная")) {
                    String s = (test[j].trim() + test[j + 1] + test[j + 2]).replaceAll("[\\s]{2,}", " ");
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //СХ
                if (test[j].contains("рыбоводство")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //ИСКОПАЕМЫЕ
                if (test[j].contains("ископаемых")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //ОБРАБОТКА
                if (test[j].contains("производства")) {
                    if (!test[j].contains("Динамика производства")) {
                        String s = test[j].trim();
                        if (isExactYear())
                            values.add(getNumber(s, getPosition()));
                    }
                }
                //ЖКХ ЭЛЕКТР
                if (test[j].contains(" воздуха")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //ЖКХ ВОДА
                if (test[j].contains(" загрязнений")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //СТРОИТЕЛЬСТВО
                if (test[j].contains("строительство")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //ТОРГОВЛЯ
                if (test[j].contains(" мотоциклов")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //ТРАНСПОРТ И ХРАНЕНИЕ
                if (test[j].contains(" хранение")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //ГОСТИНИЦЫ И ОБЩЕПИТ
                if (test[j].contains(" питания")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //ИНФОРМАЦИЯ И СВЯЗЬ
                if (test[j].contains(" связи")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //ФИНАНСЫ
                if (test[j].contains(" страховая")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //НЕДВИЖИМОСТЬ
                if (test[j].contains(" имуществом")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //НАУЧНАЯ ДЕЯТ
                if (test[j].contains(" техническая")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
//                Административные услуги
                if (test[j].contains(" услуги")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
//                Гос., военка, соц
                if (test[j].contains("социальное обеспечение")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
//                Образование
                if (test[j].contains("образование ")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
//                        Здравоохранение
                if (test[j].contains("социальных услуг")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
//                Культура и спорт
                if (test[j].contains(" развлечений")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
//                        Прочие
                if (test[j].contains("видов услуг")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
//                Домохозяйства как предприним
                if (test[j].contains(" потребления")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                }
                //                Чистые налоги
                if (test[j].contains("на продукты")) {
                    String s = test[j].trim();
                    if (isExactYear())
                        values.add(getNumber(s, getPosition()));
                    nalog++;
                }
            }
        }
        // убираем за собой
        reader.close();
        //возвращаем список
        return values;
    }
}
