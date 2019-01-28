package CheckDownloadEdit.Util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XlsxUtil {

    public static final Logger LOG = LoggerFactory.getLogger(XlsxUtil.class);

    public static XSSFCellStyle getSquareStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    public static XSSFCellStyle getSquareFatStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        return style;
    }

    public static void fillCells(Cell cellF, Cell cellMKR) {
        switch (cellF.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                cellMKR.setCellValue(cellF.getStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                cellMKR.setCellValue(cellF.getNumericCellValue());
                break;
        }
    }
}
