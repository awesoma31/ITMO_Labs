package awesoma.common.commands;

import awesoma.common.exceptions.*;
import awesoma.common.models.*;
import awesoma.common.util.UniqueIdGenerator;
import awesoma.common.util.Validator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This command adds an element with given fields to the collection
 */
public class Add extends Command {
    private final Vector<Movie> collection;
    private final UniqueIdGenerator idGenerator;
    public HashSet<Integer> idList;


    public Add(UniqueIdGenerator idGenerator, Vector<Movie> collection) {
        super("add", "this command adds an element to the collection");
        this.idList = idGenerator.getIdList();
        this.idGenerator = idGenerator;
        this.collection = collection;
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
            } catch (ValidationException | ConvertationException e) {
                System.err.println(e.getMessage());
            }
        }
        return x;
    }

    public Long askY() {
        Long y;
        String bfr;
        while (true) {
            System.out.println("input (Long, notNull, <117) y: ");
            try {
                bfr = reader.readLine();
                y = Validator.convertYFromString(bfr);
                return y;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ValidationException | ConvertationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public Integer askOscarsCount() {
        Integer oscarsCount = null;
        String bfr;

        while (true) {
            System.out.println("input (Integer, >0) oscarsCount: ");
            try {
                bfr = reader.readLine();
                oscarsCount = Validator.convertOscarsCountFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConvertationException e) {
                System.err.println(e.getMessage());
                continue;
            } catch (ValidationException e) {
                System.err.println(e.getMessage());
            }
            return oscarsCount;
        }
    }

    public int askTotalBoxOffice() {
        int totalBoxOffice;
        String bfr;

        while (true) {
            System.out.println("input (int, >0) totalBoxOffice: ");
            try {
                bfr = reader.readLine();
                totalBoxOffice = Validator.convertTBOFromString(bfr);
                return totalBoxOffice;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConvertationException | ValidationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public Long askUsaBoxOffice() {
        Long usaBoxOffice;
        String bfr;

        while (true) {
            System.out.println("input (Long, >0) usaBoxOffice: ");
            try {
                bfr = reader.readLine();
                usaBoxOffice = Validator.convertUBOFromString(bfr);
                return usaBoxOffice;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConvertationException | ValidationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public MovieGenre askGenre() {
        String bfr;

        System.out.println("Available genres: " + Arrays.toString(MovieGenre.values()));
        while (true) {
            System.out.print("input (MovieGenre) genre: ");
            try {
                bfr = reader.readLine();
                return Validator.convertGenreFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConvertationException e) {
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

    private Country askNationality() {
        String bfr;

        System.out.println("Available nationalities: " + Arrays.toString(Country.values()));
        while (true) {
            System.out.println("input (Country, notNull) nationality: ");
            try {
                bfr = reader.readLine();
                return Validator.convertCountryFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConvertationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private Color askEyeColor() {
        String bfr;

        System.out.println("Available eye colors: " + Arrays.toString(MovieGenre.values()));
        while (true) {
            System.out.print("input (Color) eye color: ");
            try {
                bfr = reader.readLine();
                return Validator.convertEyeColorFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConvertationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private float askWeight() {
        String bfr;

        while (true) {
            System.out.println("input (float, >0) weight: ");
            try {
                bfr = reader.readLine();
                return Validator.convertWeightFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConvertationException | ValidationException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private Date askBirthdate() {
        String bfr;

        while (true) {
            System.out.println("input (Date, notNull) birthdate: ");
            try {
                bfr = reader.readLine();
                System.out.println(Validator.convertDateFromString(bfr));
                return Validator.convertDateFromString(bfr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ConvertationException e) {
                System.err.println(e.getMessage());
            }
        }

    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() != argAmount & this.reader != null) {
            throw new WrongAmountOfArgumentsException();
        } else {
            // id
            int id = idGenerator.generateUniqueId();
            String name = askName();
            double x = askX();
            Long y = askY();
            LocalDateTime creationDate = LocalDateTime.now();
            Integer oscarsCount = askOscarsCount();
            int totalBoxOffice = askTotalBoxOffice();
            Long usaBoxOffice = askUsaBoxOffice();
            MovieGenre genre = askGenre();
            String operatorName = askOperatorName();
            Date birthdate = askBirthdate();
            float weight = askWeight();
            Color eyeColor = askEyeColor();
            Country nationality = askNationality();

            Person operator = new Person(operatorName, birthdate, weight, eyeColor, nationality);
            Coordinates coordinates = new Coordinates(x, y);
            Movie movie = new Movie(
                    id, name, oscarsCount, totalBoxOffice,
                    usaBoxOffice, coordinates, creationDate,
                    genre, operator
            );

            collection.add(movie);
        }
    }
}
