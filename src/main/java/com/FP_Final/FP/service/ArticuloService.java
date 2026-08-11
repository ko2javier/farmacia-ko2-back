package com.FP_Final.FP.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.FP_Final.FP.model.Articulos;
import com.FP_Final.FP.model.Insert_DTO;
import com.FP_Final.FP.model.UpdateDTO;
import com.FP_Final.FP.repository.ArticulosRepository;

@Service
public class ArticuloService {

    @Autowired
    private ArticulosRepository articulosRepository;

    // Devuelve todos los artículos del inventario
    public List<Articulos> getAll() {
        return articulosRepository.findAll();
    }

    // Busca artículos cuyo nombre contenga la palabra clave
    public List<Articulos> searchByKeyword(String keyword) {
        return articulosRepository.findByNombreContaining(keyword);
    }

    // Descuenta stock de varios artículos en una sola transacción (venta en lote)
    @Transactional
    public List<Articulos> updateStockBatch(List<UpdateDTO> updates) {
        List<Articulos> articulosActualizados = new ArrayList<>();
        for (UpdateDTO update : updates) {
            Articulos articulo = articulosRepository.findByCodigo(update.getCodigo());
            if (articulo == null || articulo.getCantidad() < update.getCantidadVendida()) {
                throw new RuntimeException("Artículo no encontrado o cantidad insuficiente: " + update.getCodigo());
            }
            // Restamos las unidades vendidas del stock actual
            articulo.setCantidad(articulo.getCantidad() - update.getCantidadVendida());
            articulosActualizados.add(articulosRepository.save(articulo));
        }
        return articulosActualizados;
    }

    // Actualiza precio y cantidad de un artículo concreto (edición desde almacén)
    public Articulos updateItem(UpdateDTO cambios) {
        Articulos articulo = articulosRepository.findByCodigo(cambios.getCodigo());
        if (articulo == null) {
            throw new RuntimeException("Artículo no encontrado: " + cambios.getCodigo());
        }
        articulo.setCantidad(cambios.getCantidad());
        articulo.setPrecio(cambios.getPrecio());
        return articulosRepository.save(articulo);
    }

    // Genera un número aleatorio de 13 dígitos  que no exista ya en base de datos
    private String generarCodigoUnico() {
        String codigo;
        long numeroAleatorio;
        do {
            numeroAleatorio = (long) (Math.random() * 10000000000000L);
            codigo = String.format("%013d", numeroAleatorio);
        } while (articulosRepository.findByCodigo(codigo) != null);
        return codigo;
    }

    // Crea un nuevo artículo en el inventario con código único autogenerado
    public Articulos insertarArticulo(Insert_DTO insertDto) {
        if (insertDto == null) {
            throw new RuntimeException("Los datos del artículo no pueden estar vacíos");
        }
        String codigo = generarCodigoUnico();
        Articulos nuevoArticulo = new Articulos(
                insertDto.getNombre(),
                insertDto.getCategoria(),
                insertDto.getPrecio(),
                insertDto.getCantidad(),
                codigo
        );
        nuevoArticulo.setAempsCode(insertDto.getAempsCode());
        nuevoArticulo.setLaboratorio(insertDto.getLaboratorio());
        return articulosRepository.save(nuevoArticulo);
    }

    // Busca un artículo por su ID (usado antes de borrar para obtener el nombre)
    public Optional<Articulos> getById(int id) {
        return articulosRepository.findById(id);
    }

    // Elimina un artículo del inventario por su ID
    public void deleteArticuloById(int id) {
        if (!articulosRepository.existsById(id)) {
            throw new RuntimeException("Artículo no encontrado con id: " + id);
        }
        articulosRepository.deleteById(id);
    }

    // Vincula un artículo del inventario con su ficha oficial en AEMPS
    public Articulos linkAemps(int id, String aempsCode, String laboratorio) {
        Articulos articulo = articulosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado con id: " + id));
        articulo.setAempsCode(aempsCode);
        articulo.setLaboratorio(laboratorio);
        return articulosRepository.save(articulo);
    }
}
