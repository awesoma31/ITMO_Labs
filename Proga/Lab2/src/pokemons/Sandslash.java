package pokemons;

import attacks.status.*;
import ru.ifmo.se.pokemon.*;

public class Sandslash extends Sandshrew {

    public Sandslash(String name, int lvl) {
        super(name, lvl);

        setType(Type.GROUND);
        setStats(75, 100, 110, 45, 55, 65);
        setMove(
                new LightScreen(),
                new ThunderWave(),
                new StringShot()
        );
    }
}
