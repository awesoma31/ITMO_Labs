package attacks.special;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.SpecialMove;
import ru.ifmo.se.pokemon.Type;

public class Blizzard extends SpecialMove {
    public Blizzard() {
        super(Type.ICE, 110, 70);
    }

    //TODO Надо ли переписывать нанесение урона
    protected void applyOppDamage(Pokemon target, int dmg) {
        super.applyOppDamage(target, dmg);

    }

    @Override
    protected void applyOppEffects(Pokemon target) {
        if (0.1 > Math.random()) Effect.freeze(target);
    }

    @Override
    protected String describe() {
        return "использует способность Blizzard";
    }
}
