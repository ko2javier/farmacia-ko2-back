package com.FP_Final.FP.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.FP_Final.FP.model.Authorities;
import com.FP_Final.FP.model.Users;
import com.FP_Final.FP.repository.AuthoritiesRepository;
import com.FP_Final.FP.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    // Devuelve todos los usuarios del sistema
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    // Busca un usuario por su ID numérico
    public Optional<Users> getUserById(int id) {
        return userRepository.findById(id);
    }

    // Busca un usuario por su nombre de usuario
    public Optional<Users> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Elimina un usuario por su ID
    public void deleteUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Crea un nuevo usuario con contraseña encriptada y lo registra en authorities
    public Users crearUsuario(String username, String rawPassword, String permiso) {
        Users nuevoUsuario = new Users(username, passwordEncoder.encode(rawPassword), true, permiso);
        userRepository.save(nuevoUsuario);
        // Guardamos el rol en la tabla authorities para que Spring Security lo reconozca
        authoritiesRepository.save(new Authorities(username, "ROLE_" + permiso));
        return nuevoUsuario;
    }

    // Actualiza permiso y contraseña de un usuario, sincronizando también su authority
    public Users actualizarUsuario(Users usuario) {
        // 1. Cargamos el usuario actual desde la base de datos
        Users usuarioExistente = userRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuario.getId()));

        // 2. Buscamos su authority (puede no existir si hubo un error de sincronización previo)
        Authorities authorityExistente = authoritiesRepository.findByUsername(usuarioExistente.getUsername())
                .orElse(null);

        // 3. Aplicamos los cambios al usuario
        usuarioExistente.setPermiso(usuario.getPermiso());

        // Solo encriptamos la contraseña si viene rellena (puede que solo cambie el permiso)
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        userRepository.save(usuarioExistente);

        // 4. Sincronizamos la authority — si no existe, la creamos (auto-reparación)
        if (authorityExistente != null) {
            authorityExistente.setRole("ROLE_" + usuario.getPermiso());
            authoritiesRepository.save(authorityExistente);
        } else {
            authoritiesRepository.save(new Authorities(usuarioExistente.getUsername(), "ROLE_" + usuario.getPermiso()));
        }

        return usuarioExistente;
    }
}
