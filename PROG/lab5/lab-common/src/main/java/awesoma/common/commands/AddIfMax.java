package awesoma.common.commands;

import awesoma.common.exceptions.ArgParsingException;
import awesoma.common.exceptions.CommandExecutingException;
import awesoma.common.exceptions.WrongAmountOfArgumentsException;
import awesoma.common.models.*;
import awesoma.common.util.UniqueIdGenerator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Adds an element in the collection if its totalBoxOffice value is maximum in the collection
 */
public class AddIfMax extends Command {
    private final Vector<Movie> collection;
    private final UniqueIdGenerator idGenerator;
    public HashSet<Integer> idList;

    public AddIfMax(UniqueIdGenerator idGenerator, Vector<Movie> collection) {
        super(
                "add_if_max",
                "This command adds an element to the collection if its totalBoxOffice " +
                        "is the biggest in the collection"
        );
//        this.reader = reader;
        this.idList = idGenerator.getIdList();
        this.idGenerator = idGenerator;
        this.collection = collection;
    }

    @Override
    public void execute(ArrayList<String> args) throws CommandExecutingException {
        if (args.size() != argAmount) {
            throw new WrongAmountOfArgumentsException();
        } else {
            Integer maxTotalBoxOffice = 0;
            for (Movie m : collection) {
                if (m.getTotalBoxOffice() > maxTotalBoxOffice) {
                    maxTotalBoxOffice = m.getTotalBoxOffice();
                }
            }

            // totalBoxOffice >0
            int totalBoxOffice = 0;
            String bfr = "";
            boolean f = true;
            while (f | totalBoxOffice <= 0) {
                System.out.print("input (int, >0) totalBoxOffice: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        totalBoxOffice = Integer.parseInt(bfr);
                        if (totalBoxOffice <= 0) {
                            System.out.println("<totalBoxOffice> must be above 0");
                        } else {
                            f = false;
                        }
                    } else {
                        f = true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    throw new ArgParsingException("error parsing totalBoxOffice");
                }
            }

            if (totalBoxOffice < maxTotalBoxOffice) {
                System.out.println(
                        "[ABORTION]: inputed totalBoxOffice is not the bigggest in the collection, " +
                                "element won't be added anyway"
                );
                return;
            }

            // id
            int id = idGenerator.generateUniqueId();


            //name
            String name = null;
            bfr = "";
            while (bfr.isEmpty()) {
                System.out.print("input (String, notNull) Movie.name: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        name = bfr;
                    } else {
                        System.out.println("<name> can't be null, try again");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // coords
            //x
            f = true;
            double x = 0;
            while (f | x == 0) {
                System.out.print("input (double) x: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        x = Double.parseDouble(bfr);
                        f = false;
                    } else {
                        f = true;
                        continue;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    throw new ArgParsingException("error parsing x");
                }
            }


            //y notNull <117
            Long y = (long) 120F;
            bfr = "";
            while (bfr.isEmpty() | y >= 117) {
                System.out.print("input (Long, notNull, <117) y: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        y = Long.parseLong(bfr);
                        if (y >= 117) {
                            System.out.println("<y> must be less than 117");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    throw new ArgParsingException("error parsing y");
                }
            }

            // creationDate
            LocalDateTime creationDate = LocalDateTime.now();

            // oscarsCount mbNull >0
            Integer oscarsCount = null;
            f = true;
            while (f) {
                System.out.print("input (Integer, >0) oscarsCount: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        oscarsCount = Integer.parseInt(bfr);
                        if (oscarsCount <= 0) {
                            System.out.println("<oscarsCount> must be above 0 or null");
                        } else {
                            f = false;
                        }
                    } else {
                        oscarsCount = null;
                        f = false;
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    throw new ArgParsingException("error parsing oscarsCount");
                }
            }


            // usaBoxOffice notNull >0
            Long usaBoxOffice = 0L;
            f = true;
            while (f | usaBoxOffice <= 0) {
                System.out.print("input (Long, >0) usaBoxOffice: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        usaBoxOffice = Long.parseLong(bfr);
                        if (usaBoxOffice <= 0) {
                            System.out.println("<usaBoxOffice> must be above 0");
                        } else {
                            f = false;
                        }
                    } else {
                        f = true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    throw new ArgParsingException("error parsing usaBoxOffice");
                }
            }


            // genre mbNull
            MovieGenre genre = null;
            f = true;
            while (f) {
                System.out.println("Available genres: " + Arrays.toString(MovieGenre.values()));
                System.out.print("input (MovieGenre) genre: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        try {
                            genre = MovieGenre.valueOf(bfr.toUpperCase());
                            f = false;
                        } catch (IllegalArgumentException e) {
                            System.out.println("[FAIL]: No such genre");
                        }
                    } else {
                        f = false;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    throw new ArgParsingException("error parsing x");
                }
            }

            // operator
            // name
            String operatorName = null;
            bfr = "";
            while (bfr.isEmpty()) {
                System.out.print("input (String, notNull) operator.name: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        operatorName = bfr;
                    } else {
                        System.out.println("<operatorName> can't be null, try again");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // birthday
            Date birthdate = null;
            bfr = "";
            while (bfr.isEmpty() | birthdate == null) {
                System.out.print("input (Date, notNull, format: y-m-d, year from 1900) operator.birthday: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        String[] data = bfr.split("-");
                        if (data.length == 3) {
                            birthdate = new Date(
                                    Integer.parseInt(data[0]),
                                    Integer.parseInt(data[1]),
                                    Integer.parseInt(data[2])
                            );
                        } else {
                            System.out.println("wrong data format");
                            bfr = "";
                        }
                    } else {
                        System.out.println("<birthdate> can't be null, try again");
                    }
                    System.out.println(birthdate);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // weight >0
            float weight = 0;
//            float weight_;
            f = true;
            while (f | weight <= 0) {
                System.out.print("input (float, >0) weight: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        weight = Float.parseFloat(bfr);
                        if (weight <= 0) {
                            System.out.println("<weight> must be above 0");
                        } else {
                            f = false;
                        }
                    } else {
                        f = true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    throw new ArgParsingException("error parsing weight");
                }
            }

            // eyeColor
            Color eyeColor = null;
            f = true;
            while (f) {
                System.out.println("Available colors: " + Arrays.toString(Color.values()));
                System.out.print("input (Color) eyeColor: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        try {
                            eyeColor = Color.valueOf(bfr.toUpperCase());
                            f = false;
                        } catch (IllegalArgumentException e) {
                            System.out.println("[FAIL]: No such eyeColor");
                        }
                    } else {
                        f = false;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    throw new ArgParsingException("error parsing eyeColor");
                }
            }

            // nationality
            Country nationality = null;
            f = true;
            while (f & nationality == null) {
                System.out.println("Available nationalities: " + Arrays.toString(Country.values()));
                System.out.print("input (Country) nationality: ");
                try {
                    bfr = reader.readLine();
                    if (!bfr.isEmpty()) {
                        try {
                            nationality = Country.valueOf(bfr.toUpperCase());
                            f = false;
                        } catch (IllegalArgumentException e) {
                            System.out.println("[FAIL]: No such nation");
                        }
                    } else {
                        System.out.println("Nationality can't be null, try again");
                        f = true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    throw new ArgParsingException("error parsing nation");
                }
            }


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
