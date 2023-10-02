package attacks.status;

import ru.ifmo.se.pokemon.*;

public class Agility extends StatusMove {

    public Agility() {
        super(Type.PSYCHIC, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon p) {
        super.applySelfEffects(p);

        Effect e = new Effect().stat(Stat.SPEED, 2);
        p.addEffect(e);
    }

    @Override
    protected String describe() {
        return "использует способность Agility";
    }
}
