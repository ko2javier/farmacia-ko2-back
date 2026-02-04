package com.FP_Final.FP.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.FP_Final.FP.model.Articulos;

public interface ArticulosRepository extends JpaRepository<Articulos, Integer> {
	 List<Articulos> findByNombreContaining(String keyword);
	 Articulos findByCodigo(String codigo);

}
