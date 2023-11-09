package heroes;

import heroes.abstractClasses.AbstractAnimal;
import interfaces.Speak;
import interfaces.Stand;

public class Kenga extends AbstractAnimal implements Speak, Stand {
    public Kenga(String name) {
        super(name);
    }
}
