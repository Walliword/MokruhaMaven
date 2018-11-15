package CheckDownloadEdit.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HtmlUtil {

    public static Elements getElementsByTr(String linkUrl) throws IOException {
        Document doc = Jsoup.connect(linkUrl).get();
        return doc.getElementsByTag("tr");
    }

    public static Elements getElementsByTd(String linkUrl) throws IOException {
        Document doc = Jsoup.connect(linkUrl).get();
        return doc.getElementsByTag("td");
    }

    public static boolean isDouble(String s) throws NumberFormatException {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
