package com.FP_Final.FP.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.FP_Final.FP.model.Articulos;
import com.FP_Final.FP.model.Insert_DTO;
import com.FP_Final.FP.model.UpdateDTO;
import com.FP_Final.FP.model.VentaDTO;
import com.FP_Final.FP.service.ArticuloService;

import java.util.List;

@RestController
@RequestMapping("/articulos")
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

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
            return ResponseEntity.noContent().build(); // 204 No Content si no hay artículos
        }
        
        return ResponseEntity.ok(articulos);
    }
    
    /*
     * Este metodo es para cdo hacer una venta 
     * tenemos que modificar los stocks de los articulos comprados, por supuesto a menos!!*/
    
    @PutMapping("/updateStock")
    public ResponseEntity<List<Articulos>> updateArticuloStock(@RequestBody List<UpdateDTO> updateDTOs) {
        List<Articulos> updatedArticulos = articuloService.updateStockBatch(updateDTOs);
        if (updatedArticulos == null || updatedArticulos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedArticulos);
    }
    
    //Para hacer update a un articulo solo cant y precio!!
    @PutMapping("/updateItem")
    public ResponseEntity<Articulos> updateArticulo(@RequestBody UpdateDTO upDTO) {
    	
		Articulos updatedArticulo = articuloService.updateItem(upDTO);
		
		if (updatedArticulo == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(updatedArticulo);
    }
    
	
	//Para crear un articulo
	@PostMapping("/insert")
	public ResponseEntity<Articulos> createArticulo(@RequestBody Insert_DTO insertDto) {
		
		Articulos createdArticulo = articuloService.InsertItem(insertDto);
		return ResponseEntity.ok(createdArticulo);
	}
    
    
    
    // Para borrar un articulo!!
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticulo(@PathVariable int id) {
        articuloService.deleteArticuloById(id);
        return ResponseEntity.noContent().build();
    }
    
 
    
    
}
