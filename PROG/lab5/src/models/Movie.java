package models;

public class Movie {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer oscarsCount; //Значение поля должно быть больше 0, Поле может быть null
    private long totalBoxOffice; //Значение поля должно быть больше 0
    private Float usaBoxOffice; //Поле не может быть null, Значение поля должно быть больше 0
    private MovieGenre genre; //Поле может быть null
    private Person operator; //Поле не может быть null
}
