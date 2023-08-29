package com.imss.sivimss.hojasubrogacion.model.request;

import lombok.Data;

@Data
public class FiltrosRequest {
    private Integer idVelatorio;
    private String folio;
    private Integer idProveedor;
    private String fecha;
}
