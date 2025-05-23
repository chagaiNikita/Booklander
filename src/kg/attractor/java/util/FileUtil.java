package kg.attractor.java.util;

import com.google.gson.*;
import kg.attractor.java.models.BookLender;


import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {
    private static final Path PATH = Path.of("data/resources", "booklender.json");
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).
            setPrettyPrinting().create();


    public static BookLender readFromFile() {
        try {

            String json = Files.readString(PATH);
            return GSON.fromJson(json, BookLender.class);

        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла, путь " + e.getMessage() + " не найден");
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    public static void writeToFile(BookLender bookLender) {
        try {
            System.out.println("Попытка записи");
            if (!Files.exists(PATH)) {
                Files.createFile(PATH);
            }
            try (FileWriter writer = new FileWriter(PATH.toFile())) {
                GSON.toJson(bookLender, writer);
                System.out.println("Запись завершена");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи задач в файл: " + e.getMessage());
        }
    }

}

class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(formatter));
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocalDate.parse(json.getAsString(), formatter);
    }
}
