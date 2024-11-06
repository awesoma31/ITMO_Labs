package org.awesoma.back.repository;

import org.awesoma.back.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PointRepository extends JpaRepository<Point, Long>, JpaSpecificationExecutor<Point> {
}