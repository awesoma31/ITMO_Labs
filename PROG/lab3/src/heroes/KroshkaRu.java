package src.heroes;

import src.heroes.abstractClasses.AbstractAnimal;
import src.interfaces.Speak;
import src.interfaces.Stand;

public class KroshkaRu extends AbstractAnimal implements Speak, Stand {
    public KroshkaRu(String name) {
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
        return "KroshkaRu{" +
                "name='" + name + '\'' +
                '}';
    }
}
