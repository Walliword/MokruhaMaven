package CheckDownloadEdit;

import CheckDownloadEdit.ExcelFiles.*;
import CheckDownloadEdit.PdfFiles.GrossDomesticProduct;
import CheckDownloadEdit.PdfFiles.Trade;
import CheckDownloadEdit.MixedFiles.FNB;
import CheckDownloadEdit.ExcelFiles.ExtTrade;
import CheckDownloadEdit.HtmlFiles.DepositsF;
import CheckDownloadEdit.HtmlFiles.Credits;
import CheckDownloadEdit.HtmlFiles.DepositsNonF;

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
        new PayBalance().makeMagic();
        new ExtTrade().makeMagic();
        new DepositsF().makeMagic();
        new DepositsNonF().makeMagic();
        new Credits().makeMagic();
    }
}
