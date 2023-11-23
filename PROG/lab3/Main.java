import src.enums.*;
import src.heroes.*;
import src.heroes.abstractClasses.*;

public class Main {
    public static void turnedOut() {
        System.out.print(" и оказалось, что ничего этого ");
    }

    public static void stand(Stuff stuff, AbstractHero hero) {
        System.out.print(
                stuff.getName() + " стояли вокруг " + hero.getRoditName() +
                        ", уговаривая его принять рыбий жир"
        );
    }

    public static void persuade() {
        System.out.print(", уговаривая его принять рыбий жир.");
    }

    public static void main(String[] args) {
        Tigra tigra = new Tigra("Тигра");
        Tiger tiger = new Tiger("Тигры");
        ChristoferRobin christoferRobin = new ChristoferRobin("Кристофер Робин");
        Kenga kenga = new Kenga("Кенга", "Кенгу");
        KroshkaRu kroshkaRu = new KroshkaRu("Крошка Ру", "Крошки Ру");
        Piglet piglet = new Piglet("Пятачок");

        tigra.stick(BodyParts.NOSE);

        tigra.find(FindTime.PAST, Stuff.THINGS);
        tiger.hate();
        tigra.dig(Stuff.ALL);
        System.out.println();
        tiger.find(FindTime.PRESENT, Stuff.ALL);

        turnedOut();

        tigra.dontLike();
        tigra.ask(kenga);

        System.out.print(
                "Но " + kenga.getName() + ", и " +
                        christoferRobin.getName() + ", и " +
                        piglet.getName() + " - "
        );
        System.out.println();
        stand(Stuff.ALL, kroshkaRu);
        persuade();

        kroshkaRu.speak("Может, не надо?");
        System.out.println();
        kenga.speak("Ну-ну, милый Ру, вспомни, что ты мне обещал");

        /*
        Но чем больше Тигра совал свой нос лапу  то в одну то в другую банку тем больше Тигра находил вещей которые Тигры не любят.
        И когда он перерыл все весь буфет, нашел все что там было и оказалось, что ничего этого есть не может, он спросил Кенгу:
        Но Кенга, и Кристофер Робин, и Пятачок - все стояли вокруг Крошки Ру, уговаривая его принять рыбий жир, уговаривая его принять
        рыбий жир. И Крошка Ру говорит: "Может, не надо?" - а Кенга говорит: "Ну-ну, милый Ру, вспомни, что ты мне обещал".

         */
    }


}
