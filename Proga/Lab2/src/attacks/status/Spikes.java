package attacks.status;

import ru.ifmo.se.pokemon.StatusMove;
import ru.ifmo.se.pokemon.Type;

public class Spikes extends StatusMove {
    public Spikes() {
        super(Type.GROUND, 0, 0);
    }

    @Override
    protected String describe() {
        return "использует способность Spikes";
    }
}
