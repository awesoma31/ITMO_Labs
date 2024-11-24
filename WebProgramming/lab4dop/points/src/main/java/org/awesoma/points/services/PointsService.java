package org.awesoma.points.services;

import lombok.extern.slf4j.Slf4j;
import org.awesoma.points.exceptions.InvalidPageParamException;
import org.awesoma.points.model.Point;
import org.awesoma.points.model.User;
import org.awesoma.points.repository.PointRepository;
import org.awesoma.points.repository.dto.PageDTO;
import org.awesoma.points.repository.dto.PointDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        } catch (NullPointerException e) {
            log.error("Error adding point", e);
            throw e;
        } catch (Exception e) {
            log.error("Error adding point", e);
            throw new RuntimeException("Failed to add point: " + e.getMessage(), e);
        }
    }

    public PageDTO<Point> getPointsPageById(int page, int size, Long id) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Point> pointsPage = pr.findAllByOwnerId(id, pageable);

        return new PageDTO<>(
                pointsPage.getContent(),
                pointsPage.getNumber(),
                pointsPage.getSize(),
                pointsPage.getTotalElements(),
                pointsPage.getTotalPages()
        );
    }

    public PageDTO<Point> getPointsPageById(String pageParam, int size, Long id) {
        int pageNumber;
        int totalElements = pr.countPointsByOwnerId(id);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        if (totalPages == 0) {
            totalPages = 1;
        }

        if (pageParam.equalsIgnoreCase("last")) {
            pageNumber = totalPages - 1;
            log.info("last page");
        } else {
            try {
                pageNumber = Integer.parseInt(pageParam) - 1;
                log.info("page number: {}", pageNumber);
                if (pageNumber < 0) {
                    pageNumber = 0;
                } else if (pageNumber >= totalPages) {
                    pageNumber = totalPages - 1;
                }
            } catch (NumberFormatException e) {
                throw new InvalidPageParamException("Invalid page number: " + pageParam);
            }
        }

        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Point> page = pr.findAllByOwnerId(id, pageable);

        log.info("page number: {}, page size: {}, total elements: {}, total pages: {}, content: {}", pageNumber, size, totalElements, totalPages, page.getContent());

        return new PageDTO<>(
                page.getContent(),
                pageNumber + 1,
                size,
                totalElements,
                totalPages
        );
    }

    public int getTotalPointsById(Long id) {
        return pr.countPointsByOwnerId(id);
    }
}
