package pokemons;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Sandshrew extends Pokemon {
    public Sandshrew(String name, int lvl) {
        super(name, lvl);

        setType(Type.GROUND);
        setStats(50, 75, 85, 20, 30, 40);

    }
}
