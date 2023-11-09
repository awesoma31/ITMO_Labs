package heroes;

import heroes.abstractClasses.AbstractAnimal;
import interfaces.Speak;
import interfaces.Stand;

public class Piglet extends AbstractAnimal implements Speak, Stand {
    public Piglet(String name) {
        super(name);
    }
}
