package org.awesoma.points.services;

import org.awesoma.points.model.Point;
import org.awesoma.points.repository.dto.PointDTO;
import org.springframework.stereotype.Service;

@Service
public class AreaCheckService {
    public Point checkAndGetPoint(Double x, Double y, Double r) {
        boolean isInside = checkArea(x, y, r);

        return new Point(x, y, r, isInside);
    }

    public Point checkAndGetPoint(PointDTO pointDTO) {
        boolean isInside = checkArea(pointDTO.getX(), pointDTO.getY(), pointDTO.getR());
        return new Point(pointDTO.getX(), pointDTO.getY(), pointDTO.getR(), isInside);
    }

    private boolean checkArea(Double x, Double y, Double r) {
        if (y > 0 && x < 0) {
            return false;
        }
        if (y < 0 && x < 0) {
            if ((y * y + x * x) > r * r) {
                return false;
            }
        }
        if (y > 0 && x > 0) {
            if (y > (((double) -1 / 2) * x) + r / 2) {
                return false;
            }
        }
        if (x > 0 && y < 0) {
            return !(x > r) && !(y < -r / 2);
        }
        return true;
    }
}
