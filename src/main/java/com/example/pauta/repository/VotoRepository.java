package com.example.pauta.repository;

import com.example.pauta.repository.entity.VotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotoRepository extends JpaRepository<VotoEntity, VotoEntity.PautaUserKey> {

    List<VotoEntity> findByVotoKeyPautaId(long pautaId);

}
