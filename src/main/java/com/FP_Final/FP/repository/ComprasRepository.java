package com.FP_Final.FP.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.FP_Final.FP.model.ComprasCliente;



public interface ComprasRepository extends JpaRepository<ComprasCliente, Integer>{
    List<ComprasCliente> findByDnicliente(String dnicliente);
    
    List<ComprasCliente> findByProducto(String producto);


}
