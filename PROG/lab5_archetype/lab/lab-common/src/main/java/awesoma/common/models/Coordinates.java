package awesoma.common.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates {
    @XmlElementWrapper
    @XmlElement
    private Long x;
    @XmlElement
    private Float y;

    public Long getX() {
        return x;
    }

    public Float getY() {
        return y;
    }
}
