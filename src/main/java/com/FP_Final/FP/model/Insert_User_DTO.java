package com.FP_Final.FP.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para registrar un nuevo usuario")
public class Insert_User_DTO {

	@Schema(example = "farmaceutico01", description = "Nombre de usuario único")
	private String username;

	@Schema(example = "Pass1234!", description = "Contraseña (se almacenará cifrada con BCrypt)")
	private String password;

	@Schema(example = "ADMIN", description = "Rol del usuario: USER, ADMIN o SUPERADMIN")
	private String permiso;

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getPermiso() { return permiso; }
	public void setPermiso(String permiso) { this.permiso = permiso; }
}
