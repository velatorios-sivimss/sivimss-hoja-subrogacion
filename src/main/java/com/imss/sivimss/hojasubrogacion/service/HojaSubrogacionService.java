package com.imss.sivimss.hojasubrogacion.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.hojasubrogacion.util.DatosRequest;
import com.imss.sivimss.hojasubrogacion.util.Response;

public interface HojaSubrogacionService {
	
	Response<Object>consultarFolioOrden(DatosRequest request, Authentication authentication) throws IOException;

}
