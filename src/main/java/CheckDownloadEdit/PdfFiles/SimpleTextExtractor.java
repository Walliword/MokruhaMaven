package CheckDownloadEdit.PdfFiles;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class SimpleTextExtractor {

    private static String dest = "C:\\Users\\Wallian\\IdeaProjects\\MokruhaMaven\\pdfs\\oper-03-2018.pdf";
    public static Map<String, Double> excelTransfer = new LinkedHashMap<>();
    public static int nalog = 0;

    //НУЖНО ОГРАНИЧЕНИЕ ДЖИ ПО ЧИСТЫМ НАЛОГАМ НА ПРОДУКТЫ!!!!
    //пройти циклом раз - найти j начала таблицы и конца
    //пройти циклом два - достать в этих пределах все нужные значения

    public static void main(String[] args) throws IOException {
        extractLines();
        excelExtractor();
    }

    private static void excelExtractor() throws IOException {
        FileInputStream fsIP= new FileInputStream(new File("C:\\Users\\Wallian\\IdeaProjects\\MokruhaMaven\\pdfs\\MOKRUHA Eternal.xlsx")); //Read the spreadsheet that needs to be updated

        XSSFWorkbook wb = new XSSFWorkbook(fsIP); //Access the workbook
//страница
        XSSFSheet worksheet = wb.getSheetAt(1); //Access the worksheet, so that we can update / modify it.

        Cell cell = null; // declare a Cell object
        Cell cell1 = null;
        //List<String> keys = getKeys();
        List<Double> values = getValues();
        XSSFCellStyle style = wb.createCellStyle();
        style.setBorderRight(BorderStyle.THIN);
        for (int i = 1; i < 24; i++) {
            //cell = worksheet.getRow(i+1).createCell(6);
            cell1 = worksheet.getRow(i+1).createCell(8);
            cell1.setCellStyle(style);
//            if (i==2) {
//                XSSFCellStyle style1 = style;
//                style1.setBorderBottom(BorderStyle.THIN);
//                cell1.setCellStyle(style1);
//            }
            //cell.setCellValue(keys.get(i-1));
            cell1.setCellValue(values.get(i-1));
            //System.out.println("proceed");
        }
//строка и столбец
 //       cell = worksheet.getRow(2).getCell(3);   // Access the second cell in second row to update the value

//        cell.setCellValue("OverRide Last Name");  // Get current cell value value and overwrite the value

        fsIP.close(); //Close the InputStream

        FileOutputStream output_file =new FileOutputStream(new File("C:\\Users\\Wallian\\IdeaProjects\\MokruhaMaven\\pdfs\\MOKRUHA Eternal.xlsx"));  //Open FileOutputStream to write updates

        wb.write(output_file); //write changes

        output_file.close();  //close the stream
        System.out.println("Редактирование завершено");
    }

    private static void extractLines() throws IOException {
        // считаем, что программе передается один аргумент - имя файла
        PdfReader reader = new PdfReader(dest);

        // не забываем, что нумерация страниц в PDF начинается с единицы.
        for (int i = 5; i <=8; ++i) {
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            String[] test = text.split("\n");
            for (int j = 0; j < test.length; j++) {
                if (nalog==2) break;

                if (test[j].startsWith("Валовой внутренний")) {
                    System.out.println(test[j - 1]);
                    String s = (test[j].trim() + test[j + 1]).replaceAll("[\\s]{2,}", " ");
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("ВВП",getNumber(s, 3));
                }
                if (test[j].startsWith("Валовая добавленная")) {
                    String s = (test[j].trim() + test[j + 1] + test[j + 2]).replaceAll("[\\s]{2,}", " ");
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("ВДС",getNumber(s, getPosition()));
                }
                //СХ
                if (test[j].contains("рыбоводство")) {
                    String s = test[j].trim();
                    System.out.println(j+ s);
                    if (isExactYear())
                    excelTransfer.put("Сельхоз",getNumber(s, getPosition()));
                }

                //ИСКОПАЕМЫЕ
                if (test[j].contains("ископаемых")) {
                    String s = test[j].trim();
                    System.out.println(j+ s);
                    if (isExactYear())
                    excelTransfer.put("Добыча ПИ",getNumber(s, getPosition()));
                }
                //ОБРАБОТКА
                if (test[j].contains("производства")) {
                    if (!test[j].contains("Динамика производства")) {
                        String s = test[j].trim();
                        System.out.println(j + s);
                        if (isExactYear())
                        excelTransfer.put("Обработка",getNumber(s, getPosition()));
                    }
                }
                //ЖКХ ЭЛЕКТР
                if (test[j].contains(" воздуха")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("ЖКХ электр",getNumber(s, getPosition()));
                }
                //ЖКХ ВОДА
                if (test[j].contains(" загрязнений")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("ЖКХ вода",getNumber(s, getPosition()));
                }
                //СТРОИТЕЛЬСТВО
                if (test[j].contains("строительство")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Строительство",getNumber(s, getPosition()));
                }
                //ТОРГОВЛЯ
                if (test[j].contains(" мотоциклов")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Торговля",getNumber(s, getPosition()));
                }
                //ТРАНСПОРТ И ХРАНЕНИЕ
                if (test[j].contains(" хранение")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Транспорт и хранение",getNumber(s, getPosition()));
                }
                //ГОСТИНИЦЫ И ОБЩЕПИТ
                if (test[j].contains(" питания")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Гостиницы, общепит",getNumber(s, getPosition()));
                }
                //ИНФОРМАЦИЯ И СВЯЗЬ
                if (test[j].contains(" связи")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Информация и связь",getNumber(s, getPosition()));
                }
                //ФИНАНСЫ
                if (test[j].contains(" страховая")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Финансы",getNumber(s, getPosition()));
                }
                //НЕДВИЖИМОСТЬ
                if (test[j].contains(" имуществом")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Недвижимость",getNumber(s, getPosition()));
                }
                //НАУЧНАЯ ДЕЯТ
                if (test[j].contains(" техническая")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Научная деят",getNumber(s, getPosition()));
                }
//                Административные услуги
                if (test[j].contains(" услуги")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Административные услуги",getNumber(s, getPosition()));
                }
//                Гос., военка, соц
                if (test[j].contains("социальное обеспечение")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Гос., военка, соц",getNumber(s, getPosition()));
                }
//                Образование
                if (test[j].contains("образование ")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Образование",getNumber(s, getPosition()));
                }
//                        Здравоохранение
                if (test[j].contains("социальных услуг")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Здравоохранение",getNumber(s, getPosition()));
                }
//                Культура и спорт
                if (test[j].contains(" развлечений")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Культура и спорт",getNumber(s, getPosition()));
                }
//                        Прочие
                if (test[j].contains("видов услуг")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Прочие",getNumber(s, getPosition()));
                }
//                Домохозяйства как предприним
                if (test[j].contains(" потребления")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Домохозяйства как предприним",getNumber(s, getPosition()));
                }
//                Чистые налоги
                if (test[j].contains("на продукты")) {
                    String s = test[j].trim();
                    System.out.println(j + s);
                    if (isExactYear())
                    excelTransfer.put("Чистые налоги",getNumber(s, getPosition()));
                    nalog++;
                }
            }
            System.out.println("END OF TEST");
        }
        excelTransfer.forEach((k, v) -> System.out.println(k+" : "+v));
        // убираем за собой
        reader.close();
    }

    private static int getPosition() {
        return 3;
    }

//    private static void getNumbers(String s) {
//        String a = s.replaceAll("[A-Za-zА-Яа-я]", "").trim().replaceAll(",", ".");
//        List<Double> doubles = new ArrayList<>();
//        for (String nmb : a.split(" ")) {
//            doubles.add(Double.parseDouble(nmb));
//        }
//        doubles.forEach(System.out::println);
//    }

    private static double getNumber(String s, int position) {
        String a = s.replaceAll("[A-Za-zА-Яа-я;]", "").trim().replaceAll(",", ".");
        String numberString = (a.split(" ")[position]);
        return Double.parseDouble(numberString);
    }

    private static boolean isExactYear() {
        return nalog ==1;
    }

    public static List<String> getKeys() {
        List<String> keys = new ArrayList<>();
        excelTransfer.forEach((k, v) -> keys.add(k));
        return keys;
    }

    public static List<Double> getValues() {
        List<Double> values = new ArrayList<>();
        excelTransfer.forEach((k, v) -> values.add(v));
        return values;
    }
}
