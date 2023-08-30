package com.imss.sivimss.hojasubrogacion.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Setter
@Getter
public class TareasDTO {

	 private String tipoHoraMinuto;
	 private String cveTarea;
	 private Integer totalHoraMinuto;
	 private String tipoEjecucion; 
	 private String validacion;
	 private Object datos;
	 public TareasDTO(String tipoHoraMinuto, String cveTarea, Integer totalHoraMinuto, String tipoEjecucion,
			String validacion, Object datos) {
		this.tipoHoraMinuto = tipoHoraMinuto;
		this.cveTarea = cveTarea;
		this.totalHoraMinuto = totalHoraMinuto;
		this.tipoEjecucion = tipoEjecucion;
		this.validacion = validacion;
		this.datos = datos;
	 }
	 
	 
}