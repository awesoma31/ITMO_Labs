package awesoma.common.util.json;

import awesoma.common.exceptions.ValidationException;
import awesoma.common.models.Color;
import awesoma.common.models.Country;
import awesoma.common.models.Movie;
import awesoma.common.models.MovieGenre;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class Validator {
    public void validateCollection(Vector<Movie> collection) throws ValidationException {
        Field[] fields = collection.getClass().getFields();
        validateId(collection);
        validateName(collection);
        validateCoordinates(collection);
        validateLocalDateTime(collection);
        validateOscarsCount(collection);
        validateTotalBoxOffice(collection);
        validateUsaBoxOffice(collection);
        validateGenre(collection);
        validateOperator(collection);
    }

    public void validateId(Vector<Movie> collection) throws ValidationException {
        ArrayList<Integer> idList = new ArrayList<>();
        for (Movie m : collection) {
            if (m.getId() <= 0) {
                throw new ValidationException("Negative ID found: <" + m.getId() + ">");
            }
            idList.add(m.getId());
        }

        Set<Integer> set = new HashSet<>();

        for (Integer element : idList) {
            if (!set.add(element)) {
                throw new ValidationException("Duplicate IDs found");
            }
        }
    }

    public void validateName(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getName().isEmpty()) {
                throw new ValidationException("Name cant be null, Movie id = " + m.getId());
            }
        }
    }

    public void validateCoordinates(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getCoordinates().getY() >= 117) {
                throw new ValidationException("Y must be less than 117, but <" + m.getCoordinates().getY() + "> given");
            }
        }
    }

    public void validateLocalDateTime(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getCreationDate().toString().isEmpty()) {
                throw new ValidationException("Creation Date cant be null, Movie id = " + m.getId());
            }
        }
    }

    public void validateOscarsCount(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getOscarsCount() <= 0) {
                throw new ValidationException("OscarsCount must be above null, but <" + m.getOscarsCount() + "> given");
            }
        }
    }

    public void validateTotalBoxOffice(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getTotalBoxOffice() <= 0) {
                throw new ValidationException("totalBoxOffice must be above null, but <" + m.getTotalBoxOffice() + "> given");
            }
        }
    }

    public void validateUsaBoxOffice(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getUsaBoxOffice() <= 0) {
                throw new ValidationException("usaBoxOffice must be above null, but <" + m.getUsaBoxOffice() + "> given");
            }
        }
    }

    public void validateGenre(Vector<Movie> collection) throws ValidationException {
        ArrayList<String> genres = MovieGenre.getVals();
        for (Movie m : collection) {
            if (!genres.contains(m.getGenre().name())) {
                throw new ValidationException("Genre <" + m.getGenre() + "> not found");
            }
        }
    }

    public void validateOperator(Vector<Movie> collection) throws ValidationException {
        ArrayList<String> colors = Color.getVals();
        ArrayList<String> countries = Country.getVals();

        for (Movie m : collection) {
            if (!colors.contains(m.getOperator().getEyeColor().name())) {
                throw new ValidationException("Eye color <" + m.getOperator().getEyeColor().name() + "> not found");
            }
            if (!countries.contains(m.getOperator().getNationality().name())) {
                throw new ValidationException("Nationality <" + m.getOperator().getNationality().name() + "> not found");
            }
            if (m.getOperator().getName().isEmpty()) {
                throw new ValidationException("Operator name cant be empty");
            } else if (m.getOperator().getWeight() <= 0) {
                throw new ValidationException("Operator weight must be above 0, but <" + m.getOperator().getWeight() + "> given");
            }
        }
    }
}



