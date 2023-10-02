package attacks.status;

import ru.ifmo.se.pokemon.*;

public class ThunderWave extends StatusMove {
    public ThunderWave() {
        super(Type.ELECTRIC, 0, 90);
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        //TODO 25% шанс что парализованный покемон не атакует
        Effect.paralyze(p);
        p.setMod(Stat.SPEED, -2);
    }

    protected String describe() {
        return "использует способность Thunder Wave";
    }
}
