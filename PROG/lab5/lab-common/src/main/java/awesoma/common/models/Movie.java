package awesoma.common.models;

import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;

public class Movie implements Comparable<Movie> {
    private Integer id; // notNull, >0, unique, auto
    private String name; //notNull, notEmpty
    private Coordinates coordinates; // notNull
    private java.time.LocalDateTime creationDate; // notNull, auto
    private Integer oscarsCount; //mbNull, >0
    private int totalBoxOffice; // >0
    private Long usaBoxOffice; // notNull, >0
    private MovieGenre genre; //mbNull
    private Person operator; // notNull

    public Movie() {
    }

    public Movie(
            Integer id, String name,
            Integer oscarsCount, int totalBoxOffice,
            Long usaBoxOffice, Coordinates coordinates,
            LocalDateTime creationDate, MovieGenre genre,
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

    public int getId() {
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

    public Person getOperator() {
        return operator;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    @Override
    public int compareTo(Movie other) {
        return Integer.compare(this.id, other.id);
    }


    public Integer getOscarsCount() {
        return oscarsCount;
    }

    public void setOscarsCount(int oscarsCount) {
        this.oscarsCount = oscarsCount;
    }

    public Integer getTotalBoxOffice() {
        return totalBoxOffice;
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
}