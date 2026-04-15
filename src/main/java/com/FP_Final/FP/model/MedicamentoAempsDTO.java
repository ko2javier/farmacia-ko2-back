package com.FP_Final.FP.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Medicamento del catálogo AEMPS")
public class MedicamentoAempsDTO {

    @Schema(example = "67031", description = "Código nacional AEMPS del medicamento")
    private String id;

    @Schema(example = "IBUPROFENO KERN PHARMA 600 MG", description = "Nombre oficial del medicamento en AEMPS")
    private String nombre;

    public MedicamentoAempsDTO() {}

    public MedicamentoAempsDTO(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
