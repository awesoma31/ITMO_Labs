package org.awesoma.points.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.points.exceptions.InvalidPageParamException;
import org.awesoma.points.model.Point;
import org.awesoma.points.model.UserPOJO;
import org.awesoma.points.repository.dto.PageDTO;
import org.awesoma.points.repository.dto.PointDTO;
import org.awesoma.points.services.PointsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/points")
@Tag(name = "Points")
public class PointController {
    private final PointsService pointsService;

    public PointController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageDTO<Point>> getPointsById(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody UserPOJO userPOJO
    ) {
        if (userPOJO == null || userPOJO.getId() == null || userPOJO.getUsername() == null) {
            log.error("NULL fields in body");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        try {
            PageDTO<Point> pointsPage = pointsService.getPointsPageById(page, size, userPOJO.getId());
            return ResponseEntity.ok(pointsPage);
//            Page<Point> pointsPage = pointsService.getAllPointsById(pageParam, size, userPOJO.getId());
//            PageDTO<Point> response = new PageDTO<>(
//                    pointsPage.getContent(),
//                    pointsPage.getNumber(),
//                    pointsPage.getSize(),
//                    pointsPage.getTotalElements(),
//                    pointsPage.getTotalPages()
//            );
//            return ResponseEntity.ok(response);
        } catch (InvalidPageParamException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }

        catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Point> addPoint(
            @RequestBody AddPointRequest addPointRequest
    ) {
        if (addPointRequest == null || addPointRequest.id == null || addPointRequest.username == null) {
            log.error("not all essential fields in body: {}", addPointRequest);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        log.info("ADD POINT request");
        try {
            var p = pointsService.addPoint(addPointRequest.getPointDTO(), addPointRequest.getUsername(), addPointRequest.getId());

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
    public ResponseEntity<Integer> getTotalPoints(@RequestBody UserPOJO bigUserPOJO) {
        if (bigUserPOJO == null) {
            log.error("body must not be null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        try {
            int totalPoints = pointsService.getTotalPointsById(bigUserPOJO.getId());
            return ResponseEntity.ok(totalPoints);
        } catch (RuntimeException e) {
            log.error("Error getting total points", e);
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error getting total points", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Getter
    public static class AddPointRequest {
        @NotBlank
        private Double x;
        @NotBlank
        private Double y;
        @NotBlank
        private Double r;
        @NotBlank
        private Long id;
        @NotBlank
        private String username;

        public PointDTO getPointDTO() {
            return new PointDTO(x, y, r);
        }
    }
}
