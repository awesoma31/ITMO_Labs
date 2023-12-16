package heroes;

import enums.FindTime;
import enums.Languages;
import enums.Stuff;
import heroes.abstractClasses.AbstractHero;
import interfaces.FindAble;

import java.util.Objects;

public class Tiger extends AbstractHero implements FindAble {
    public Tiger(String name, String roditName, Languages lang) {
        super(name, roditName, lang);
    }

    @Override
    public String getName() {
        return name;
    }

    public void hate() {
        System.out.print(" которые " + this.name + " не любят.");
    }

    @Override
    public void find(FindTime time, Stuff stuff) {
        System.out.print(time.getName() + " " + stuff.getName() + " что там было");
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tiger that = (Tiger) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public String persuade(AbstractHero who) {
        return this.name + " уговаривал " + who.getRoditName() + " на " + language.getTitle() + " языке";
    }

    @Override
    public String toString() {
        return "Tiger{" +
                "name='" + name + '\'' +
                ", roditName='" + roditName + '\'' +
                '}';
    }
}
