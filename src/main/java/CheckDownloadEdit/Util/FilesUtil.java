package CheckDownloadEdit.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import static java.lang.String.format;

public class FilesUtil {

    private static final String DIRECTORY = "D:\\temp\\";
    public static final String MOKRUHA_ETERNAL = "D:\\MKR_DIR\\MOKRUHA Eternal.xlsx";
    public static final Logger LOG = LoggerFactory.getLogger(FilesUtil.class);

    /**
     * Получает двузначное значение текущего года, используемое для автоматического смещения по строкам в таблицах
     */
    public static final int CURRENT_YEAR;
    static {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        CURRENT_YEAR = calendar.get(Calendar.YEAR) - 2000;
    }

    /**
     * Проверка существования файла по ссылке - необходимо перед любым скачиванием
     *
     * @param URLName текстовая ссылка на файл
     * @return возвращает результат проверки
     */
    public static boolean exists(String URLName) {
        String filename = URLName.substring(URLName.lastIndexOf('/') + 1, URLName.lastIndexOf('.'));
        String suffix = URLName.substring(URLName.lastIndexOf('.'));
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOG.error(format("Ошибка при проверке наличия файла %s.%s. %s", filename, suffix, e.getMessage()));
//            System.out.println("Ошибка при проверке наличия файла %s.%s" + filename + "." + suffix);
//            e.printStackTrace();
            return false;
        }
    }

    /**
     * Создание временной директории для всех загружаемых файлов - вызывается перед
     * каждым скачиванием, ничего не делает, если директория уже существует
     *
     * @return путь к директории
     */
    private static Path createTempDirectory() {
        Path dirPath = Paths.get(DIRECTORY);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
                LOG.debug("Временная директория создана.");
//                System.out.println("Временная директория создана.");
            } catch (IOException e) {
                LOG.error(format("Ошибка при создании директории. %s", e.getMessage()));
//                System.out.println("Ошибка при создании директории.");
//                e.printStackTrace();
            }
        }
        return dirPath;
    }

    /**
     * Скачивание файла по ссылке, если он существует
     *
     * @param URLName ссылка на файл
     * @return возвращает требуемый файл, если он успешно скачан
     */
    public static File downloadFile(String URLName) {
        if (exists(URLName)) {
            String filename = URLName.substring(URLName.lastIndexOf('/') + 1, URLName.lastIndexOf('.'));
            String suffix = URLName.substring(URLName.lastIndexOf('.'));
            LOG.debug(format("File %s%s exists. Downloading..", filename, suffix));
//            System.out.println("файл " + filename + "." + suffix +
//                    " существует");
            try {
                URL url = new URL(URLName);
                InputStream inputStream;
                inputStream = url.openStream();
                Path tempFile = Files.createTempFile(filename, suffix);
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                Path target = Paths.get(createTempDirectory() + "/" + filename + suffix);
                Files.move(tempFile, target, StandardCopyOption.REPLACE_EXISTING);
                return new File(String.valueOf(target));
            } catch (IOException e) {
                LOG.error(format("Ошибка при скачивании существующего файла. %s", e.getMessage()));
//                System.out.println("Ошибка при скачивании существующего файла");
                e.printStackTrace();
            }
        } else {
            LOG.debug("Файла не существует.");
//            System.out.println("файла не существует");
        }
        return null;
    }

    /**
     * Удаляет временную директорию со всем содержимым - вызывается в
     * управляющем методе по завершению работы всех постраничных методов
     */
    public static void deleteTempDirectory() {
        Path dirPath = Paths.get(DIRECTORY);
        try {
            //noinspection ResultOfMethodCallIgnored
            Files.walk(dirPath).sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            LOG.debug("Временная директория удалена.");
//            System.out.println("Временная директория удалена");
        } catch (IOException e) {
            LOG.error(format("Проблемы с удалением директории. %s", e.getMessage()));
//            System.out.println("Проблемы с удалением директории");
            e.printStackTrace();
        }
    }


//    public static int getYear() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        return calendar.get(Calendar.YEAR) - 2000;
//    }
}
