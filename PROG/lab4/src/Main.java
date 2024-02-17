import enums.*;
import exceptions.checked.PasswdNotFoundException;
import exceptions.unchecked.UnableToContinueStoryException;
import heroes.*;
import heroes.abstractClasses.AbstractHero;
import interfaces.DisappearAble;
import interfaces.UgovarivAble;
import story.Story;

import java.util.Scanner;

public class Main {
    public static void turnedOut() {
        System.out.print(" и оказалось, что ничего этого ");
    }

    public static void stand(Stuff stuff, AbstractHero hero) {
        System.out.print(
                stuff.getName() + " стояли вокруг " + hero.getRoditName()
        );
    }

    public static void main(String[] args) throws PasswdNotFoundException {
        Scanner in = new Scanner(System.in);

        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                System.out.println("prog is dead");
            }
        });

        Story story = new Story();
        Story.Password.check("docs/passwd");

        Tigra tigra = new Tigra("Тигра", "Тигры", Languages.TIGERLANG);
        Tiger tiger = new Tiger("Тигры", "Тигров", Languages.TIGERLANG);
        ChristoferRobin christoferRobin =
                new ChristoferRobin("Кристофер Робин", "Кристофера Робина", Languages.HUMANLANG);
        Kenga kenga = new Kenga("Кенга", "Кенгу", Languages.KANGAROOOLANG);
        KroshkaRu kroshkaRu = new KroshkaRu("Крошка Ру", "Крошки Ру", Languages.KANGAROOOLANG);
        Piglet piglet = new Piglet("Пятачок", "Пятачка", Languages.PIGLETLANG);
        Puh puh = new Puh("Пух", "Пуха", Languages.HUMANLANG);

        kroshkaRu.say("Здравствуй, " + puh.getName());
        kroshkaRu.say("Здравствуй, " + piglet.getName());
        kroshkaRu.tell(kenga, " зачем  они пришли ");

        System.out.println("Введите `1`, чтобы продолжить историю или `0`, чтобы остановить");
        Story.Valve valve = story.new Valve();
        story.continueStory(in.nextInt(), valve);

        kenga.say(tigra, "Ну что ж, милый Тигра, загляни в мой буфет и посмотри — что тебе там понравится");
        kenga.understand("хотя с виду Тигра очень большой, он так же нуждается в ласке, как и Крошка Ру");

        puh.say("А можно мне тоже поглядеть?");
        puh.setMood(Moods.ELEV_OCLOCK);
        puh.find(Stuff.POT);
        puh.think("Тигры не любят сгущенного молока");
        puh.eat(Stuff.POT);

        /////////////
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
        ////////////////

        tigra.say("Что это там такое?", piglet, Volume.QUIETLY);
        piglet.say(tigra, "Это ему лекарство дают. — Витамины! Он их ненавидит!");

        tigra.bend(Stuff.CHAIR_BACK);
        tigra.stickOut(BodyParts.TOUNGUE);

        System.out.println("послышался буль-буль");

        kenga.jump(" от удивления");
        kenga.say("Ох!", kenga, Volume.LOUD);
        // local
        kenga.catchSpoon(true);


        // anonymous
        new DisappearAble() {
            @Override
            public void disappear() {
                String name = "рыбий жир";
                System.out.println("но " + name + " исчез");
            }
        }.disappear();
    }
}
