package com.FP_Final.FP.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.FP_Final.FP.model.Users;



public interface UserRepository extends JpaRepository<Users, Integer>{
	Optional<Users> findByUsername(String username);

}
