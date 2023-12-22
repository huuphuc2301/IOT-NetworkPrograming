package com.example.gateway.db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Getter
@Setter
@Entity
@Table(name = "node")
public class Node {
    @ToString.Include
    @EqualsAndHashCode.Include
    @Id
    private String id;

    private String name;

    private boolean status;

    public Node(String id, String name, boolean status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public Node() {

    }
}
