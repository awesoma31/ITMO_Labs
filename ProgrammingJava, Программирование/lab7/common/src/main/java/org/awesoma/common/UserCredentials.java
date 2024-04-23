package org.awesoma.common;

import java.io.Serializable;

public record UserCredentials(String username, byte[] password) implements Serializable {
}
