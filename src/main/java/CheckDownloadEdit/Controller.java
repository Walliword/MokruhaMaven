package CheckDownloadEdit;

import CheckDownloadEdit.ExcelFiles.Demography;
import CheckDownloadEdit.ExcelFiles.Inflation;
import CheckDownloadEdit.PdfFiles.GrossDomesticProduct;

public class Controller {
    public static void main(String[] args) {
        Inflation inflation = new Inflation();
        inflation.makeMagic();
        GrossDomesticProduct product = new GrossDomesticProduct();
        product.makeMagic();
        Demography demography = new Demography();
        demography.makeMagic();
    }
}
