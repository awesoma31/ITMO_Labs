package org.awesoma.back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.awesoma.back.model.Point;
import org.awesoma.back.repository.dto.PageDTO;
import org.awesoma.back.repository.dto.PointDTO;
import org.awesoma.back.services.PointsService;
import org.awesoma.back.services.TokenService;
import org.awesoma.back.util.AuthenticationException;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/points")
public class PointController {
    private final PointsService pointsService;

    public PointController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Point>> getAllPoints() {
        try {
            List<Point> pl = pointsService.getAllPoints();
            return ResponseEntity.ok(pl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //todo lazy fetch
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageDTO<Point>> getPointsById(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<Point> pointsPage = pointsService.getAllPointsById(page, size);
            PageDTO<Point> response = new PageDTO<>(
                    pointsPage.getContent(),
                    pointsPage.getNumber(),
                    pointsPage.getSize(),
                    pointsPage.getTotalElements(),
                    pointsPage.getTotalPages()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Point> addPoint(
            @RequestBody PointDTO pointDTO
    ) {
        log.info("trying add point");
        try {
            var p = pointsService.addPoint(pointDTO);

             return ResponseEntity.ok(p);
        } catch (RuntimeException e) {
            log.error("Error adding point", e);
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error adding point", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/total")
    public ResponseEntity<Integer> getTotalPoints() {
        try {
            int totalPoints = pointsService.getTotalPoints();
            return ResponseEntity.ok(totalPoints);
        } catch (RuntimeException e) {
            log.error("Error getting total points", e);
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error getting total points", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    private static String getTokenFromContext() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("User is not authenticated");
        }

        TokenService.JwtUser jwtUser = (TokenService.JwtUser) authentication.getPrincipal();
        return jwtUser.getAccessToken();
    }
}
