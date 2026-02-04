package com.FP_Final.FP.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "authorities")
public class Authorities {
	

	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto_increment
	    private int id;

	    @Column(nullable = false, unique = true, length = 50)
	    private String username;
	    @Column(nullable = false, length = 50)
	    private String role;

	    public Authorities() {}
	    
	    
	    public Authorities(String username, String role) {
	        this.username = username;
	        this.role = role;
	    }


		public String getUsername() {
			return username;
		}


		public void setUsername(String username) {
			this.username = username;
		}


		public String getRole() {
			return role;
		}


		public void setRole(String role) {
			this.role = role;
		}

	   

}
