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

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Articulos>> searchArticulos(@PathVariable String keyword) {
        List<Articulos> articulos = articuloService.searchByKeyword(keyword);
        if (articulos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articulos);
    }

    @GetMapping("/All")
    public ResponseEntity<List<Articulos>> searchAll() {
        List<Articulos> articulos = articuloService.getAll();
        if (articulos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articulos);
    }

    @PutMapping("/updateStock")
    public ResponseEntity<List<Articulos>> updateArticuloStock(@RequestBody List<UpdateDTO> updateDTOs) {
        List<Articulos> updatedArticulos = articuloService.updateStockBatch(updateDTOs);
        if (updatedArticulos == null || updatedArticulos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedArticulos);
    }

    @PutMapping("/updateItem")
    public ResponseEntity<Articulos> updateArticulo(@RequestBody UpdateDTO upDTO) {
        Articulos updatedArticulo = articuloService.updateItem(upDTO);
        if (updatedArticulo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedArticulo);
    }

    @PostMapping("/insert")
    public ResponseEntity<Articulos> createArticulo(@RequestBody Insert_DTO insertDto) {
        Articulos createdArticulo = articuloService.InsertItem(insertDto);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        activityLogService.log(username, "INSERT_PRODUCT", createdArticulo.getNombre(), request.getRemoteAddr());
        return ResponseEntity.ok(createdArticulo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticulo(@PathVariable int id) {
        String nombre = articuloService.getById(id)
                .map(Articulos::getNombre)
                .orElse("id=" + id);
        articuloService.deleteArticuloById(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        activityLogService.log(username, "DELETE_PRODUCT", nombre, request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/aemps")
    public ResponseEntity<Articulos> linkAemps(@PathVariable int id,
                                                @RequestBody Map<String, String> body) {
        Articulos updated = articuloService.linkAemps(id, body.get("aempsCode"), body.get("laboratorio"));
        return ResponseEntity.ok(updated);
    }
}
