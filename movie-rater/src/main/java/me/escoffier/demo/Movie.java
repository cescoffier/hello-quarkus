package me.escoffier.demo;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Movie extends PanacheEntity {

    public String title;
    public int rating;

}
