package pokemons;

import attacks.status.LightScreen;
import attacks.status.ThunderWave;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public class Kabuto extends Pokemon {
    public Kabuto(String name, int lvl) {
        super(name, lvl);
        setType(Type.ROCK, Type.WATER);
        setStats(30, 80, 90, 55, 45, 55);
        setMove(new LightScreen(), new ThunderWave());
    }
}
