package awesoma.server;

import awesoma.common.models.Movie;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.ArrayList;

public final class Server {
    static final int COMMAND_HISTORY_MAX_SIZE = 13;

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void addToHistory(String command, ArrayList<String> comHist) {
        if (comHist.size() < COMMAND_HISTORY_MAX_SIZE) {
            comHist.add(command);
        } else {
            comHist.remove(0);
            comHist.add(command);
        }
    }

    public static void main(String[] args) throws JAXBException {
//        Movie movie1 = new Movie(
//                "Rambo", 1,
//                2L, 3.0F, MovieGenre.COMEDY
//        );

        Movie movie = new Movie();
        movie.setId(1);
        movie.setName("Rambo");
        movie.setOscarsCount(1);
        movie.setTotalBoxOffice(2L);
        movie.setUsaBoxOffice(3F);

        JAXBContext context = JAXBContext.newInstance(Movie.class);
        JAXBContext context1 = JAXBContext.newInstance(Movie.class);
        Marshaller mar = context1.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(movie, new File("my_model.xml"));

//        FileManager.saveCollection(movie,
//                "my_model.xml"
//        );

//        FileManager.loadCollection("C:\\Users\\gwert\\Documents\\ITMO_Labs\\PROG\\lab5_archetype\\lab\\lab-common\\src\\model.xml");
//        ArrayList<String> commandHistory = new ArrayList<>();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
//
//            for (;;) {
//                String command = reader.readLine();
//                if (command == null) {
//                    System.exit(0);
//                }
//
//                switch (command) {
//                    case "exit":
//                    case "q":
//                        System.exit(0);
//                        commandHistory.clear();
//                        break;
//                    case "help":
//                        addToHistory(command, commandHistory);
//                        Help.execute();
//                        break;
//                    case "history":
//                        System.out.println("History of executed commands:");
//                        System.out.println("-----");
//                        for (String cmnd : commandHistory) {
//                            System.out.println(cmnd);
//                        }
//                        System.out.println("-----");
//                        break;
//                    default:
//                        System.err.println("Unknown command, please try again");
//                }
//            }
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            System.exit(1);
//        }
    }
}
