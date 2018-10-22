
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.IOException;

public class SimpleTextExtractor {

    private static String dest = "C:\\Users\\Vallian\\IdeaProjects\\MokruhaMaven\\pdfs\\oper-03-2018.pdf";

    //НУЖНО ОГРАНИЧЕНИЕ ДЖИ ПО ЧИСТЫМ НАЛОГАМ НА ПРОДУКТЫ!!!!
    //пройти циклом раз - найти j начала таблицы и конца
    //пройти циклом два - достать в этих пределах все нужные значения

    public static void main(String[] args) throws IOException {
        // считаем, что программе передается один аргумент - имя файла
        PdfReader reader = new PdfReader(dest);

        // не забываем, что нумерация страниц в PDF начинается с единицы.
        for (int i = 5; i <=8; ++i) {
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            String[] test = text.split("\n");
            for (int j = 0; j < test.length; j++) {

                if (test[j].startsWith("Валовой внутренний")) {
                    System.out.println(test[j - 1]);
                    System.out.println(j+test[j] + test[j + 1]);
                    //System.out.println(test[j + 1]);
                    //System.out.println("testing");
                }
                if (test[j].startsWith("Валовая добавленная"))
                    System.out.println(j+test[j].trim() + test[j + 1] + test[j + 2]);
                //System.out.println(test[j + 1]);
                //System.out.println("TEST");
//                if (test[j].contains("сельское хозяйство"))
//                    System.out.println(j+test[j] + test[j + 1]);
                //СХ
                if (test[j].contains("рыбоводство"))
                    System.out.println(j+test[j]);
                //ИСКОПАЕМЫЕ
                if (test[j].contains("ископаемых"))
                    System.out.println(j+test[j]);
                //ОБРАБОТКА
                if (test[j].contains("производства"))
                    System.out.println(j+test[j]);
                //ЖКХ ЭЛЕКТР
                if (test[j].contains(" воздуха"))
                    System.out.println(j+test[j]);
                //ЖКХ ВОДА
                if (test[j].contains(" загрязнений"))
                    System.out.println(j+test[j]);
                //СТРОИТЕЛЬСТВО
                if (test[j].contains("строительство"))
                    System.out.println(j+test[j]);
                //ТОРГОВЛЯ
                if (test[j].contains(" мотоциклов"))
                    System.out.println(j+test[j]);
                //ТРАНСПОРТ И ХРАНЕНИЕ
                if (test[j].contains(" хранение"))
                    System.out.println(j+test[j]);
                //ГОСТИНИЦЫ И ОБЩЕПИТ
                if (test[j].contains(" питания"))
                    System.out.println(j+test[j]);
                //ИНФОРМАЦИЯ И СВЯЗЬ
                if (test[j].contains(" связи"))
                    System.out.println(j+test[j]);
                //ФИНАНСЫ
                if (test[j].contains(" страховая"))
                    System.out.println(j+test[j]);
                //НЕДВИЖИМОСТЬ
                if (test[j].contains(" имуществом"))
                    System.out.println(j+test[j]);
                //НАУЧНАЯ ДЕЯТ
                if (test[j].contains(" техническая"))
                    System.out.println(j+test[j]);
                ///////////////////////////////////////////////
//                Административные услуги
                if (test[j].contains(" услуги"))
                    System.out.println(j+test[j]);
//                Гос., военка, соц
                if (test[j].contains("социальное обеспечение"))
                    System.out.println(j+test[j]);
//                Образование
                if (test[j].contains("образование "))
                    System.out.println(j+test[j]);
//                        Здравоохранение
                if (test[j].contains("социальных услуг"))
                    System.out.println(j+test[j]);
//                Культура и спорт
                if (test[j].contains(" развлечений"))
                    System.out.println(j+test[j]);
//                        Прочие
                if (test[j].contains("видов услуг"))
                    System.out.println(j+test[j]);
//                Домохозяйства как предприним
                if (test[j].contains(" потребления"))
                    System.out.println(j+test[j]);
//                Чистые налоги
                if (test[j].contains("на продукты"))
                    System.out.println(j+test[j]);


            }
            //System.out.println("TEST");
            //System.out.println(text);
            System.out.println("END OF TEST");
        }

        // убираем за собой
        reader.close();
    }
}
