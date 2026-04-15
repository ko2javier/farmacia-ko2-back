package com.FP_Final.FP.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.FP_Final.FP.model.Authorities;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthoritiesRepository extends JpaRepository<Authorities, Integer>{

    Optional<Authorities> findByUsername(String username);

}
