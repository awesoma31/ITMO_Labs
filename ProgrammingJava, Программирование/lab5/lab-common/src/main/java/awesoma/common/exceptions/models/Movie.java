package awesoma.common.exceptions.models;

import awesoma.common.exceptions.Validator;
import awesoma.common.exceptions.exceptions.ValidationException;

import java.time.LocalDateTime;

/**
 * Class represents element of collection
 */
public class Movie implements Comparable<Movie> {
    private Integer id; // notNull, >0, unique, auto
    private String name; //notNull, notEmpty
    private Coordinates coordinates; // notNull
    private LocalDateTime creationDate; // notNull, auto
    private Integer oscarsCount; // >0
    private int totalBoxOffice; // >0
    private Long usaBoxOffice; // notNull, >0
    private MovieGenre genre; //mbNull
    private Person operator; // notNull

    public Movie(
            Integer id, String name,
            Integer oscarsCount, int totalBoxOffice,
            Long usaBoxOffice, Coordinates coordinates,
            LocalDateTime creationDate, MovieGenre genre,
            Person operator
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

    public int getId() {
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

    public Person getOperator() {
        return operator;
    }

    public void setOperator(Person operator) {
        this.operator = operator;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }

    @Override
    public int compareTo(Movie other) {
        return Integer.compare(this.id, other.id);
    }

    public Integer getOscarsCount() {
        return oscarsCount;
    }

    public void setOscarsCount(Integer oscarsCount) {
        this.oscarsCount = oscarsCount;
    }

    public Integer getTotalBoxOffice() {
        return totalBoxOffice;
    }

    public void setTotalBoxOffice(int totalBoxOffice) {
        this.totalBoxOffice = totalBoxOffice;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", oscarsCount=" + oscarsCount +
                ", totalBoxOffice=" + totalBoxOffice +
                ", usaBoxOffice=" + usaBoxOffice +
                ", genre=" + genre +
                ", operator=" + operator +
                '}';
    }

    public Long getUsaBoxOffice() {
        return usaBoxOffice;
    }

    public void setUsaBoxOffice(Long usaBoxOffice) {
        this.usaBoxOffice = usaBoxOffice;
    }

}
