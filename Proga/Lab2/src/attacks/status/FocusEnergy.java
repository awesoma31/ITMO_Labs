package attacks.status;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;

import java.util.prefs.AbstractPreferences;

public class FocusEnergy extends StatusMove {
    public FocusEnergy() {
        super(Type.NORMAL, 0, 0);
    }
    @Override
    protected void applySelfEffects(Pokemon p) {
        //TODO Увеличить в ероятность крит удара
        super.applySelfEffects(p);


    }

    @Override
    protected String describe() {
        return "использует способность Focus Energy";
    }
}
