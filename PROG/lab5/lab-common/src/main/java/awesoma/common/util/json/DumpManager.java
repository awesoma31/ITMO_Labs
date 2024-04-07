package awesoma.common.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import awesoma.common.exceptions.EnvVariableNotFoundException;
import awesoma.common.exceptions.ValidationException;
import awesoma.common.models.Movie;
import awesoma.common.util.Validator;

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
//            setDateFormat("MMM dd, yyyy HH:mm:ss").
            create();

    public DumpManager(final String path, Validator validator) throws EnvVariableNotFoundException {
        if (path == null) {
            throw new EnvVariableNotFoundException();
        }
        this.path = path;
        this.validator = validator;
    }

    /**
     * @return Vector collection of Movie objects from json file
     * @throws IOException         if exception while opening/reading a file
     * @throws ValidationException if fields in the file are not valid
     */
    public Vector<Movie> readCollection() throws IOException, ValidationException {
        File file = new File(path);
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
                FileInputStream inputStream = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            Vector<Movie> collection = gson.fromJson(reader, new TypeToken<Vector<Movie>>() {
            }.getType());

            validator.validateCollection(collection);
            return collection;
        }
    }

    /**
     * @param path to the json file from where to read the data
     * @return Vector collection of Movie objects from json file
     * @throws IOException         if exception while opening/reading a file
     * @throws ValidationException if fields in the file are not valid
     */
    public Vector<Movie> readCollection(String path) throws IOException, ValidationException {
        File file = new File(path);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("File can't be created");
            }
        }
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

    /**
     * writes the collection to the json file
     *
     * @param collection to write to the file
     * @throws IOException if exception while opening/writing file
     */
    public void writeCollection(final Vector<Movie> collection) throws IOException {
        if (collection == null) {
            throw new IllegalArgumentException("Collection to write cannot be null");
        }
        File file = new File(path);
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
