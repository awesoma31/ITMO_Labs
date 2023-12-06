package heroes;

public class Rulle extends AbstractHero{
    public Rulle(String name) {
        super(name);
    }

    public void embrace(AbstractHero hero) {
        System.out.println();
    }

    public void unEmbrace() {
        System.out.println(name + " разжал объятия");
    }
}
