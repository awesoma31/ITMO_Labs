package org.awesoma.back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.awesoma.back.model.Point;
import org.awesoma.back.repository.dto.PageDTO;
import org.awesoma.back.repository.dto.PointDTO;
import org.awesoma.back.services.PointsService;
import org.springframework.data.domain.Page;
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

    //todo lazy fetch
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageDTO<Point>> getPointsById(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token
    ) {
        try {
            Page<Point> pointsPage = pointsService.getAllPointsById(token, page, size);
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


    @GetMapping("/test")
    public String getPoints() {
        return "get test!";
    }

//    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Map<String, String>> addPoint(@RequestBody PointDTO pointDTO, @RequestHeader("Authorization") String token) {
//        log.info("trying add point");
//        try {
//            pointsService.addPoint(pointDTO, token);
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Point added!");
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            log.error("Error adding point", e);
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            log.error("Error adding point", e);
//            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//        }
//    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Point> addPoint(@RequestBody PointDTO pointDTO, @RequestHeader("Authorization") String token) {
        log.info("trying add point");
        try {
            var p = pointsService.addPoint(pointDTO, token);

             return ResponseEntity.ok(p);
        } catch (RuntimeException e) {
            log.error("Error adding point", e);
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error adding point", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
