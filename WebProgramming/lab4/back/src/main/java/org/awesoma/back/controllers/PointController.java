package org.awesoma.back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.awesoma.back.model.Point;
import org.awesoma.back.repository.dto.PointDTO;
import org.awesoma.back.services.PointsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/test")
    public String getPoints() {
        return "get test!";
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPoint(@RequestBody PointDTO pointDTO, @RequestHeader("Authorization") String token) {
        log.info("trying add point");
        try {
            var p = pointsService.addPoint(pointDTO, token);
            return ResponseEntity.ok("Point added!");
        } catch (RuntimeException e) {
            log.error("Error adding point", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error adding point", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
