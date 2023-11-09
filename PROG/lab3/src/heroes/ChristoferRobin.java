package src.heroes;

import src.heroes.abstractClasses.AbstractHuman;
import src.interfaces.Speak;
import src.interfaces.Stand;

public class ChristoferRobin extends AbstractHuman implements Speak, Stand {
    public ChristoferRobin(String name) {
        super(name);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        return "ChristoferRobin{" +
                "name='" + name + '\'' +
                '}';
    }
}
