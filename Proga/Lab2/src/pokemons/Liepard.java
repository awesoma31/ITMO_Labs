package pokemons;

import attacks.physical.*;
import ru.ifmo.se.pokemon.*;
//import ru.ifmo.se.pokemon.Type;

public class Liepard extends Pokemon {
    /*
    Outrage
    Shadow Punch
    Pin Missile
     */


    public Liepard(String name, int lvl) {
        super(name, lvl);

        super.setType(Type.DARK);
        super.setStats(64, 88, 50, 88, 50, 106);
        super.setMove(
                new ShadowPunch(),
                new PinMissile(),
                new Outrage()
        );

//        ShadowPunch shadowPunch = new ShadowPunch();

    }

}
