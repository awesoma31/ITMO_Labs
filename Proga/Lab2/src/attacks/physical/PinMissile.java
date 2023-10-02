package attacks.physical;

import ru.ifmo.se.pokemon.*;

public class PinMissile extends PhysicalMove {
    public PinMissile() {
        double chance = Math.random();
        if (chance < ((double) 3 / 8)) {
            super.hits = 2;
            super.power = 50;
        } else if (chance > ((double) 3 / 8) & chance < ((double) 6 / 8)) {
            super.hits = 3;
            super.power = 75;
        } else if (chance > ((double) 6 / 8) & chance < ((double) 7 / 8)) {
            super.hits = 4;
            super.power = 100;
        } else if (chance > ((double) 7 / 8)) {
            super.hits = 5;
            super.power = 125;
        }
    }

    @Override
    protected String describe() {
        return "использует способность Pin Missile";
    }
}
