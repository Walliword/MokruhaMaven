package CheckDownloadEdit.Util;

import org.apache.poi.ss.usermodel.Cell;

public class XlsxUtil {

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
