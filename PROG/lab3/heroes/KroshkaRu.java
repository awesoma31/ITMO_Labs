package heroes;

import heroes.abstractClasses.AbstractAnimal;
import interfaces.Speak;
import interfaces.Stand;

public class KroshkaRu extends AbstractAnimal implements Speak, Stand {
    public KroshkaRu(String name) {
        super(name);
    }
}
