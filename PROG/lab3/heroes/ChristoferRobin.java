package heroes;

import heroes.abstractClasses.AbstractHuman;
import interfaces.Speak;
import interfaces.Stand;

public class ChristoferRobin extends AbstractHuman implements Speak, Stand {
    public ChristoferRobin(String name) {
        super(name);
    }
}
