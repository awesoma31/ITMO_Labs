package heroes;

import heroes.abstractClasses.AbstractTiger;
import interfaces.Speak;
import interfaces.Stand;

public class Tigra extends AbstractTiger implements Speak, Stand {
    public Tigra(String name) {
        super(name);
    }
}
