package awesoma.common.models;

import awesoma.common.csv.CsvBean;
import com.opencsv.bean.CsvBindByPosition;

import java.time.LocalDateTime;


public class Movie extends CsvBean implements Comparable<Movie> {
    @CsvBindByPosition(position = 0)
    private Integer id; // notNull, >0, unique, auto
    @CsvBindByPosition(position = 1)
    private String name; //notNull, notEmpty
    @CsvBindByPosition(position = 2)
    private Coordinates coordinates; // notNull
    @CsvBindByPosition(position = 3)
    private java.time.LocalDateTime creationDate; // notNull, auto
    @CsvBindByPosition(position = 4)
    private Integer oscarsCount; //mbNull, >0
    @CsvBindByPosition(position = 5)
    private int totalBoxOffice; // >0
    @CsvBindByPosition(position = 6)
    private Long usaBoxOffice; // notNull, >0
    @CsvBindByPosition(position = 7)
    private MovieGenre genre; //mbNull
    @CsvBindByPosition(position = 8)
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

    public void setOscarsCount(int oscarsCount) {
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
