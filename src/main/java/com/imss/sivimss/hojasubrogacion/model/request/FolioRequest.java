package com.imss.sivimss.hojasubrogacion.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FolioRequest {

	private Integer idVelatorio;
	
	private String folioOrdenServicio;
	
	private String nombreProveedor;
}
