package awesoma.common.util.json;

import awesoma.common.exceptions.ValidationException;
import awesoma.common.models.Movie;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Vector;

public class DumpManager {
    private final String path;
    private final Validator validator;
    private final Gson gson = new GsonBuilder().
            registerTypeAdapter(
                    LocalDateTime.class,
                    new LocalDateTimeJson())
            .enableComplexMapKeySerialization().
            serializeNulls().
            create();

    public DumpManager(final String path, Validator validator) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty");
        }
        this.path = path;
        this.validator = validator;
    }

    public Vector<Movie> readCollection() throws IOException, ValidationException {
        File file = new File(path);
        if (!file.isFile()) {
            throw new IOException(path + " is not a valid file");
        }
        if (!file.canRead()) {
            throw new IOException("File can't be read");
        }
        try (
                FileInputStream inputStream = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            Vector<Movie> collection = gson.fromJson(reader, new TypeToken<Vector<Movie>>() {
            }.getType());

            validator.validateCollection(collection);
            return collection;
        }
    }

    public Vector<Movie> readCollection(String path) throws IOException, ValidationException {
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
            Vector<Movie> collection = gson.fromJson(reader, new TypeToken<Vector<Movie>>() {
            }.getType());

            validator.validateCollection(collection);
            return collection;
        }
    }

    public void writeCollection(final Vector<Movie> collection) throws IOException {
        if (collection == null) {
            throw new IllegalArgumentException("Collection to write cannot be null");
        }
        File file = new File(path);
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
