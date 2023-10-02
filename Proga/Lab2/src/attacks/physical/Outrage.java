package attacks.physical;

import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Type;

public class Outrage extends PhysicalMove {
    public Outrage() {
        super(Type.DRAGON, 120, 100);
    }

    @Override
    protected String describe() {
        return "использует способность Outrage";
    }
}
