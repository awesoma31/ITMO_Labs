import pokemons.*;
import ru.ifmo.se.pokemon.Battle;

public class Main {
    public static void main(String... args) {
        //TODO Spikes
        //TODO Outrage
        Battle b = new Battle();

        Kecleon kecleon = new Kecleon("Kecleon", 1);
        Liepard liepard = new Liepard("Liepard", 1);
        Kabuto kabuto = new Kabuto("Kabuto", 1);
        Pansage pansage = new Pansage("Pansage", 1);
        Sandslash sandslash = new Sandslash("Sandslash", 1);
        Kabutops kabutops = new Kabutops("Kabutops", 1);

        b.addAlly(kabutops);
        b.addAlly(kecleon);
        b.addAlly(pansage);

        b.addFoe(liepard);
        b.addFoe(sandslash);
        b.addFoe(kabuto);

        b.go();
    }
}
