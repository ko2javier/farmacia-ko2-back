package com.FP_Final.FP.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MatchRequestDTO {

    @JsonProperty("busqueda_usuario")
    private String busquedaUsuario;

    @JsonProperty("lista_limpia")
    private List<MedicamentoAempsDTO> listaLimpia;

    public MatchRequestDTO() {}

    public MatchRequestDTO(String busquedaUsuario, List<MedicamentoAempsDTO> listaLimpia) {
        this.busquedaUsuario = busquedaUsuario;
        this.listaLimpia = listaLimpia;
    }

    public String getBusquedaUsuario() {
        return busquedaUsuario;
    }

    public void setBusquedaUsuario(String busquedaUsuario) {
        this.busquedaUsuario = busquedaUsuario;
    }

    public List<MedicamentoAempsDTO> getListaLimpia() {
        return listaLimpia;
    }

    public void setListaLimpia(List<MedicamentoAempsDTO> listaLimpia) {
        this.listaLimpia = listaLimpia;
    }
}
