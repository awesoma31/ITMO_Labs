package org.awesoma.common.util;

import org.awesoma.common.exceptions.ConversionException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.Color;
import org.awesoma.common.models.Country;
import org.awesoma.common.models.MovieGenre;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class represents a validator of Movie fields
 */
public class Validator {
    public static void validateName(String name) throws ValidationException {
        if (name.isEmpty()) {
            throw new ValidationException("Name cant be empty");
        }
    }

    public static Long convertYFromString(String y) throws ConversionException, ValidationException {
        long res;
        if (y.isEmpty()) {
            throw new ConversionException("Y cant be null");
        } else {
            try {
                res = Long.parseLong(y);
                if (res >= 117) {
                    throw new ConversionException("Y cant be >=117");
                }
                return res;
            } catch (NumberFormatException e) {
                throw new ConversionException("Couldn't convert y to Long");
            }
        }
    }

    public static double convertXFromString(String x) throws ConversionException, ValidationException {
        try {
            return Double.parseDouble(x);
        } catch (NumberFormatException e) {
            throw new ConversionException("Couldn't convert x to double");
        }
    }

    public static Integer convertOscarsCountFromString(String oc) throws ValidationException, ConversionException {
        Integer res;
        if (oc.isEmpty()) {
            return null;
        } else {
            try {
                res = Integer.parseInt(oc);
                isAboveZero(res);
                return res;
            } catch (NumberFormatException e) {
                throw new ConversionException("Couldn't convert oscarsCount to Integer");
            } catch (ValidationException e) {
                throw new ConversionException(e.getMessage());
            }
        }
    }

    public static int convertTBOFromString(String tbo) throws ValidationException, ConversionException {
        int res;
        if (tbo.isEmpty()) {
            throw new ConversionException("TotalBoxOffice cant be null");
        } else {
            try {
                res = Integer.parseInt(tbo);
                isAboveZero(res);
                return res;
            } catch (NumberFormatException e) {
                throw new ConversionException("Couldn't convert TotalBoxOffice to int");
            } catch (ValidationException e) {
                throw new ConversionException(e.getMessage());
            }
        }
    }

    public static float convertWeightFromString(String weight) throws ValidationException, ConversionException {
        float res;
        if (weight.isEmpty()) {
            throw new ConversionException("weight cant be null");
        } else {
            try {
                res = Float.parseFloat(weight);
                isAboveZero(res);
                return res;
            } catch (NumberFormatException e) {
                throw new ConversionException("Couldn't convert weight to float");
            } catch (ValidationException e) {
                throw new ConversionException(e.getMessage());
            }
        }
    }

    public static LocalDateTime convertDateFromString(String date) throws ConversionException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(date, formatter);
            LocalDateTime localDateTime = localDate.atStartOfDay();
            if (date.isEmpty()) {
                throw new ConversionException("Date cant be empty");
            } else {
                return localDateTime;
            }
        } catch (DateTimeException e) {
            throw new ConversionException("Invalid date");
        }
    }

    public static Long convertUBOFromString(String ubo) throws ValidationException, ConversionException {
        Long res;
        if (ubo.isEmpty()) {
            throw new ConversionException("UsaBoxOffice cant be null");
        } else {
            try {
                res = Long.parseLong(ubo);
                isAboveZero(res);
                return res;
            } catch (NumberFormatException e) {
                throw new ConversionException("Couldn't convert UsaBoxOffice to Long");
            } catch (ValidationException e) {
                throw new ConversionException(e.getMessage());
            }
        }
    }

    public static MovieGenre convertGenreFromString(String genre) throws ConversionException {
        if (genre.isEmpty()) {
            return null;
        } else {
            try {
                return MovieGenre.valueOf(genre.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ConversionException("No such genre");
            }
        }
    }

    public static Color convertEyeColorFromString(String genre) throws ConversionException {
        Color res;
        if (genre.isEmpty()) {
            return null;
        } else {
            try {
                res = Color.valueOf(genre.toUpperCase());
                return res;
            } catch (IllegalArgumentException e) {
                throw new ConversionException("No such color");
            }
        }
    }

    public static Country convertCountryFromString(String country) throws ConversionException {
        Country res;
        if (country.isEmpty()) {
            throw new ConversionException("Nationality cant be null");
        } else {
            try {
                res = Country.valueOf(country.toUpperCase());
                return res;
            } catch (IllegalArgumentException e) {
                throw new ConversionException("No such nationality");
            }
        }
    }

    public static <T extends Number & Comparable<T>> void isAboveZero(T x) throws ValidationException {
        if (x.doubleValue() <= 0) {
            throw new ValidationException("This number less than 0");
        }
    }
}
