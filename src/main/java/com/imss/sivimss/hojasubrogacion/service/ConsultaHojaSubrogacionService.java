package com.imss.sivimss.hojasubrogacion.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.hojasubrogacion.util.DatosRequest;
import com.imss.sivimss.hojasubrogacion.util.Response;

public interface ConsultaHojaSubrogacionService {
	
	Response<Object> consultaHojaSubrogacion(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> generarReporteHojaSubrogacion(DatosRequest request, Authentication authentication)throws IOException ;

}
