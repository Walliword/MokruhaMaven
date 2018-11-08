package CheckDownloadEdit;

import CheckDownloadEdit.ExcelFiles.Debt;
import CheckDownloadEdit.ExcelFiles.Demography;
import CheckDownloadEdit.ExcelFiles.Income;
import CheckDownloadEdit.ExcelFiles.Inflation;
import CheckDownloadEdit.PdfFiles.GrossDomesticProduct;
import CheckDownloadEdit.PdfFiles.Trade;

public class Controller {
    public static void main(String[] args) {
        new Inflation().makeMagic();
        new GrossDomesticProduct().makeMagic();
        new Demography().makeMagic();
        new Income().makeMagic();
        new Debt().makeMagic();
        new Trade().makeMagic();
    }
}
