package exceptions;

import java.io.FileNotFoundException;

public class PasswdNotFoundException extends FileNotFoundException {
    public PasswdNotFoundException(String message) {
        super(message);
    }
}
