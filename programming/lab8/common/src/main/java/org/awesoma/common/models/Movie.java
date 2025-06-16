package org.awesoma.common.models;

import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.util.Validator;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Class represents element of collection
 */
public class Movie implements Comparable<Movie>, Serializable {
    private final Coordinates coordinates; // notNull
    private final Integer oscarsCount; // >0
    private final int totalBoxOffice; // >0
    private final Long usaBoxOffice; // notNull, >0
    private final MovieGenre genre; //mbNull
    private final Person operator; // notNull
    private String owner;
    private Integer id; // notNull, >0, unique, auto
    private String name; //notNull, notEmpty
    private LocalDateTime creationDate; // notNull, auto

    public Movie() {
        this.coordinates = null;
        this.oscarsCount = 0;
        this.totalBoxOffice = 0;
        this.usaBoxOffice = 0L;
        this.genre = null;
        this.operator = null;
        this.owner = null;
        this.id = 0;
        this.name = null;
        this.creationDate = null;
    }

    public Movie(
            String name,
            Integer oscarsCount,
            int totalBoxOffice,
            Long usaBoxOffice,
            Coordinates coordinates,
            MovieGenre genre,
            Person operator
    ) throws ValidationException {
        Validator.validateName(name);
        this.name = name;
        Validator.isAboveZero(oscarsCount);
        this.oscarsCount = oscarsCount;
        Validator.isAboveZero(totalBoxOffice);
        this.totalBoxOffice = totalBoxOffice;
        Validator.isAboveZero(usaBoxOffice);
        this.usaBoxOffice = usaBoxOffice;
        this.coordinates = coordinates;
        this.genre = genre;
        this.operator = operator;
        this.creationDate = LocalDateTime.now();
    }

    public Movie(Coordinates coordinates, Integer oscarsCount, int totalBoxOffice, Long usaBoxOffice, MovieGenre genre, Person operator, Integer id, String name, LocalDateTime creationDate) {
        this.coordinates = coordinates;
        this.oscarsCount = oscarsCount;
        this.totalBoxOffice = totalBoxOffice;
        this.usaBoxOffice = usaBoxOffice;
        this.genre = genre;
        this.operator = operator;
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
    }

    public Movie(
            Integer id, String name,
            Integer oscarsCount, int totalBoxOffice,
            Long usaBoxOffice, org.awesoma.common.models.Coordinates coordinates,
            LocalDateTime creationDate, org.awesoma.common.models.MovieGenre genre,
            org.awesoma.common.models.Person operator
    ) throws ValidationException {
        this.id = id;
        Validator.validateName(name);
        this.name = name;
        Validator.isAboveZero(oscarsCount);
        this.oscarsCount = oscarsCount;
        Validator.isAboveZero(totalBoxOffice);
        this.totalBoxOffice = totalBoxOffice;
        Validator.isAboveZero(usaBoxOffice);
        this.usaBoxOffice = usaBoxOffice;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.genre = genre;
        this.operator = operator;
    }

    public Movie(
            String owner,
            Integer id, String name,
            Integer oscarsCount, int totalBoxOffice,
            Long usaBoxOffice, org.awesoma.common.models.Coordinates coordinates,
            LocalDateTime creationDate, org.awesoma.common.models.MovieGenre genre,
            org.awesoma.common.models.Person operator
    ) throws ValidationException {
        this.owner = owner;
        this.id = id;
        Validator.validateName(name);
        this.name = name;
        Validator.isAboveZero(oscarsCount);
        this.oscarsCount = oscarsCount;
        Validator.isAboveZero(totalBoxOffice);
        this.totalBoxOffice = totalBoxOffice;
        Validator.isAboveZero(usaBoxOffice);
        this.usaBoxOffice = usaBoxOffice;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.genre = genre;
        this.operator = operator;
    }

    public Movie(
            String owner,
            String name,
            Integer oscarsCount, int totalBoxOffice,
            Long usaBoxOffice, org.awesoma.common.models.Coordinates coordinates,
            LocalDateTime creationDate, org.awesoma.common.models.MovieGenre genre,
            org.awesoma.common.models.Person operator
    ) throws ValidationException {
        this.owner = owner;
        Validator.validateName(name);
        this.name = name;
        Validator.isAboveZero(oscarsCount);
        this.oscarsCount = oscarsCount;
        Validator.isAboveZero(totalBoxOffice);
        this.totalBoxOffice = totalBoxOffice;
        Validator.isAboveZero(usaBoxOffice);
        this.usaBoxOffice = usaBoxOffice;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.genre = genre;
        this.operator = operator;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public org.awesoma.common.models.Person getOperator() {
        return operator;
    }

    public org.awesoma.common.models.MovieGenre getGenre() {
        return genre;
    }

    @Override
    public int compareTo(Movie other) {
        return Integer.compare(this.id, other.id);
    }

    public Integer getOscarsCount() {
        return oscarsCount;
    }

    public Integer getTotalBoxOffice() {
        return totalBoxOffice;
    }

    public org.awesoma.common.models.Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getUsaBoxOffice() {
        return usaBoxOffice;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "owner=" + owner +
                ", id=" + id +
                ", name=" + name +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", oscarsCount=" + oscarsCount +
                ", totalBoxOffice=" + totalBoxOffice +
                ", usaBoxOffice=" + usaBoxOffice +
                ", genre=" + genre +
                ", operator=" + operator +
                '}';
    }


    public static final class Builder {
        private Coordinates coordinates;
        private Integer oscarsCount;
        private int totalBoxOffice;
        private Long usaBoxOffice;
        private MovieGenre genre;
        private Person operator;
        private String owner;
        private String name;
        private LocalDateTime creationDate;

        public Builder() {
        }

        public static Builder aMovie() {
            return new Builder();
        }

        public Builder coordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public Builder oscarsCount(Integer oscarsCount) {
            this.oscarsCount = oscarsCount;
            return this;
        }

        public Builder totalBoxOffice(int totalBoxOffice) {
            this.totalBoxOffice = totalBoxOffice;
            return this;
        }

        public Builder usaBoxOffice(Long usaBoxOffice) {
            this.usaBoxOffice = usaBoxOffice;
            return this;
        }

        public Builder genre(MovieGenre genre) {
            this.genre = genre;
            return this;
        }

        public Builder operator(Person operator) {
            this.operator = operator;
            return this;
        }

        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Movie build() {
            try {
                return new Movie(owner, null, name, oscarsCount, totalBoxOffice, usaBoxOffice, coordinates, creationDate, genre, operator);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
