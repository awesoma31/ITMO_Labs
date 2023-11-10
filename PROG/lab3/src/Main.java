package src;

import src.enums.BodyParts;
import src.heroes.*;

public class Main {
    public static void main(String[] args) {
        Tigra tigra = new Tigra("Тигра");
        ChristoferRobin christoferRobin = new ChristoferRobin("Кристофер Робин");
        Kenga kenga = new Kenga("Кенга");
        KroshkaRu kroshkaRu = new KroshkaRu("Крошка Ру");
        Piglet piglet = new Piglet("Пятачок");

        BodyParts bodyParts;

        tigra.stick(BodyParts.NOSE);
        tigra.stick(BodyParts.PAW);

        /*
        tigra.stick(NOSE, ONE);
        tigra.stick(PAW, ANOTHER);
        tigra.find(stuff);
        которые тигры не любят
        tiger.isLike();

        tigra.dig();
        tigra.find();
        оказалось ято ничего из этого он есть не может
        tigra.ask(kenga)
        kroshkaRu.standsAround(kenga, chris, piglet)
        kroshkaRu.beingAsked(by who, to eat)

        kroshkaRu.say("Может не надо")
        kenga.speak("")
         */
    }
}
