package com.example.gateway.db.repository;

import com.example.gateway.db.entity.Node;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends JpaRepository<Node, String> {
    List<Node> findAll();
}
