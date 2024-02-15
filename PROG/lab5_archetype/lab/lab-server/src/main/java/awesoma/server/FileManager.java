package awesoma.server;

import awesoma.common.models.Movie;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.stream.Collectors;


public class FileManager {
    public static void loadCollection(String path) throws IOException, JAXBException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String body = br.lines().collect(Collectors.joining());
        StringReader reader = new StringReader(body);
        JAXBContext context = JAXBContext.newInstance(Movie.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Movie movie = (Movie) unmarshaller.unmarshal(reader);

        System.out.println(movie.toString());
    }

    public static void saveCollection(Movie movie, String filename) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(Movie.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(movie, new File(filename));
    }
}
