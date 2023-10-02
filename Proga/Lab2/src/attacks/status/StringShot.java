package attacks.status;

import ru.ifmo.se.pokemon.*;

public class StringShot extends StatusMove {
    public StringShot() {
        super(Type.BUG, 0, 95);
    }

    @Override
    protected void applyOppEffects(Pokemon target) {
        target.setMod(Stat.SPEED, -2);
    }

    @Override
    protected String describe() {
        return "использует способность String Shot";
    }
}