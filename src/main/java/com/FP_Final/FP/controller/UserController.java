package com.FP_Final.FP.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.FP_Final.FP.model.Insert_User_DTO;
import com.FP_Final.FP.model.Users;
import com.FP_Final.FP.repository.UserRepository;
import com.FP_Final.FP.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    
	@Autowired
	private UserRepository userRepository;

    // Endpoint para obtener todos los usuarios
    @GetMapping("/all")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }
    
 // Endpoint para delete user !!
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para obtener un usuario por username. No lo usare 
    
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
    	
        Optional<Users> userOptional = userService.getUserByUsername(username);
        
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with username: " + username);
        }
    }
    
    @PostMapping("/insert")
    public ResponseEntity<?> insertarUsuario(@RequestBody Insert_User_DTO insertDto) {	
    	// Chequeo qie el username no exista
    	Optional<Users> userOpt = userRepository.findByUsername(insertDto.getUsername());
    	
    	if (userOpt.isPresent()) {
			return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body("El usuario ya existe");
		}
    	else {
        	Users nuevoUsuario = userService.crearUsuario(insertDto.getUsername(), insertDto.getPassword(), insertDto.getPermiso());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        }
        
    }
    @PutMapping("/update")
	public ResponseEntity<Users> updateUser( @RequestBody Users user) {
		Users updatedUser = userService.Update_Usuario(user);
		return ResponseEntity.ok(updatedUser);
	}
    
    
    
}