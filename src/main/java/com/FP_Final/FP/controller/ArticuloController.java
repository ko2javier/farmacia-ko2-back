package com.FP_Final.FP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.FP_Final.FP.model.Articulos;
import com.FP_Final.FP.model.Insert_DTO;
import com.FP_Final.FP.model.UpdateDTO;
import com.FP_Final.FP.service.ActivityLogService;
import com.FP_Final.FP.service.ArticuloService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/articulos")
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private HttpServletRequest request;

    // GET /articulos/search/{keyword} — busca artículos por nombre
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Articulos>> buscarArticulos(@PathVariable String keyword) {
        List<Articulos> articulos = articuloService.searchByKeyword(keyword);
        if (articulos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articulos);
    }

    // GET /articulos/All — devuelve todo el inventario
    @GetMapping("/All")
    public ResponseEntity<List<Articulos>> obtenerTodos() {
        List<Articulos> articulos = articuloService.getAll();
        if (articulos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articulos);
    }

    // PUT /articulos/updateStock — descuenta stock de varios artículos (venta en lote)
    @PutMapping("/updateStock")
    public ResponseEntity<List<Articulos>> actualizarStockLote(@RequestBody List<UpdateDTO> updates) {
        List<Articulos> articulosActualizados = articuloService.updateStockBatch(updates);
        if (articulosActualizados == null || articulosActualizados.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articulosActualizados);
    }

    // PUT /articulos/updateItem — edita precio/cantidad de un artículo desde el almacén
    @PutMapping("/updateItem")
    public ResponseEntity<Articulos> actualizarArticulo(@RequestBody UpdateDTO cambios) {
        Articulos articuloActualizado = articuloService.updateItem(cambios);
        if (articuloActualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articuloActualizado);
    }

    // POST /articulos/insert — crea un nuevo artículo y lo registra en el activity log
    @PostMapping("/insert")
    public ResponseEntity<Articulos> crearArticulo(@RequestBody Insert_DTO insertDto) {
        Articulos nuevoArticulo = articuloService.insertarArticulo(insertDto);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        activityLogService.log(username, "INSERT_PRODUCT", nuevoArticulo.getNombre(), request.getRemoteAddr());
        return ResponseEntity.ok(nuevoArticulo);
    }

    // DELETE /articulos/{id} — elimina un artículo y registra la acción
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarArticulo(@PathVariable int id) {
        String nombre = articuloService.getById(id)
                .map(Articulos::getNombre)
                .orElse("id=" + id);
        articuloService.deleteArticuloById(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        activityLogService.log(username, "DELETE_PRODUCT", nombre, request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

    // PATCH /articulos/{id}/aemps — vincula un artículo con su ficha oficial AEMPS
    @PatchMapping("/{id}/aemps")
    public ResponseEntity<Articulos> vincularAemps(@PathVariable int id,
                                                    @RequestBody Map<String, String> body) {
        Articulos articuloActualizado = articuloService.linkAemps(id, body.get("aempsCode"), body.get("laboratorio"));
        return ResponseEntity.ok(articuloActualizado);
    }
}
