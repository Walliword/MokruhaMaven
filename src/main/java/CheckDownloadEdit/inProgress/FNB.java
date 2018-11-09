package CheckDownloadEdit.inProgress;


import CheckDownloadEdit.FilesUtil;
import CheckDownloadEdit.HtmlUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class FNB {

    private static final int year = FilesUtil.getYear();
    private static final int month = LocalDate.now().getMonthValue();
    private static final String LINK1 = "https://www.minfin.ru/common/upload/library/20" +
            year +
            "/" +
            month +
            "/main/Obem_sredstv_Fonda_natsionalnogo_blagosostoyaniya_01_" +
            month +
            "_20" +
            year +
            ".docx";
    private static final String LINK2 = "https://www.cbr.ru/hd_base/mrrf/mrrf_m/";
    private static final String LINK3 = "http://www.cbr.ru/vfs/statistics/ms/ms_m21.xlsx";

    public static void main(String[] args) throws Exception {
//        System.out.println(LINK1);
//        System.out.println(Arrays.toString(getDocxInfo()));
            List<String[]> list = getExcelInfo();
    }

    public static String[] getDocxInfo() throws IOException {
//        String[] dates = new String[55];
//        String[] dollars = new String[55];
//        String[] rubles = new String[55];
//        String[] prts = new String[55];
        File file = FilesUtil.downloadFile(LINK1);
        if (file == null) {
            System.out.println("Данного docx файла не существует");
            return null;
        } else {
            XWPFDocument docx = new XWPFDocument(new FileInputStream(file));
            //using XWPFWordExtractor Class
            XWPFWordExtractor we = new XWPFWordExtractor(docx);
            String[] lines = we.getText().split("\n");
            String[] neededLines = new String[60];
            System.arraycopy(lines, 5, neededLines, 0, 60);
            return neededLines;
        }
    }


    public static List<String[]> getHtmlInfo() throws IOException {
        Elements links = HtmlUtil.getElements(LINK2);
        List<String[]> list = new LinkedList<>();
        for (Element link : links) {
            String[] line = new String[7];
            String[] raw = link.text().split(" ");
            if (raw.length > 12) {
                line[0] = raw[0];
                line[1] = raw[1] + raw[2];
                line[2] = raw[3] + raw[4];
                line[3] = raw[5] + raw[6];
                line[4] = raw[7] + raw[8];
                line[5] = raw[9] + raw[10];
                line[6] = raw[11] + raw[12];
                list.add(line);
            }

        }
        return list;
    }

    public static List<String[]> getExcelInfo() {
        File file = FilesUtil.downloadFile(LINK3);
        List<String[]> cells = new ArrayList<>();
        if (file == null) {
            System.out.println("данного xlsx файла не существует");
            return null;
        }
        else {
            System.out.println("Скачиваю xlsx файл");
            try(FileInputStream fileStream = new FileInputStream(file)) {
                XSSFWorkbook wbF = new XSSFWorkbook(fileStream);
                XSSFSheet worksheetF = wbF.getSheetAt(0);
                Cell cellF;
                //NILLPOINTER
                if ((worksheetF.getRow(305+month -1 +12*(year-18)).getCell(0)) != null) {
//                    System.out.println("Данных xlsx за текущий месяц не поступило");
                    cellF = worksheetF.getRow(305+month -1 +12*(year-18)).getCell(0);
                    System.out.println(cellF);
                }
//                Cell cellF = worksheetF.getRow(305+month+12*(year-18)).getCell(0);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return cells;
        }
    }

}
