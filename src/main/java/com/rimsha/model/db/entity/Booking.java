package com.rimsha.model.db.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "rooms")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "date_start")
    LocalDate dateStart;

    @Column(name = "date_end")
    LocalDate dateEnd;

    @ManyToOne
    Room room;

    @Cascade(value = CascadeType.ALL)
    @OneToMany
    List<Service> services;
}