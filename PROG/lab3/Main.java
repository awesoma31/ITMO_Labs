import src.enums.BodyParts;
import src.enums.FindTime;
import src.enums.Languages;
import src.enums.Stuff;
import src.heroes.*;
import src.heroes.abstractClasses.AbstractHero;
import src.interfaces.UgovarivAble;

public class Main {
    public static void turnedOut() {
        System.out.print(" и оказалось, что ничего этого ");
    }

    public static void stand(Stuff stuff, AbstractHero hero) {
        System.out.print(
                stuff.getName() + " стояли вокруг " + hero.getRoditName()
        );
    }

    public static void main(String[] args) {
        Tigra tigra = new Tigra("Тигра", "Тигры", Languages.TIGERLANG);
        Tiger tiger = new Tiger("Тигры", "Тигров", Languages.TIGERLANG);
        ChristoferRobin christoferRobin = new ChristoferRobin(
                "Кристофер Робин", "Кристофера Робина", Languages.HUMANLANG
        );
        Kenga kenga = new Kenga("Кенга", "Кенгу", Languages.KANGAROOOLANG);
        KroshkaRu kroshkaRu = new KroshkaRu("Крошка Ру", "Крошки Ру", Languages.KANGAROOOLANG);
        Piglet piglet = new Piglet("Пятачок", "Пятачка", Languages.PIGLETLANG);

        UgovarivAble[] whoPersuaded = {kenga, christoferRobin, piglet};

        tigra.stick(BodyParts.NOSE);

        tigra.find(FindTime.PAST, Stuff.THINGS);
        tiger.hate();
        tigra.dig(Stuff.ALL);
        System.out.println();
        tiger.find(FindTime.PRESENT, Stuff.ALL);

        turnedOut();

        tigra.cantEat();
        tigra.ask(kenga);

        System.out.println();
        stand(Stuff.ALL, kroshkaRu);
        System.out.println();

        for (UgovarivAble persuadingHero : whoPersuaded) {
            System.out.println(persuadingHero.persuade(kroshkaRu));
        }

        kroshkaRu.speak("Может, не надо?");
        System.out.println();
        kenga.speak("Ну-ну, милый Ру, вспомни, что ты мне обещал");
    }
}
