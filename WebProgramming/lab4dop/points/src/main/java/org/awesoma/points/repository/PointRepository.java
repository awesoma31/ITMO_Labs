package org.awesoma.points.repository;

import org.awesoma.points.model.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PointRepository extends JpaRepository<Point, Long>, JpaSpecificationExecutor<Point> {
    Page<Point> findAllByOwnerId(Long id, Pageable pageable);

    int countPointsByOwnerId(Long userId);
}