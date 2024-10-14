package org.awesoma.lab3.beans;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import  jakarta.inject.Named;


/**
 *
 * @author awesoma
 */
@Named
@SessionScoped
public class CalculatorBean implements Serializable {
    private String message = "Hello World!";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
