package org.awesoma.points.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.points.model.BigUser;
import org.awesoma.points.model.Point;
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
    private final ContextService contextService;

    public PointsService(PointRepository pointRepository, AreaCheckService areaCheckService, ContextService contextService) {
        this.pr = pointRepository;
        this.areaCheckService = areaCheckService;
        this.contextService = contextService;
    }

    public List<Point> getAllPoints() {
        return pr.findAll();
    }

    public Point addPoint(PointDTO pointDTO) {
        try {
            var user = ContextService.getUserFromContext().getUser();
            String username = user.getUsername();

//            User user =

            Point point = areaCheckService.checkAndGetPoint(pointDTO);

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

    //todo fix recursion
//    public List<Point> getAllPointsById() {
////        try {
////            var userId = contextService.getUserIdFromContext();
////            var id = tokenService.getUserIdFromToken(TokenService.getTokenFromContext());
////
////            return pr.findAllByOwnerId(id);
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
//    }

    public Page<Point> getAllPointsById(int page, int size, HttpServletRequest request) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            var id = contextService.getUserIdFromRequest(request);

            return pr.findAllByOwnerId(id, pageable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Page<Point> getAllPointsById(int page, int size, BigUser bigUser) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            return pr.findAllByOwnerId(bigUser.getUser().getId(), pageable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getTotalPoints(HttpServletRequest request) {
        Long userId = contextService.getUserIdFromRequest(request);

        return pr.countPointsByOwnerId(userId);
    }

    public int getTotalPoints(BigUser bigUser) {
        Long userId = bigUser.getUser().getId();
        return pr.countPointsByOwnerId(userId);
    }
}
