package heroes;

import enums.FindTime;
import enums.Languages;
import enums.Moods;
import enums.Stuff;
import heroes.abstractClasses.AbstractHero;
import interfaces.FindAble;
import interfaces.SpeakAble;
import interfaces.ThinkAble;

public class Puh extends AbstractHero implements SpeakAble, FindAble, ThinkAble {
    protected Moods mood;

    public void setMood(Moods mood) {
        this.mood = mood;
    }

    public Puh (String name, String rn, Languages languages) {
        super(name, rn, languages);
    }

    @Override
    public String persuade(AbstractHero who) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void speak(String whatToSay) {
        System.out.println(name + " сказал " + whatToSay);
    }

    @Override
    public void say(AbstractHero hero, String str) {
        SpeakAble.super.say(hero, str);
    }

    @Override
    public void find(Stuff stuff) {
        System.out.println(name + " нашел " + stuff.getName());
    }

    @Override
    public void think(String s) {
        System.out.println(name + " думает: " + s);
    }

    public void eat(Stuff stuff) {
        System.out.println(name + " ест " + stuff.getName());
    }
}
