package CheckDownloadEdit;

import CheckDownloadEdit.ExcelFiles.*;
import CheckDownloadEdit.PdfFiles.*;
import CheckDownloadEdit.MixedFiles.FNB;
import CheckDownloadEdit.ExcelFiles.ExtTrade;
import CheckDownloadEdit.HtmlFiles.DepositsF;
import CheckDownloadEdit.HtmlFiles.Credits;
import CheckDownloadEdit.HtmlFiles.DepositsNonF;
import CheckDownloadEdit.Util.FilesUtil;

public class Controller {
    public static void main(String[] args) throws InterruptedException {
        new GrossDomesticProduct().makeMagic();
        new Inflation().makeMagic();
        new Demography().makeMagic();
        new Energy().makeMagic();
        new Income().makeMagic();
        new Debt().makeMagic();
        new Trade().makeMagic();
        new Transport().makeMagic();
        new FNB().makeMagic();
        new PrivateOutflow().makeMagic();
        new PayBalance().makeMagic();
        new Wage().makeMagic();
        new ExtTrade().makeMagic(); //необходимо редактировать после выхода первых данных по 19
        new DepositsF().makeMagic();
        new DepositsNonF().makeMagic();
        new Credits().makeMagic();
        new InflationStructure().makeMagic();
        Thread.sleep(1000);
        FilesUtil.LOG.info("Работа со всеми страницами завершена.");
    }
}
