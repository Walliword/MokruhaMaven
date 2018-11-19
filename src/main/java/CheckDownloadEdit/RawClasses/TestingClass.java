package CheckDownloadEdit.RawClasses;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Map;

public class TestingClass {
//    public static void main(String[] args) throws IOException {
//
//        ExcelExtractor();
//    }

    private static void ExcelExtractor() throws IOException {
        FileInputStream fsIP= new FileInputStream(new File("C:\\Users\\Wallian\\IdeaProjects\\MokruhaMaven\\pdfs\\emplo.xlsx")); //Read the spreadsheet that needs to be updated

        XSSFWorkbook wb = new XSSFWorkbook(fsIP); //Access the workbook
//страница
        XSSFSheet worksheet = wb.getSheetAt(0); //Access the worksheet, so that we can update / modify it.

        Cell cell = null; // declare a Cell object
//строка и столбец
        cell = worksheet.getRow(2).getCell(2);   // Access the second cell in second row to update the value

        cell.setCellValue("Testing");  // Get current cell value value and overwrite the value

        fsIP.close(); //Close the InputStream

        FileOutputStream output_file =new FileOutputStream(new File("C:\\Users\\Wallian\\IdeaProjects\\MokruhaMaven\\pdfs\\emplo.xlsx"));  //Open FileOutputStream to write updates

        wb.write(output_file); //write changes

        output_file.close();  //close the stream
        System.out.println("Редактирование завершено");
    }
}
