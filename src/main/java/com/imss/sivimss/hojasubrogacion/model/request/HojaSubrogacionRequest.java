package com.imss.sivimss.hojasubrogacion.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HojaSubrogacionRequest {
	private Integer idHojaSubrogacion;
	private Integer idOrdenServicio;
	private Integer idDelegacion;
	private Integer idVelatorio;
	private Integer idProveedor;
	private String fecGeneracionHoja;
	private String tipoTraslado ;
	private Integer idFinado;
	private String origen;
	private String destino;
	private String distanciaRecorrer;
	private Integer idServicio;
	private String especificaciones;
	private String nombreOperador;
	private String carrozaNum;
	private String numeroPlacas;
	private String diaPartida;
	private String horaPartida;
	private String nomAcompaniante;	
}
