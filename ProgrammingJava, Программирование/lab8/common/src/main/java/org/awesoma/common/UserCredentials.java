package org.awesoma.common;

import java.io.Serializable;

/**
 * This class represents user credentials: password and username
 *
 * @param username
 * @param password
 */
public record UserCredentials(String username, String password) implements Serializable {
}
