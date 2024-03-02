package awesoma.common.util;

import awesoma.common.exceptions.ConvertationException;
import awesoma.common.exceptions.ValidationException;
import awesoma.common.models.Color;
import awesoma.common.models.Country;
import awesoma.common.models.Movie;
import awesoma.common.models.MovieGenre;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class represents a validator of Movie fields
 */
public class Validator {
    public static void validateName(String name) throws ValidationException {
        if (name.isEmpty()) {
            throw new ValidationException("Name cant be empty");
        }
    }

    public static Long convertYFromString(String y) throws ConvertationException, ValidationException {
        long res;
        if (y.isEmpty()) {
            throw new ConvertationException("Y cant be null");
        } else {
            try {
                res = Long.parseLong(y);
                if (res >= 117) {
                    throw new ConvertationException("Y cant be >=117");
                }
                return res;
            } catch (NumberFormatException e) {
                throw new ConvertationException("Couldn't convert y to Long");
            }
        }
    }

    public static double convertXFromString(String x) throws ConvertationException, ValidationException {
        double res;
        try {
            res = Double.parseDouble(x);
        } catch (NumberFormatException e) {
            throw new ConvertationException("Couldn't convert x to double");
        }
        return res;
    }

    public static Integer convertOscarsCountFromString(String oc) throws ValidationException, ConvertationException {
        Integer res;
        // >0
        if (oc.isEmpty()) {
            return null;
        } else {
            try {
                res = Integer.parseInt(oc);
                isAboveZero(res);
                return res;
            } catch (NumberFormatException e) {
                throw new ConvertationException("Couldn't convert oscarsCount to Integer");
            } catch (ValidationException e) {
                throw new ConvertationException(e.getMessage());
            }
        }
    }

    public static int convertTBOFromString(String tbo) throws ValidationException, ConvertationException {
        int res;
        if (tbo.isEmpty()) {
            throw new ConvertationException("TotalBoxOffice cant be null");
        } else {
            try {
                res = Integer.parseInt(tbo);
                isAboveZero(res);
                return res;
            } catch (NumberFormatException e) {
                throw new ConvertationException("Couldn't convert TotalBoxOffice to int");
            } catch (ValidationException e) {
                throw new ConvertationException(e.getMessage());
            }
        }
    }

    public static float convertWeightFromString(String weight) throws ValidationException, ConvertationException {
        float res;
        if (weight.isEmpty()) {
            throw new ConvertationException("weight cant be null");
        } else {
            try {
                res = Float.parseFloat(weight);
                isAboveZero(res);
                return res;
            } catch (NumberFormatException e) {
                throw new ConvertationException("Couldn't convert weight to float");
            } catch (ValidationException e) {
                throw new ConvertationException(e.getMessage());
            }
        }
    }

    public static Date convertDateFromString(String date) throws ConvertationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        if (date.isEmpty()) {
            throw new ConvertationException("Date cant be empty");
        } else {
            try {
                return dateFormat.parse(date);
            } catch (ParseException e) {
                throw new ConvertationException("Couldn't convert your input to Date");
            }
        }
    }

    public static Long convertUBOFromString(String ubo) throws ValidationException, ConvertationException {
        Long res;
        if (ubo.isEmpty()) {
            throw new ConvertationException("UsaBoxOffice cant be null");
        } else {
            try {
                res = Long.parseLong(ubo);
                isAboveZero(res);
                return res;
            } catch (NumberFormatException e) {
                throw new ConvertationException("Couldn't convert UsaBoxOffice to Long");
            } catch (ValidationException e) {
                throw new ConvertationException(e.getMessage());
            }
        }
    }

    public static MovieGenre convertGenreFromString(String genre) throws ConvertationException {
        MovieGenre res;
        if (genre.isEmpty()) {
            return null;
        } else {
            try {
                res = MovieGenre.valueOf(genre.toUpperCase());
                return res;
            } catch (IllegalArgumentException e) {
                throw new ConvertationException("No such genre");
            }
        }
    }

    public static Color convertEyeColorFromString(String genre) throws ConvertationException {
        Color res;
        if (genre.isEmpty()) {
            return null;
        } else {
            try {
                res = Color.valueOf(genre.toUpperCase());
                return res;
            } catch (IllegalArgumentException e) {
                throw new ConvertationException("No such color");
            }
        }
    }

    public static Country convertCountryFromString(String country) throws ConvertationException {
        Country res;
        if (country.isEmpty()) {
            throw new ConvertationException("Nationality cant be null");
        } else {
            try {
                res = Country.valueOf(country.toUpperCase());
                return res;
            } catch (IllegalArgumentException e) {
                throw new ConvertationException("No such nationality");
            }
        }
    }

    public static <T extends Number & Comparable<T>> void isAboveZero(T x) throws ValidationException {
        if (x.doubleValue() <= 0) {
            throw new ValidationException("This number less than 0");
        }
    }

    /**
     * validates the collection
     *
     * @param collection which must be validated
     * @throws ValidationException if collection failed validation
     */
    public void validateCollection(Vector<Movie> collection) throws ValidationException {
        validateId(collection);
        validateName(collection);
        validateCoordinates(collection);
        validateCreationDate(collection);
        validateOscarsCount(collection);
        validateTotalBoxOffice(collection);
        validateUsaBoxOffice(collection);
        validateGenre(collection);
        validateOperator(collection);
    }

    /**
     * validates id of every element of the collection
     *
     * @param collection that needs validation
     * @throws ValidationException if at least 1 id failed validation
     */
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

    /**
     * validates name of every element of the collection
     *
     * @param collection where to validate names
     * @throws ValidationException if at least 1 name failed validation
     */
    public void validateName(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getName().isEmpty()) {
                throw new ValidationException("Name cant be null, Movie id = " + m.getId());
            }
        }
    }

    /**
     * validates coordinates of every element of the collection
     *
     * @param collection where to validate
     * @throws ValidationException if at least 1 coordinate failed validation
     */
    public void validateCoordinates(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getCoordinates().getY() >= 117) {
                throw new ValidationException("Y must be less than 117, but <" + m.getCoordinates().getY() + "> given");
            }
        }
    }

    /**
     * validates creationDate of every element of the collection
     *
     * @param collection where to validate
     * @throws ValidationException if at least 1 creationDate failed validation
     */
    public void validateCreationDate(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getCreationDate().toString().isEmpty()) {
                throw new ValidationException("Creation Date cant be null, Movie id = " + m.getId());
            }
        }
    }

    /**
     * validates totalBoxOffice of every element of the collection
     *
     * @param collection where to validate
     * @throws ValidationException if at least 1 field failed validation
     */
    public void validateTotalBoxOffice(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getTotalBoxOffice() <= 0) {
                throw new ValidationException("totalBoxOffice must be above null, but <" + m.getTotalBoxOffice() + "> given");
            }
        }
    }

    /**
     * validates oscarsCount of every element of the collection
     *
     * @param collection where to validate
     * @throws ValidationException if at least 1 oscarsCount failed validation
     */
    public void validateOscarsCount(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getOscarsCount() <= 0) {
                throw new ValidationException("OscarsCount must be above null, but <" + m.getOscarsCount() + "> given");
            }
        }
    }

    /**
     * validates usaBoxOffice of every element of the collection
     *
     * @param collection where to validate
     * @throws ValidationException if at least 1 field failed validation
     */
    public void validateUsaBoxOffice(Vector<Movie> collection) throws ValidationException {
        for (Movie m : collection) {
            if (m.getUsaBoxOffice() <= 0) {
                throw new ValidationException("usaBoxOffice must be above null, but <" + m.getUsaBoxOffice() + "> given");
            }
        }
    }

    /**
     * validates genre of every element of the collection
     *
     * @param collection where to validate
     * @throws ValidationException if at least 1 field failed validation, if genre not found
     */
    public void validateGenre(Vector<Movie> collection) throws ValidationException {
        ArrayList<String> genres = MovieGenre.getVals();
        for (Movie m : collection) {
            if (!genres.contains(m.getGenre().name())) {
                throw new ValidationException("Genre <" + m.getGenre() + "> not found");
            }
        }
    }

    /**
     * validates operator of every element of the collection
     *
     * @param collection where to validate
     * @throws ValidationException if at least 1 field failed validation, if operator.name is empty or nationality
     *                             not found or eye color not found etc
     */
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




