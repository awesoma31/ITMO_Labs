package awesoma.common.util;


import awesoma.common.exceptions.ConversionException;
import awesoma.common.exceptions.ValidationException;
import awesoma.common.models.Color;
import awesoma.common.models.Country;
import awesoma.common.models.MovieGenre;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class Asker {
    BufferedReader reader;

    public Asker(BufferedReader reader) {
        this.reader = reader;
    }

    public String askName() {
        String bfr;

        while (true) {
            System.out.println("input (String, notNull) movie name: ");
            try {
                bfr = reader.readLine();
                Validator.validateName(bfr);
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
                    x = Validator.convertXFromString(bfr);
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
                return Validator.convertYFromString(bfr);
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
                return Validator.convertOscarsCountFromString(bfr);
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
                return Validator.convertTBOFromString(bfr);
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
                return Validator.convertUBOFromString(bfr);
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
                return Validator.convertGenreFromString(bfr);
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
                Validator.validateName(bfr);
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
                return Validator.convertCountryFromString(bfr);
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
                return Validator.convertEyeColorFromString(bfr);
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
                return Validator.convertWeightFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConversionException | ValidationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public Date askBirthdate() {
        String bfr;

        while (true) {
            System.out.println("input (Date, notNull) birthdate: ");
            try {
                bfr = reader.readLine();
                System.out.println(Validator.convertDateFromString(bfr));
                return Validator.convertDateFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConversionException e) {
                System.err.println(e.getMessage());
            }
        }

    }
}
