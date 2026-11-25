package repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TextFileRepository<T> implements FileRepository<T> {
    private final Path filePath;
    private final Function<T, String> serializer;
    private final Function<String, T> deserializer;

    public TextFileRepository(Path filePath, Function<T, String> serializer, Function<String, T> deserializer) {
        this.filePath = filePath;
        this.serializer = serializer;
        this.deserializer = deserializer;
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            System.out.println("Error initializing data file: " + e.getMessage());
        }
    }

    @Override
    public List<T> findAll() {
        ensureFileExists();
        List<T> result = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (line == null || line.trim().isEmpty()) continue;
                T item = deserializer.apply(line);
                if (item != null) result.add(item);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void saveAll(List<T> items) {
        ensureFileExists();
        List<String> lines = new ArrayList<>();
        for (T item : items) {
            lines.add(serializer.apply(item));
        }
        try {
            Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}


