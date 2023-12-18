package com.example.gateway.db.repository;

import com.example.gateway.db.entity.Temperature;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TemperatureRepository extends JpaRepository<Temperature, Long> {

    @Query("""
        select t from Temperature t
        where t.node.id = :nodeId
        order by t.id
    """)
    List<Temperature> findAllByNodeId(@Param("nodeId") Long nodeId);
}
