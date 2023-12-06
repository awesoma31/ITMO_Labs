package heroes;

import enums.*;
import interfaces.*;

public abstract class AbstractHero implements PutAble, JumpAble, WalkAble, TakeAble, EmbraceAble, LookAble, SayAble {
    public String name;
    public Places position;
    public Moods mood;
    public Boolean isEmbraced;
    public Stuff thingInHand;

    public AbstractHero(String name) {
        this.name = name;
    }
}
