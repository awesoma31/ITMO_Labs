package pokemons;

import attacks.status.*;
import ru.ifmo.se.pokemon.*;
//import ru.ifmo.se.pokemon.Type;

public class Kecleon extends Pokemon {
    //TODO Доделать Swallow
    public Kecleon(String name, int lvl) {
        super(name, lvl);

        setType(Type.NORMAL);
        setStats(60, 90, 70, 60, 120, 40);
        setMove(
                new Agility(),
                new FocusEnergy(),
                new LightScreen(),
                new ThunderWave(),
                new Swallow()
        );
    }


}
