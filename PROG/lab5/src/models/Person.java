package models;

/**
 *
 */
public class Person {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private java.time.ZonedDateTime birthday; //Поле не может быть null
    private Double weight; //Поле может быть null, Значение поля должно быть больше 0
    private Color eyeColor; //Поле не может быть null
    private Country nationality; //Поле не может быть null
}
