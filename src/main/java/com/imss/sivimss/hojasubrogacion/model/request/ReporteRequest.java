package com.imss.sivimss.hojasubrogacion.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteRequest {
	
	private Integer idVelatorio;
	private Integer idOrdenServicio;
	private Integer idProveedor;
	private String fechaInicio;
	private String fechaFin;
	private String tipoReporte;
	
}
