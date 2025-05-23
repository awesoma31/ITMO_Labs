package org.awesoma.back.services;

import lombok.extern.slf4j.Slf4j;
import org.awesoma.back.model.Point;
import org.awesoma.back.model.User;
import org.awesoma.back.repository.PointRepository;
import org.awesoma.back.repository.UserRepository;
import org.awesoma.back.repository.dto.PointDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PointsService {
    private final PointRepository pr;
    private final UserRepository ur;
    private final AreaCheckService areaCheckService;
    private final TokenService tokenService;

    public PointsService(PointRepository pointRepository, UserRepository ur, AreaCheckService areaCheckService, TokenService tokenService) {
        this.pr = pointRepository;
        this.ur = ur;
        this.areaCheckService = areaCheckService;
        this.tokenService = tokenService;
    }

    public List<Point> getAllPoints() {
        return pr.findAll();
    }

    public Point addPoint(PointDTO pointDTO) {
        try {
            String username = tokenService.getUsernameFromJWT(TokenService.getTokenFromContext());

            User user = ur.getByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

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
    public List<Point> getAllPointsById() {
        try {
            var id = tokenService.getUserIdFromToken(TokenService.getTokenFromContext());

            return pr.findAllByOwnerId(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Page<Point> getAllPointsById(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            var id = tokenService.getUserIdFromToken(TokenService.getTokenFromContext());

            return pr.findAllByOwnerId(id, pageable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getTotalPoints() {
        Long userId = tokenService.getUserIdFromToken(TokenService.getTokenFromContext());

        return pr.countPointsByOwnerId(userId);
    }
}
