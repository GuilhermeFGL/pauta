package com.example.pauta.repository.entity;

import com.example.pauta.repository.entity.enums.PautaResult;
import com.example.pauta.repository.entity.enums.PautaStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@Entity(name = "pauta")
public class PautaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String description;

    @Column
    private Integer duration;

    @Column
    private LocalDateTime start;

    @Column
    private LocalDateTime end;

    @Column
    @Enumerated(EnumType.STRING)
    private PautaStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private PautaResult result;

}
