package pokemons;

import attacks.physical.*;
import attacks.special.*;
import ru.ifmo.se.pokemon.*;

public class Pansage extends Pokemon {

    //TODO Outrage, Pin Missile

    public Pansage(String name, int lvl) {
        super(name, lvl);

        super.setType(Type.GRASS);
        super.setStats(50, 53, 48, 53, 48, 64);
        super.setMove(
                new Outrage(),
                new ShadowPunch(),
                new PinMissile(),
                new Blizzard()
        );
    }

}
