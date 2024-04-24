package org.awesoma.common.util;

import org.awesoma.common.exceptions.CommandExecutingException;
import org.awesoma.common.exceptions.ValidationException;
import org.awesoma.common.models.Coordinates;
import org.awesoma.common.models.Movie;
import org.awesoma.common.models.Person;

public interface Ask {
    default Movie askMovie(Asker asker) {
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
}
