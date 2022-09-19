package com.example.pauta.repository.dao;

import com.example.pauta.repository.dao.enums.PautaResult;
import com.example.pauta.repository.dao.enums.PautaStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity(name = "pauta")
public class PautaDao {

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
    private PautaStatus status;

    @Column
    private PautaResult result;

}
