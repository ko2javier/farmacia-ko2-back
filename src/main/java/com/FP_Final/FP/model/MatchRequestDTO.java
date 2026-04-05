package com.FP_Final.FP.model;

import java.util.List;

public class MatchRequestDTO {

    private String busquedaUsuario;
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
