package com.imss.sivimss.hojasubrogacion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4819422371941925970L;

	private final String nombreRecurso;

	private final String nombreCampo;

	private final String valorCampo;

	public ResourceNotFoundException(String nombreRecurso, String nombreCampo, String valorCampo) {
		super(String.format("%s no se encontro con %s : %s ", nombreRecurso, nombreCampo, valorCampo));

		this.nombreRecurso = nombreRecurso;
		this.nombreCampo = nombreCampo;
		this.valorCampo = valorCampo;
	}

}
