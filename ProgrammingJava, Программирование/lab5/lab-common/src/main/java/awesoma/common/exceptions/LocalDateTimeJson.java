package awesoma.common.exceptions;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Adapter of LocalDateTime for Gson library to parse it
 */
public class LocalDateTimeJson extends TypeAdapter<LocalDateTime> {
    /**
     * @param out   JsonWriter that writes
     * @param value of LocalDateTime to parse
     * @throws IOException
     */
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        out.value(value.toString());
    }

    /**
     * @param in from where to read value
     * @return parsed LocalDateTime value
     * @throws IOException
     */
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        return LocalDateTime.parse(in.nextString());
    }
}
