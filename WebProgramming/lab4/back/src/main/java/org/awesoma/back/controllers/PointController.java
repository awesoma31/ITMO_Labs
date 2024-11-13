package org.awesoma.back.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Points")
public class PointController {
    private final PointsService pointsService;

    public PointController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all points", description = "Retrieve a list of all points", security = {
            @SecurityRequirement(name = "bearerAuth")
    })
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of points")
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
    @Operation(summary = "Get paginated points", description = "Retrieve a paginated list of points by page number and size", security = {
            @SecurityRequirement(name = "bearerAuth")
    })
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list of points")
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
    @Operation(summary = "Add a new point", description = "Add a new point to the database", security = {
            @SecurityRequirement(name = "bearerAuth")
    })
    @ApiResponse(responseCode = "200", description = "Successfully added the point")
    @ApiResponse(responseCode = "400", description = "Invalid point data provided")
    @ApiResponse(responseCode = "500", description = "Internal server error")
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
    @Operation(
            summary = "Get total number of points",
            description = "Retrieve the total number of points in the database",
            security = {
                    @SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the total number of points")
    @ApiResponse(responseCode = "400", description = "Error retrieving total points")
    @ApiResponse(responseCode = "500", description = "Internal server error")
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
}
