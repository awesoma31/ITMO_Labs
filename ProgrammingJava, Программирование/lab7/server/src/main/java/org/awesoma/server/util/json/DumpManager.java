package org.awesoma.server.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.awesoma.common.exceptions.EnvVariableNotFoundException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.Movie;
import org.awesoma.common.util.Validator;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Vector;

/**
 * This class is responsible for wiring/reading collection to/from file
 */
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

    public DumpManager(final String path, Validator validator) throws EnvVariableNotFoundException {
        if (path == null) {
            throw new EnvVariableNotFoundException();
        }
        try {
            this.path = path;
        } catch (IllegalArgumentException e) {
            throw new EnvVariableNotFoundException();
        }
        this.validator = validator;
    }

    private static Vector<Movie> getCollection(String path, Gson gson, Validator validator) throws IOException, ValidationException {
        var file = new File(path);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("File can't be created");
            }
        }
        if (!file.isFile()) {
            throw new IOException(path + " is not a valid file");
        }
        if (!file.canRead()) {
            throw new IOException("File can't be read");
        }
        try (
                var inputStream = new FileInputStream(file);
                var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            Vector<Movie> collection = gson.fromJson(reader, new TypeToken<Vector<Movie>>() {
            }.getType());

            if (collection == null) {
                collection = new Vector<>();
            }
            validator.validateCollection(collection);
            return collection;
        }
    }

    /**
     * @return Vector collection of Movie objects from json file
     * @throws IOException         if exception while opening/reading a file
     * @throws ValidationException if fields in the file are not valid
     */
    public synchronized Vector<Movie> readCollection() throws IOException, ValidationException {
        Vector<Movie> col = getCollection(path, gson, validator);
        validator.validateCollection(col);
        return col;
    }

    /**
     * writes the collection to the json file
     *
     * @param collection to write to the file
     * @throws IOException if exception while opening/writing file
     */
    public synchronized void writeCollection(final Vector<Movie> collection) throws IOException {
        if (collection == null) {
            throw new IllegalArgumentException("Collection to write cannot be null");
        }
        var file = new File(path);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("File can't be created");
            }
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
