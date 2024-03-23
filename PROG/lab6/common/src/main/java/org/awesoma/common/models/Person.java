package org.awesoma.common.models;


import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.util.Validator;
import org.awesoma.common.util.json.LocalDateTimeJson;

import java.util.Date;
import java.util.Objects;

/**
 * Class represents a person
 */
public class Person {
    private final String name; //notNull, notEmpty
    private final Date birthday; // notNull
    private final float weight; // >0
    private final org.awesoma.common.models.Color eyeColor; //mbNull
    private final org.awesoma.common.models.Country nationality; // notNull

    public Person(
            String name, Date birthday,
            float weight, org.awesoma.common.models.Color eyeColor,
            org.awesoma.common.models.Country nationality
    ) throws ValidationException {
        Validator.validateName(name);
        this.name = name;
        this.birthday = birthday;
        Validator.isAboveZero(weight);
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

    public String getName() {
        return name;
    }

    public float getWeight() {
        return weight;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Country getNationality() {
        return nationality;
    }
}
