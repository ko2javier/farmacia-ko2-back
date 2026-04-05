package com.FP_Final.FP.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.FP_Final.FP.service.ActivityLogService;
import com.FP_Final.FP.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private HttpServletRequest request;

    private String getCallerRole() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        Optional<Users> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String callerRole = getCallerRole();
        String callerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String targetPermiso = userOpt.get().getPermiso();
        String targetUsername = userOpt.get().getUsername();

        if ("ADMIN".equals(callerRole) && ("ADMIN".equals(targetPermiso) || "SUPERADMIN".equals(targetPermiso))) {
            activityLogService.log(callerUsername, "FORBIDDEN_ACTION",
                    "Intento de DELETE sobre usuario " + targetUsername + " con rol " + targetPermiso,
                    request.getRemoteAddr());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Un ADMIN no puede eliminar usuarios con rol ADMIN o SUPERADMIN");
        }

        userService.deleteUserById(id);
        activityLogService.log(callerUsername, "DELETE_USER", targetUsername, request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

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
        String callerRole = getCallerRole();
        String callerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if ("ADMIN".equals(callerRole) && !"SELLER".equals(insertDto.getPermiso())) {
            activityLogService.log(callerUsername, "FORBIDDEN_ACTION",
                    "Intento de crear usuario con rol " + insertDto.getPermiso(),
                    request.getRemoteAddr());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Un ADMIN solo puede crear usuarios con rol SELLER");
        }

        Optional<Users> userOpt = userRepository.findByUsername(insertDto.getUsername());
        if (userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya existe");
        }

        Users nuevoUsuario = userService.crearUsuario(insertDto.getUsername(), insertDto.getPassword(), insertDto.getPermiso());
        activityLogService.log(callerUsername, "INSERT_USER", nuevoUsuario.getUsername(), request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody Users user) {
        String callerRole = getCallerRole();
        String callerUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Users> targetOpt = userService.getUserById(user.getId());
        if (targetOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String targetPermiso = targetOpt.get().getPermiso();
        String targetUsername = targetOpt.get().getUsername();

        if ("ADMIN".equals(callerRole) && ("ADMIN".equals(targetPermiso) || "SUPERADMIN".equals(targetPermiso))) {
            activityLogService.log(callerUsername, "FORBIDDEN_ACTION",
                    "Intento de modificar usuario " + targetUsername + " con rol " + targetPermiso,
                    request.getRemoteAddr());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Un ADMIN no puede modificar usuarios con rol ADMIN o SUPERADMIN");
        }

        Users updatedUser = userService.Update_Usuario(user);
        return ResponseEntity.ok(updatedUser);
    }
}
