package com.FP_Final.FP;

import com.FP_Final.FP.config.JwtUtil;
import com.FP_Final.FP.controller.AuthController;
import com.FP_Final.FP.service.ActivityLogService;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthControllerTest {

    // Mocks
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private JdbcTemplate jdbcTemplate;

    // Controller bajo prueba
    // @InjectMocks crea el AuthController e inyecta los mocks de arriba.
    // PERO: AuthController tiene un constructor que recibe JdbcTemplate,
    // así que Mockito lo crea vía constructor. El resto (@Autowired) los
    // inyecta por campo usando ReflectionTestUtils en el @BeforeEach.
    @InjectMocks
    private AuthController authController;

    //  Preparación común antes de cada test
    @BeforeEach
    void setUp() {
        // Inyectamos los campos @Autowired que Mockito no mete por constructor
        ReflectionTestUtils.setField(authController, "authenticationManager", authenticationManager);
        ReflectionTestUtils.setField(authController, "jwtUtil", jwtUtil);
        ReflectionTestUtils.setField(authController, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(authController, "activityLogService", activityLogService);
        ReflectionTestUtils.setField(authController, "request", request);

        // La IP que simula cualquier petición
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    }


    // TEST 1 — Login correcto: devuelve el token JWT
    @Test
    void login_credencialesCorrectas_devuelveToken() {

        // preparo los datos y configuro los mocks que necesito para este test
        // Datos que "envia el usuario" en el body JSON
        Map<String, String> body = new HashMap<>();
        body.put("username", "admin");
        body.put("password", "1234");

        // Simulamos que Spring Security acepta las credenciales sin lanzar excepción
        Authentication authMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);

        // Simulamos la consulta SQL al JdbcTemplate
        when(jdbcTemplate.queryForObject(
                eq("SELECT permiso FROM users WHERE username = ?"),
                any(Object[].class),
                eq(String.class)
        )).thenReturn("ADMIN");

        // Simulamos que JwtUtil genera un token
        when(jwtUtil.generateToken("admin", "ADMIN")).thenReturn("fake-jwt-token");

        // llamo al método del controller con los datos de arriba
        Map<String, String> response = authController.login(body);

        // compruebo que el resultado es el que esperaba
        assertNotNull(response, "La respuesta no debería ser null");
        assertEquals("fake-jwt-token", response.get("token"), "El token devuelto no coincide");

        // Verificamos que se llamó al log con LOGIN_SUCCESS
        verify(activityLogService).log("admin", "LOGIN_SUCCESS", "admin", "127.0.0.1");
    }


    // TEST 2 — Login fallido: credenciales incorrectas → lanza excepción

    @Test
    void login_credencialesIncorrectas_lanzaExcepcionYLogaFailed() {

        // preparo los datos y configuro los mocks que necesito para este test
        Map<String, String> body = new HashMap<>();
        body.put("username", "admin");
        body.put("password", "wrongpassword");

        // Simulamos que Spring Security RECHAZA las credenciales
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        // Ejecutamos el método y comprobamos que lanza la excepción esperada
        // Verificamos que el controller relanza la excepción (no la traga)
        assertThrows(AuthenticationException.class, () -> authController.login(body));

        // Verificamos que se logó el intento fallido
        verify(activityLogService).log("admin", "LOGIN_FAILED", "admin", "127.0.0.1");

        // El token nunca se generó
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }


    // TEST 3 — Body sin username: el log usa "unknown"

    @Test
    void login_sinUsername_logaComoUnknown() {

        // preparo los datos y configuro los mocks que necesito para este test
        // Body vacío → username == null
        Map<String, String> body = new HashMap<>();
        body.put("password", "1234");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("User not found"));

        // Ejecutamos el método y comprobamos que lanza la excepción esperada
        assertThrows(AuthenticationException.class, () -> authController.login(body));

        // El controller hace: username != null ? username : "unknown"
        // Como username es null → debe logar "unknown"
        verify(activityLogService).log("unknown", "LOGIN_FAILED", null, "127.0.0.1");
    }


    // TEST 4 — Endpoint /protected devuelve el mensaje esperado
  
    @Test
    void protectedEndpoint_devuelveMensajeDeAutenticacion() {

        String resultado = authController.protectedEndpoint();

        assertEquals(
                "Este es un endpoint protegido. Estás autenticado.",
                resultado
        );
    }
}
