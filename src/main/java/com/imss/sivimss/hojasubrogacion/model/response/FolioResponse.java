package com.imss.sivimss.hojasubrogacion.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FolioResponse {

	private String idOrdenServicio;
	private String folioOrdenServicio;
}
