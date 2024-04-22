package awesoma.common.exceptions;

public class EnvVariableNotFoundException extends IllegalArgumentException {
    public EnvVariableNotFoundException() {
        super("Environment variable not found");
    }
}
