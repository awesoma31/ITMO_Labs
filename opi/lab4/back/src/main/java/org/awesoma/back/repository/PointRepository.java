package org.awesoma.back.repository;

import org.awesoma.back.model.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long>, JpaSpecificationExecutor<Point> {
    List<Point> findAllByOwnerId(Long id);

    Page<Point> findAllByOwnerId(Long id, Pageable pageable);

    int countPointsByOwnerId(Long userId);
}