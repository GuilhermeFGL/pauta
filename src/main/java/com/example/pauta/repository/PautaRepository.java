package com.example.pauta.repository;

import com.example.pauta.repository.entity.PautaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PautaRepository extends JpaRepository<PautaEntity, Long> {

    @Query( "SELECT p FROM com.example.pauta.repository.entity.PautaEntity p " +
            "WHERE p.id = :id and p.status = com.example.pauta.repository.entity.enums.PautaStatus.CREATED")
    Optional<PautaEntity> findByIdAndStatusIsCreated(Long id);

}
