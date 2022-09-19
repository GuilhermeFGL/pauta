package com.example.pauta.repository;

import com.example.pauta.repository.dao.PautaDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PautaRepository extends JpaRepository<PautaDao, Long> {

}
