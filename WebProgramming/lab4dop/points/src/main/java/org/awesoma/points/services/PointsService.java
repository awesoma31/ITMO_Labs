package org.awesoma.points.services;

import lombok.extern.slf4j.Slf4j;
import org.awesoma.points.model.Point;
import org.awesoma.points.model.User;
import org.awesoma.points.repository.PointRepository;
import org.awesoma.points.repository.dto.PointDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PointsService {
    private final PointRepository pr;
    private final AreaCheckService areaCheckService;

    public PointsService(PointRepository pointRepository, AreaCheckService areaCheckService) {
        this.pr = pointRepository;
        this.areaCheckService = areaCheckService;
    }

    public Point addPoint(PointDTO pointDTO, String username, Long id) {
        try {
            Point point = areaCheckService.checkAndGetPoint(pointDTO);

            var user = new User();
            user.setUsername(username);
            user.setId(id);

            point.setOwner(user);
            log.info("point to be saved: {}", point);

            pr.save(point);
            log.info("point saved");
            return point;
        } catch (Exception e) {
            log.error("Error adding point", e);
            throw new RuntimeException("Failed to add point: " + e.getMessage(), e);
        }
    }

    public Page<Point> getAllPointsById(int page, int size, Long id) {
//        try {
            Pageable pageable = PageRequest.of(page, size);

            return pr.findAllByOwnerId(id, pageable);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    public int getTotalPointsById(Long id) {
        return pr.countPointsByOwnerId(id);
    }
}
