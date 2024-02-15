package awesoma.common.models;


import java.time.LocalDateTime;
import java.util.Objects;

public class Person {

    private String name; //Поле не может быть null, Строка не может быть пустой

    private LocalDateTime birthday; //Поле не может быть null

    private Double weight; //Поле может быть null, Значение поля должно быть больше 0

    private Color eyeColor; //Поле не может быть null

    private Country nationality; //Поле не может быть null

    public Person() {
    }

    public Person(
            String name, LocalDateTime birthday,
            Double weight, Color eyeColor,
            Country nationality) {
        this.name = name;
        this.birthday = birthday;
        this.weight = weight;
        this.eyeColor = eyeColor;
        this.nationality = nationality;
    }

    // TODO нормальное сравнение
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name) &&
                Objects.equals(birthday, person.birthday) &&
                Objects.equals(weight, person.weight) &&
                eyeColor == person.eyeColor &&
                nationality == person.nationality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, birthday, weight, eyeColor, nationality);
    }
}
