package com.FP_Final.FP.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Petición al microservicio IA para buscar coincidencias en el catálogo AEMPS")
public class MatchRequestDTO {

    @JsonProperty("busqueda_usuario")
    @Schema(example = "ibuprofeno 600", description = "Texto libre introducido por el usuario para buscar el medicamento")
    private String busquedaUsuario;

    @JsonProperty("lista_limpia")
    @Schema(description = "Lista de medicamentos del catálogo AEMPS sobre la que buscar")
    private List<MedicamentoAempsDTO> listaLimpia;

    public MatchRequestDTO() {}

    public MatchRequestDTO(String busquedaUsuario, List<MedicamentoAempsDTO> listaLimpia) {
        this.busquedaUsuario = busquedaUsuario;
        this.listaLimpia = listaLimpia;
    }

    public String getBusquedaUsuario() { return busquedaUsuario; }
    public void setBusquedaUsuario(String busquedaUsuario) { this.busquedaUsuario = busquedaUsuario; }
    public List<MedicamentoAempsDTO> getListaLimpia() { return listaLimpia; }
    public void setListaLimpia(List<MedicamentoAempsDTO> listaLimpia) { this.listaLimpia = listaLimpia; }
}
