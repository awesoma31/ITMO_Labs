package pokemons;

import attacks.status.*;
import ru.ifmo.se.pokemon.Type;

public class Kabutops extends Kabuto {
    //TODO Spikes
    public Kabutops(String name, int lvl) {
        super(name, lvl);

        setType(Type.ROCK, Type.WATER);
        setStats(60, 115, 105, 65, 70, 80);
        setMove(
                new LightScreen(),
                new ThunderWave(),
                new StringShot(),
                new Spikes()
        );
    }
}
