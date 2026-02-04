package com.FP_Final.FP.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.FP_Final.FP.model.Articulos;
import com.FP_Final.FP.model.Insert_DTO;
import com.FP_Final.FP.model.UpdateDTO;
import com.FP_Final.FP.repository.ArticulosRepository;

@Service
public class ArticuloService {
	
	@Autowired
    private ArticulosRepository art;
	
	public List<Articulos> getAll(){
		return art.findAll();
	}
	
	 public List<Articulos> searchByKeyword(String keyword) {
	        return art.findByNombreContaining(keyword);
	    }
	 
	 public List<Articulos> updateStockBatch(List<UpdateDTO> updates) {
		    // 1- Itero sobre la lista de actualizaciones
		 
		    List<Articulos> updatedList = new ArrayList<>();
		    for (UpdateDTO update : updates) {
		        // 2- Busco el artículo por código
		    	
		        Articulos articulo = art.findByCodigo(update.getCodigo());
		        if (articulo == null || articulo.getCantidad() < update.getCantidadVendida()) {
		            throw new RuntimeException("Artículo no encontrado o cantidad insuficiente: " + update.getCodigo());
		        }
		        // 3- hago update to stock
		        articulo.setCantidad(articulo.getCantidad() - update.getCantidadVendida());
		        
		        // 4- Save !!
		        updatedList.add(art.save(articulo));
		    }
		    return updatedList;
		}
	 
	 public Articulos updateItem(UpdateDTO updates) {
		    // 1- Busco el artículo
		 
		 Articulos articulo = art.findByCodigo(updates.getCodigo());
		 
		        if (articulo == null ) {
		            throw new RuntimeException("Artículo no encontrado : " + updates.getCodigo());
		        }
		        // 3- hago update to stock
		        articulo.setCantidad(updates.getCantidad());
		        articulo.setPrecio(updates.getPrecio());
		        
		        // 4- Save !!
		        return art.save(articulo);
		        
		    
		
		}
	 
	// Método to generar un código único 13dig
	 
	    private String generateUniqueCode() {
	        String code; long randomNum;
	        do {
	            // número aleatorio entre 0 y 9,999,999,999,999 
	             randomNum = (long) (Math.random() * 10000000000000L);
	             
	            code = String.format("%013d", randomNum);
	        } while (art.findByCodigo(code) != null);  // Repetir if exists!!

	        return code;
	    }
	 
	 public Articulos InsertItem(Insert_DTO updates) {
		 String code = generateUniqueCode(); // Genero un codigo unico y lo añado al nuevo articulo
		 
		 		 // Verifico que updates no sea null
		        if (updates == null ) {
		            throw new RuntimeException("Artículo Vacio: ");
		        }
		   
		        //public Articulos(String nombre, String categoria, double precio, int cantidad, String codigo)
		        
		        // 2- Devuelvo el articulo creado ok
		        
		        return art.save(new Articulos(updates.getNombre(), updates.getCategoria(), updates.getPrecio(),
		        		 updates.getCantidad(), code));       
		}

	 
	 public void deleteArticuloById(int id) {
		    if (!art.existsById(id)) {
		        throw new RuntimeException("Artículo no encontrado con id: " + id);
		    }
		    art.deleteById(id);
		}



	
}
