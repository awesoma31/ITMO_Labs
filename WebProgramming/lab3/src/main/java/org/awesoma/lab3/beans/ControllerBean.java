package org.awesoma.lab3.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awesoma.lab3.services.db.DatabaseService;
import org.awesoma.lab3.model.Point;
import org.awesoma.lab3.services.AreaCheckService;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author awesoma
 */
@Named
@SessionScoped
public class ControllerBean implements Serializable {
    private static final Logger logger = LogManager.getLogger(ControllerBean.class);

    @Inject
    private DatabaseService dbService;
    @Inject
    private AreaCheckService areaCheckService;

    private Double x = 0d;
    private Double y = 0d;
    private Double r = 1d;

    private String errText;

    private ArrayList<Point> points;

    public ControllerBean() {
        super();
        points = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        try {
            dbService = new DatabaseService();
            areaCheckService = new AreaCheckService();

            points = dbService.getAllPoints();
            logger.info("Points fetched: {}", points);
        } catch (IOException e) {
            logger.error("SQL EXCEPTION: {}", String.valueOf(e));
            errText = e.getLocalizedMessage();
        } catch (Exception e) {
            logger.error(e);
            errText = e.getLocalizedMessage();
        }
    }



    public void submit() {
        logger.info("X: {}, Y: {}, R: {}", x, y, r);

        var point = areaCheckService.checkAndGetPoint(x, y, r);
        try {
            dbService.addPoint(point);
            logger.info("point added to DB");

            points.add(point);
        } catch (SQLException e) {
            logger.error(e);
            errText = e.getLocalizedMessage();
        }
    }

    private void validateY() {
        if (y < -5 || y > 5) {
            errText = "Y must be in [-5; 5]";
            throw new ValidatorException(new FacesMessage("Y must be between -5 and 5"));
        }
    }

    public void validateY(
            FacesContext context,
            jakarta.faces.component.UIComponent component,
            Object value
    ) {
        try {
            y = (Double) value;

            if (y < -5 || y > 5) {
                errText = "Y must be in [-5; 5]";
                throw new ValidatorException(new FacesMessage("Y must be between -5 and 5"));
            }
        } catch (NumberFormatException e) {
            errText = "Couldn't parse Y to Number";
            throw new ValidatorException(new FacesMessage("Couldn't parse Y to Number"));
        }

    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getR() {
        return r;
    }

    public void setR(Double r) {
        this.r = r;
    }

    public String getErrText() {
        return errText;
    }

    public void setErrText(String errText) {
        this.errText = errText;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }
}
