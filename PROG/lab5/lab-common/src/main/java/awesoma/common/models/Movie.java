package awesoma.common.models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;
import java.util.Date;

public class Movie implements Comparable<Movie> {
    @XmlAttribute
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    private String name; //Поле не может быть null, Строка не может быть пустой
    private Integer oscarsCount; //Значение поля должно быть больше 0, Поле может быть null


    private Long totalBoxOffice; //Значение поля должно быть больше 0

    private Float usaBoxOffice; //Поле не может быть null, Значение поля должно быть больше 0

    private Coordinates coordinates;

    private Date creationDate;

    private MovieGenre genre;

    private Person operator;

    public Movie() {
    }

    public Movie(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Movie(
            Integer id, String name,
            Integer oscarsCount, Long totalBoxOffice,
            Float usaBoxOffice, Coordinates coordinates,
            Date creationDate, MovieGenre genre,
            Person operator
    ) {
        this.id = id;
        this.name = name;
        this.oscarsCount = oscarsCount;
        this.totalBoxOffice = totalBoxOffice;
        this.usaBoxOffice = usaBoxOffice;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.genre = genre;
        this.operator = operator;
    }

    public Integer getId() {
        return id;
    }

    @XmlElement
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Person getOperator() {
        return operator;
    }

    @Override
    public int compareTo(Movie o) {
        return this.id - o.getId();
    }

    public Integer getOscarsCount() {
        return oscarsCount;
    }

    public void setOscarsCount(Integer oscarsCount) {
        this.oscarsCount = oscarsCount;
    }
}
//@XmlType(propOrder = { "id", "name" })

