package org.awesoma;

import org.awesoma.common.exceptions.UnrecognisedCommandException;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws IOException, UnrecognisedCommandException {
        Client client = new Client("localhost", 8000);
        client.run();
    }
}
