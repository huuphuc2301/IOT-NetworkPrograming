package com.example.gateway.db.entity;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "temperature")
public class Temperature {
    @ToString.Include
    @EqualsAndHashCode.Include
    @Id
    private String id;

    @ManyToOne
    private Node node;

    private Integer value;

    private Instant createdAt;

}
