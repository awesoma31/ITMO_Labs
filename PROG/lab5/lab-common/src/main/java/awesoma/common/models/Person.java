package awesoma.common.models;


import java.util.Date;
import java.util.Objects;

public class Person {
    private String name; //notNull, notEmpty
    private java.util.Date birthday; // notNull
    private float weight; // >0
    private Color eyeColor; //mbNull
    private Country nationality; // notNull

    public Person() {
    }

    public Person(
            String name, Date birthday,
            float weight, Color eyeColor,
            Country nationality) {
        this.name = name;
        this.birthday = birthday;
        this.weight = weight;
        this.eyeColor = eyeColor;
        this.nationality = nationality;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", birthday=" + birthday +
                ", weight=" + weight +
                ", eyeColor=" + eyeColor +
                ", nationality=" + nationality +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
