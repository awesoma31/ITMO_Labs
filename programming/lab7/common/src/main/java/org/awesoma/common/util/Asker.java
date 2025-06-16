package org.awesoma.common.util;


import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.ConversionException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Asker {
    BufferedReader reader;

    public Asker(BufferedReader reader) {
        this.reader = reader;
    }

    public static Movie askMovie(Asker asker) {
        try {
            return new Movie(
                    asker.askName(),
                    asker.askOscarsCount(),
                    asker.askTotalBoxOffice(),
                    asker.askUsaBoxOffice(),
                    new Coordinates(asker.askX(), asker.askY()),
                    asker.askGenre(),
                    new Person(
                            asker.askOperatorName(),
                            asker.askBirthdate(),
                            asker.askWeight(),
                            asker.askEyeColor(),
                            asker.askNationality()
                    )
            );
        } catch (ValidationException e) {
            throw new CommandExecutingException(e.getMessage());
        }
    }

    public String askName() {
        String bfr;

        while (true) {
            System.out.println("input (String, notNull) movie name: ");
            try {
                bfr = reader.readLine();
                org.awesoma.common.util.Validator.validateName(bfr);
                return bfr;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ValidationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public double askX() {
        String bfr;
        double x = 0;

        while (true) {
            System.out.println("input (double) x: ");
            try {
                bfr = reader.readLine();

                if (!bfr.isEmpty()) {
                    x = org.awesoma.common.util.Validator.convertXFromString(bfr);
                    break;
                }
                break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ValidationException | ConversionException e) {
                System.err.println(e.getMessage());
            }
        }
        return x;
    }

    public Long askY() {
        String bfr;
        while (true) {
            System.out.println("input (Long, notNull, <117) y: ");
            try {
                bfr = reader.readLine();
                return org.awesoma.common.util.Validator.convertYFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ValidationException | ConversionException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public Integer askOscarsCount() {
        String bfr;

        while (true) {
            System.out.println("input (Integer, >0) oscarsCount: ");
            try {
                bfr = reader.readLine();
                return org.awesoma.common.util.Validator.convertOscarsCountFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConversionException | ValidationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public int askTotalBoxOffice() {
        String bfr;

        while (true) {
            System.out.println("input (int, >0) totalBoxOffice: ");
            try {
                bfr = reader.readLine();
                return org.awesoma.common.util.Validator.convertTBOFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConversionException | ValidationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public Long askUsaBoxOffice() {
        String bfr;

        while (true) {
            System.out.println("input (Long, >0) usaBoxOffice: ");
            try {
                bfr = reader.readLine();
                return org.awesoma.common.util.Validator.convertUBOFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConversionException | ValidationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public MovieGenre askGenre() {
        String bfr;

        System.out.println("Available genres: " + Arrays.toString(MovieGenre.values()));
        while (true) {
            System.out.println("input (MovieGenre) genre: ");
            try {
                bfr = reader.readLine();
                return org.awesoma.common.util.Validator.convertGenreFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConversionException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public String askOperatorName() {
        String bfr;

        while (true) {
            System.out.println("input (String, notNull) operator name: ");
            try {
                bfr = reader.readLine();
                org.awesoma.common.util.Validator.validateName(bfr);
                return bfr;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ValidationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public Country askNationality() {
        String bfr;

        System.out.println("Available nationalities: " + Arrays.toString(Country.values()));
        while (true) {
            System.out.println("input (Country, notNull) nationality: ");
            try {
                bfr = reader.readLine();
                return org.awesoma.common.util.Validator.convertCountryFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConversionException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public Color askEyeColor() {
        String bfr;

        System.out.println("Available eye colors: " + Arrays.toString(Color.values()));
        while (true) {
            System.out.println("input (Color) eye color: ");
            try {
                bfr = reader.readLine();
                return org.awesoma.common.util.Validator.convertEyeColorFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConversionException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public float askWeight() {
        String bfr;

        while (true) {
            System.out.println("input (float, >0) weight: ");
            try {
                bfr = reader.readLine();
                return org.awesoma.common.util.Validator.convertWeightFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConversionException | ValidationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public LocalDateTime askBirthdate() {
        String bfr;

        while (true) {
            System.out.println("input (Date, notNull) birthdate: ");
            try {
                bfr = reader.readLine();
                return Validator.convertDateFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConversionException e) {
                System.err.println(e.getMessage());
            }
        }

    }
}
