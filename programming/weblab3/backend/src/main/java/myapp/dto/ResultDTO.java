package myapp.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultDTO")
public class ResultDTO {

    public Long id;
    public double x;
    public double y;
    public double r;
    public boolean hit;

    public ResultDTO() {}

    public ResultDTO(Long id, double x, double y, double r, boolean hit) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
    }
}
