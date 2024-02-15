package awesoma.common.models;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "Movie")
public class Movie implements Comparable<Movie> {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Integer oscarsCount; //Значение поля должно быть больше 0, Поле может быть null
    private Long totalBoxOffice; //Значение поля должно быть больше 0
    private Float usaBoxOffice; //Поле не может быть null, Значение поля должно быть больше 0


    public Movie() {
    }

//    public Movie(
//            String name, Integer oscarsCount, Long totalBoxOffice,
//            Float usaBoxOffice, MovieGenre genre
//    ) {
//        this.id = ID;
//        this.name = name;
////        this.coordinates = coordinates;
//        this.oscarsCount = oscarsCount;
////        this.creationDate = LocalDateTime.now();
//        this.totalBoxOffice = totalBoxOffice;
//        this.usaBoxOffice = usaBoxOffice;
////        this.genre = genre;
////        this.operator = operator;
//        ID++;
//    }

    public Integer getId() {
        return id;
    }

    @XmlElement(name = "id")
    public void setId(Integer id) {
        this.id = id;
    }

//    public void setCoordinates(Coordinates coordinates) {
//        this.coordinates = coordinates;
//    }

//    public void setCreationDate(LocalDateTime creationDate) {
//        this.creationDate = creationDate;
//    }

    public String getName() {
        return name;
    }

    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public Integer getOscarsCount() {
        return oscarsCount;
    }

//    @XmlElement(name = "genre")
//    public void setGenre(MovieGenre genre) {
//        this.genre = genre;
//    }

//    public static int getID() {
//        return ID;
//    }

    @XmlElement(name = "oscarsCount")
    public void setOscarsCount(Integer oscarsCount) {
        this.oscarsCount = oscarsCount;
    }

    public Long getTotalBoxOffice() {
        return totalBoxOffice;
    }

    @XmlElement(name = "totalBoxOffice")
    public void setTotalBoxOffice(long totalBoxOffice) {
        this.totalBoxOffice = totalBoxOffice;
    }

    public Float getUsaBoxOffice() {
        return usaBoxOffice;
    }

    @XmlElement(name = "usaBoxOffice")
    public void setUsaBoxOffice(Float usaBoxOffice) {
        this.usaBoxOffice = usaBoxOffice;
    }

    @Override
    public int compareTo(Movie o) {
        return this.id - o.getId();
    }

//    public MovieGenre getGenre() {
//        return genre;
//    }

    //    public void setOperator(Person operator) {
//        this.operator = operator;
//    }

//    @Override
//    public int compareTo(Movie other) {
//        return this.id - other.id;
//    }
}
