package CheckDownloadEdit.RawClasses;

import CheckDownloadEdit.Util.FilesUtil;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class MainClass {

//    public static void main(String[] args) {
//        downloadFile();
//    }

    private static void downloadFile() {
        URL url;
        try {
            for (String link : getUrlString()) {
                url = new URL(link);
                BufferedInputStream bis = new BufferedInputStream(url.openStream());
                String filename = link.substring(link.lastIndexOf('/') + 1);
                System.out.println("скачиваю файл " + filename);
                FileOutputStream fis = new FileOutputStream("C:\\Users\\Vallian\\IdeaProjects\\MokruhaMaven\\pdfs\\" + filename);
                System.out.println("путь к файлу " + "C:\\Users\\Vallian\\IdeaProjects\\MokruhaMaven\\pdfs\\" + filename);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = bis.read(buffer, 0, 1024)) != -1) {
                    fis.write(buffer, 0, count);
                }
                fis.close();
                bis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static Path downloadExistingFiles(String urlString, Path downloadDirectory) throws IOException {
        URL url = new URL(urlString);
        String filename = urlString.substring(urlString.lastIndexOf('/') + 1, urlString.lastIndexOf('.'));
        String suffix = urlString.substring(urlString.lastIndexOf('.'));
        InputStream inputStream = url.openStream();
        Path tempFile = Files.createTempFile(filename, suffix);
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        Path target = Paths.get(downloadDirectory + "/" + filename + suffix);
        Files.move(tempFile, target);

        return target;
    }

    private static List<String> getUrlString() {
        List<String> pdfs = new ArrayList<>();
        for (int i = 17; i < 20; i++) {
            for (int j = 1; j < 13; j++) {
                StringBuilder builder = new StringBuilder();
                builder.append("http://www.gks.ru/free_doc/doc_20").append(i).append("/info/oper-");
                if (j < 10)
                    builder.append(0);
                builder.append(j).append("-20").append(i).append(".pdf");
                String urlName = builder.toString();
                if (FilesUtil.exists(urlName)) {
                    pdfs.add(urlName);
                }
            }
        }
        return pdfs;
    }

}
