package CheckDownloadEdit.Util;

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

public class FilesUtil {

    private static final String DIRECTORY = "D:\\temp\\";
    public static final String MOKRUHA_ETERNAL = "D:\\MKR_DIR\\MOKRUHA Eternal.xlsx";

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
            System.out.println("Ошибка при проверке наличия файла " + filename + "." + suffix);
            e.printStackTrace();
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
                System.out.println("Временная директория создана.");
            } catch (IOException e) {
                System.out.println("Ошибка при создании директории.");
                e.printStackTrace();
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
            System.out.println("файл " + filename + suffix +
                    " существует");
            Path filePath = Paths.get(DIRECTORY + filename + suffix);
            if (!Files.exists(filePath)) {
                System.out.println("Скачиваю файл");
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
                System.out.println("Ошибка при скачивании существующего файла");
                e.printStackTrace();
            }
        }
            else {
                System.out.println("Файл уже скачан");
                return new File(String.valueOf(DIRECTORY + filename + suffix));
            }
        } else System.out.println("файла не существует");
        return null;
    }

    /**
     * Удаляет временную директорию со всем содержимым - вызывается в
     * управляющем методе по завершению работы всех постраничных методов
     */
    public static void deleteTempDirectory() {
        Path dirPath = Paths.get(DIRECTORY);
        try {
            Files.walk(dirPath).sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            System.out.println("Временная директория удалена");
        } catch (IOException e) {
            System.out.println("Проблемы с удалением директории");
            e.printStackTrace();
        }
    }

    /**
     * Получает двузначное значение текущего года, используемое для автоматического смещения по строкам в таблицах
     * @return возвращает год без тысяч
     */
    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.YEAR) - 2000;
    }

}
