package attacks.status;

import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;


public class Swallow extends StatusMove {
    public Swallow() {
        super(Type.NORMAL, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon pokemon) {
        // т.к. у покемона нет способности Stockpile, эта способность ничего делать не будет

    }

    @Override
    protected String describe() {
        return "использует способность Swallow";
    }
}
