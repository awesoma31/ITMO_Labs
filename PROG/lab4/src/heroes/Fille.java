package heroes;

public class Fille extends AbstractHero{
    public Fille(String name) {
        super(name);
    }

    public void do_(String what) {
        System.out.println(name + " сделал " + what);
    }
}
