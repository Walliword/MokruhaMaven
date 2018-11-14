package CheckDownloadEdit;

import CheckDownloadEdit.ExcelFiles.*;
import CheckDownloadEdit.PdfFiles.GrossDomesticProduct;
import CheckDownloadEdit.PdfFiles.Trade;
import CheckDownloadEdit.MixedFiles.FNB;

public class Controller {
    public static void main(String[] args) {
        new Inflation().makeMagic();
        new GrossDomesticProduct().makeMagic();
        new Demography().makeMagic();
        new Income().makeMagic();
        new Debt().makeMagic();
        new Trade().makeMagic();
        new FNB().makeMagic();
        new PrivateOutflow().makeMagic();
    }
}
