package com.FP_Final.FP.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // Extrae el rol del usuario autenticado (sin el prefijo "ROLE_")
    private String obtenerRolDelCaller() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .orElse("");
    }

    // GET /users/all — devuelve la lista completa de usuarios
    @GetMapping("/all")
    public ResponseEntity<List<Users>> obtenerTodos() {
        List<Users> usuarios = userService.getAllUsers();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    // DELETE /users/{id} — elimina un usuario (ADMIN no puede borrar ADMIN ni SUPERADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable int id) {
        Optional<Users> usuarioOpt = userService.getUserById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String rolCaller = obtenerRolDelCaller();
        String usernameCaller = SecurityContextHolder.getContext().getAuthentication().getName();
        String permisoObjetivo = usuarioOpt.get().getPermiso();
        String usernameObjetivo = usuarioOpt.get().getUsername();

        // Restricción: ADMIN no puede eliminar usuarios de igual o mayor jerarquía
        if ("ADMIN".equals(rolCaller) && ("ADMIN".equals(permisoObjetivo) || "SUPERADMIN".equals(permisoObjetivo))) {
            activityLogService.log(usernameCaller, "FORBIDDEN_ACTION",
                    "Intento de DELETE sobre usuario " + usernameObjetivo + " con rol " + permisoObjetivo,
                    request.getRemoteAddr());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Un ADMIN no puede eliminar usuarios con rol ADMIN o SUPERADMIN");
        }

        userService.deleteUserById(id);
        activityLogService.log(usernameCaller, "DELETE_USER", usernameObjetivo, request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

    // GET /users/{username} — busca un usuario por su nombre de usuario
    @GetMapping("/{username}")
    public ResponseEntity<?> obtenerPorUsername(@PathVariable String username) {
        Optional<Users> usuarioOpt = userService.getUserByUsername(username);
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado: " + username);
        }
    }

    // POST /users/insert — crea un nuevo usuario (ADMIN solo puede crear SELLER)
    @PostMapping("/insert")
    public ResponseEntity<?> insertarUsuario(@RequestBody Insert_User_DTO insertDto) {
        String rolCaller = obtenerRolDelCaller();
        String usernameCaller = SecurityContextHolder.getContext().getAuthentication().getName();

        // Restricción: ADMIN solo puede crear usuarios con rol SELLER
        if ("ADMIN".equals(rolCaller) && !"SELLER".equals(insertDto.getPermiso())) {
            activityLogService.log(usernameCaller, "FORBIDDEN_ACTION",
                    "Intento de crear usuario con rol " + insertDto.getPermiso(),
                    request.getRemoteAddr());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Un ADMIN solo puede crear usuarios con rol SELLER");
        }

        Optional<Users> usuarioExistente = userRepository.findByUsername(insertDto.getUsername());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya existe");
        }

        Users nuevoUsuario = userService.crearUsuario(insertDto.getUsername(), insertDto.getPassword(), insertDto.getPermiso());
        activityLogService.log(usernameCaller, "INSERT_USER", nuevoUsuario.getUsername(), request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    // PUT /users/update — actualiza datos de un usuario (ADMIN no puede modificar ADMIN ni SUPERADMIN)
    @PutMapping("/update")
    public ResponseEntity<?> actualizarUsuario(@RequestBody Users usuario) {
        String rolCaller = obtenerRolDelCaller();
        String usernameCaller = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Users> usuarioObjetivoOpt = userService.getUserById(usuario.getId());
        if (usuarioObjetivoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String permisoObjetivo = usuarioObjetivoOpt.get().getPermiso();
        String usernameObjetivo = usuarioObjetivoOpt.get().getUsername();

        // Restricción: ADMIN no puede modificar usuarios de igual o mayor jerarquía
        if ("ADMIN".equals(rolCaller) && ("ADMIN".equals(permisoObjetivo) || "SUPERADMIN".equals(permisoObjetivo))) {
            activityLogService.log(usernameCaller, "FORBIDDEN_ACTION",
                    "Intento de modificar usuario " + usernameObjetivo + " con rol " + permisoObjetivo,
                    request.getRemoteAddr());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Un ADMIN no puede modificar usuarios con rol ADMIN o SUPERADMIN");
        }

        Users usuarioActualizado = userService.actualizarUsuario(usuario);
        return ResponseEntity.ok(usuarioActualizado);
    }
}
