package src.heroes;

import src.heroes.abstractClasses.AbstractTiger;
import src.interfaces.Speak;
import src.interfaces.Stand;

public class Tigra extends AbstractTiger implements Speak, Stand {
    public Tigra(String name) {
        super(name);
    }
    // TODO: ask
    public String ask() {
        return "спросил";
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
        return "Tigra{" + name + "}";
    }
}
