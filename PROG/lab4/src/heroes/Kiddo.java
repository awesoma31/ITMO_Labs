package heroes;

public class Kiddo extends AbstractHero{
    public Kiddo(String name) {
        super(name);
    }

    public void amazedBy(String o){
        System.out.println(o + " изумило " + name);
    }
}
