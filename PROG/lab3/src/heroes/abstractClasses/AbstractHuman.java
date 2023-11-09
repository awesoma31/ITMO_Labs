package src.heroes.abstractClasses;

public abstract class AbstractHuman {
    protected String name;

    public AbstractHuman(String name) {
        this.name = name;
    }

    protected String getName() {
        return name;
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
        return "AbstractHuman{" +
                "name='" + name + '\'' +
                '}';
    }
}
