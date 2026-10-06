package com.cinemalog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "genres")
public class Genre {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    protected Genre() {
    }

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
