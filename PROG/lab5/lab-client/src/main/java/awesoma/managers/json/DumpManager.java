package awesoma.managers.json;

import awesoma.common.models.Movie;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Vector;

public class DumpManager {
    private final String path;
    private final Gson gson = new GsonBuilder().
            registerTypeAdapter(
                    LocalDateTime.class,
                    new LocalDateTimeJson())
            .enableComplexMapKeySerialization().
            serializeNulls().
            create();

    public DumpManager(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty");
        }
        this.path = path;
    }

    public Vector<Movie> readCollection() throws IOException {
        File file = new File(path);
//        if (!file.exists()) {
//            file.createNewFile();
//        }
        if (!file.isFile()) {
            throw new IOException(path + " is not a valid file");
        }
        if (!file.canRead()) {
            throw new IOException("File can't be read");
        }
        try (FileInputStream inputStream = new FileInputStream(file); BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return gson.fromJson(reader, new TypeToken<Vector<Movie>>() {
            }.getType());
        }
    }

    public Vector<Movie> readCollection(String path) throws IOException {
        File file = new File(path);
        if (!file.isFile()) {
            throw new IOException(path + " is not a valid file");
        }
        if (!file.canRead()) {
            throw new IOException("File can not be read");
        }
        try (
                FileInputStream inputStream = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            return gson.fromJson(reader, new TypeToken<Vector<Movie>>() {
            }.getType());
        }
    }

    public void writeCollection(final Vector<Movie> collection) throws IOException {
        if (collection == null) {
            throw new IllegalArgumentException("Collection to write cannot be null");
        }
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        if (!file.isFile()) {
            throw new IOException(path + " is not a file");
        }
        if (!file.canWrite()) {
            throw new IOException("File can't be written to");
        }
        try (PrintWriter writer = new PrintWriter(file)) {
            gson.toJson(collection, writer);
        }
    }
}
