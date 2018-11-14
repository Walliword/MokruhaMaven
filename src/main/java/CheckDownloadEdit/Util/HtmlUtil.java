package CheckDownloadEdit.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HtmlUtil {

    public static Elements getElements(String linkUrl) throws IOException {
        Document doc = Jsoup.connect(linkUrl).get();
        return doc.getElementsByTag("tr");
    }
}
