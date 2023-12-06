import enums.*;
import heroes.*;

import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;

public class Main {
    static void eat(AbstractHero[] peoples) {
        for (AbstractHero people: peoples)
            System.out.print(people.name + ", ");
        System.out.println("выпили и закусили");
    }

    static void didntNotice(AbstractHero hero, String what) {
        System.out.println(hero.name + " не заметил " + what);
    }

    static void notice(AbstractHero hero, String what) {
        System.out.println(hero.name + " заметил " + what);
    }

    public static void main(String... args) {
        Fille fille = new Fille("Филле");
        Karlson karlson = new Karlson("Карлсон");
        Kiddo kiddo = new Kiddo("Малыш");
        Oscar oscar = new Oscar("Оскар");
        Rulle rulle = new Rulle("Рулле");

        fille.do_(" нечто");

        kiddo.mood = Moods.AMAZED;

        fille.put(BodyParts.ARM, Pockets.OSCAR_POCKET);

        fille.take(Stuff.OSCAR_WALLET, Pockets.OSCAR_POCKET);

        fille.put(Stuff.OSCAR_WALLET, Pockets.FILLE_POCKET);

        //TODO: чтобы в embrace, look передавался выполняющий герой
        rulle.embrace(rulle, oscar);

        if (oscar.isEmbraced) {
            didntNotice(oscar, "ничего");
        }

        rulle.unEmbrace(rulle, oscar);

        rulle.thingInHand = Stuff.OSCAR_CLOCKS;

        rulle.put(Stuff.OSCAR_CLOCKS, Pockets.RULLE_POCKET);

        didntNotice(oscar, "ничего");

        karlson.take(Stuff.OSCAR_WALLET, Pockets.FILLE_POCKET);

        didntNotice(fille, "ничего");

        AbstractHero[] peopleWhoAte = {rulle, fille, oscar};
        eat(peopleWhoAte);

        fille.put(BodyParts.ARM, Pockets.FILLE_POCKET);

        notice(fille, " что часов нет");

        fille.look(fille, Moods.ANGRILY);
        fille.say(fille, "");

        rulle.put(BodyParts.ARM, Pockets.RULLE_POCKET);

        if (rulle.thingInHand == Stuff.OSCAR_CLOCKS) {
            didntNotice(rulle, "отсутствие часов");
        }

        rulle.look(rulle, Moods.ANGRILY);

        rulle.say(rulle, "");

        fille.walk(fille, Places.HALLWAY);
        rulle.walk(rulle, Places.HALLWAY);

        if (fille.position != Places.ROOM & rulle.position != Places.ROOM) {
            oscar.mood = Moods.BORED;
            oscar.walk(oscar, Places.HALLWAY);
        }

        karlson.jump(karlson, Places.WINDOWSILL);

        karlson.put(Stuff.OSCAR_WALLET, Stuff.SOUP_BOWL);

        //wallet dont get wet becouse soup is eaten

        karlson.put(Stuff.OSCAR_CLOCKS, Stuff.LAMP);

        // oscarclocks hang and shake

        rulle.walk(rulle, Places.ROOM);
        fille.walk(rulle, Places.ROOM);
        oscar.walk(rulle, Places.ROOM);

        if (karlson.position == Places.UNDER_TABLE) {
            didntNotice(rulle, " карлсона");
            didntNotice(fille, " карлсона");
            didntNotice(oscar, " карлсона");
        }

        kiddo.position = Places.UNDER_TABLE;
        kiddo.walk(kiddo, Places.NEAR_LAMP);
        kiddo.take(Stuff.OSCAR_CLOCKS, BodyParts.ARM);

        kiddo.put(Stuff.OSCAR_CLOCKS, Pockets.KIDDO_POCKET);

        rulle.look(rulle, Moods.STARING);
        fille.look(fille, Moods.STARING);
    }
}
