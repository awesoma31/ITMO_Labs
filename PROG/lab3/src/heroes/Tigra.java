package src.heroes;

import src.enums.BodyParts;
import src.heroes.abstractClasses.AbstractTiger;
import src.interfaces.Stickable;

public class Tigra extends AbstractTiger implements Stickable {
    public Tigra(String name) {
        super(name);
    }

    // TODO: ask
    public String ask() {
        return "спросил";
    }

    @Override
    public void stick(BodyParts obj) {
        System.out.println("совал свой " + obj.getName());
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
