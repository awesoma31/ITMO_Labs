package org.awesoma.common.models;


import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.util.Validator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class represents a person
 */
public class Person implements Serializable {
    private final String name; //notNull, notEmpty
    private final LocalDateTime birthday; // notNull
    private final float weight; // >0
    private final Color eyeColor; //mbNull
    private final Country nationality; // notNull

    public Person(
            String name, LocalDateTime birthday,
            float weight, Color eyeColor,
            Country nationality
    ) throws ValidationException {
        Validator.validateName(name);
        this.name = name;
        this.birthday = birthday;
        Validator.isAboveZero(weight);
        this.weight = weight;
        this.eyeColor = eyeColor;
        this.nationality = nationality;
    }


    public Person() {
        name = null;
        birthday = null;
        weight = (float) 0;
        eyeColor = null;
        nationality = null;
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

    public LocalDateTime getBirthday() {
        return birthday;
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




    public static final class Builder {
        private String name;
        private LocalDateTime birthday;
        private float weight;
        private Color eyeColor;
        private Country nationality;

        private Builder() {
        }

        public static Builder aPerson() {
            return new Builder();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder birthday(LocalDateTime birthday) {
            this.birthday = birthday;
            return this;
        }

        public Builder weight(float weight) {
            this.weight = weight;
            return this;
        }

        public Builder eyeColor(Color eyeColor) {
            this.eyeColor = eyeColor;
            return this;
        }

        public Builder nationality(Country nationality) {
            this.nationality = nationality;
            return this;
        }

        public Person build() {
            try {
                return new Person(name, birthday, weight, eyeColor, nationality);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
