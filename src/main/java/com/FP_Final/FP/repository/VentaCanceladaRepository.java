package com.FP_Final.FP.repository;

import com.FP_Final.FP.model.VentaCancelada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaCanceladaRepository extends JpaRepository<VentaCancelada, Long> {
    // No hace falta escribir nada más, Spring hace la magia
}