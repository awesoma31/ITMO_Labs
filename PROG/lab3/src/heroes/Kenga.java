package src.heroes;

import src.heroes.abstractClasses.AbstractAnimal;
import src.interfaces.Speak;
import src.interfaces.Stand;

public class Kenga extends AbstractAnimal implements Speak, Stand {
    public Kenga(String name) {
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
        return "Kenga{" +
                "name='" + name + '\'' +
                '}';
    }
}
