package src.heroes;

import src.enums.*;
import src.heroes.abstractClasses.AbstractHero;
import src.interfaces.AskAble;
import src.interfaces.FindAble;
import src.interfaces.StickAble;

import java.util.Objects;

public class Tigra extends Tiger implements StickAble, FindAble, AskAble {
    public Tigra(String name, String roditName, Languages lang) {
        super(name, roditName, lang);
    }

    @Override
    public void find(FindTime time, Stuff stuff) {
        System.out.print(" тем больше " + this.name + " " + time.getName() + " " + stuff.getName());
    }

    @Override
    public void stick(BodyParts bp) {
        System.out.print("Но чем больше " + this.name + " совал свой ");
        for (BodyParts v : bp.getVals()) {
            System.out.print(v.getName() + " ");
        }

        for (Pots pt : Pots.getVals()) {
            System.out.print(" то в ");
            System.out.print(pt.getName());
        }
        System.out.print(" банку");
    }

    public void cantEat() {
        System.out.print("есть не может, ");
    }

    @Override
    public void ask(AbstractHero obj) {
        System.out.print(" он спросил " + obj.getRoditName() + ": ");
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tigra that = (Tigra) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public String toString() {
        return "Tigra{" + name + "}";
    }

    public void dig(Stuff stuff) {
        System.out.print(" И когда он перерыл " + stuff.getName() + " весь буфет,");
    }
}
