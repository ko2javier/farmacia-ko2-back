package com.FP_Final.FP.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.FP_Final.FP.model.Ventas;

public interface VentasRepository   extends JpaRepository<Ventas, Integer>{
	
	List<Ventas> findByusername(String username);

}
