package heroes;

import enums.*;
import heroes.abstractClasses.AbstractHero;
import interfaces.AskAble;
import interfaces.FindAble;
import interfaces.SpeakAble;
import interfaces.StickAble;

import java.util.Objects;

public class Tigra extends Tiger implements StickAble, FindAble, AskAble, SpeakAble {
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

    @Override
    public void say(String s, AbstractHero hero, Volume volume) {
        System.out.println(name + " сказад " + s + hero.getName() + volume);
    }

    public void bend(Stuff stuff) {
        System.out.println(name + " наклонился над " + stuff.getName());
    }

    public void stickOut(BodyParts bp) {
        System.out.println(name + " вытащил " + bp.getName());
    }
}
