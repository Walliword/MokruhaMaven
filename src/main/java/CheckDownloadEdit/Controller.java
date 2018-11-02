package CheckDownloadEdit;

import CheckDownloadEdit.ExcelFiles.Inflation;
import CheckDownloadEdit.PdfFiles.GrossDomesticProduct;

public class Controller {
    public static void main(String[] args) {
        Inflation inflation = new Inflation();
        inflation.makeMagic();
        GrossDomesticProduct product = new GrossDomesticProduct();
        product.makeMagic();
    }
}
