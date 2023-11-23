package src.heroes;

import src.heroes.abstractClasses.AbstractHero;

import java.util.Objects;

public class Piglet extends AbstractHero {
    public Piglet(String name) {
        super(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piglet that = (Piglet) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public String toString() {
        return "Piglet{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public String persuade(AbstractHero obj) {
        return this.name + " уговаривал " + obj.getRoditName();
    }
}
