package com.imss.sivimss.hojasubrogacion.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imss.sivimss.hojasubrogacion.service.HojaSubrogacionService;
import com.imss.sivimss.hojasubrogacion.util.DatosRequest;
import com.imss.sivimss.hojasubrogacion.util.LogUtil;
import com.imss.sivimss.hojasubrogacion.util.ProviderServiceRestTemplate;
import com.imss.sivimss.hojasubrogacion.util.Response;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

@RestController
@RequestMapping("/hoja-subrogacion")
public class HojaSubrogacionController {
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Autowired
	private HojaSubrogacionService hojaSubrogacionService;
	
	@Autowired
	private LogUtil logUtil;
	
	private static final String RESILENCIA = " Resilencia  ";
	private static final String CONSULTA = "consulta";
	
	@PostMapping("/consultar/folio-orden")
	public CompletableFuture<Object>consultarFolioOrden(@RequestBody DatosRequest request, Authentication authentication) throws IOException, SQLException{
		Response<?>response = hojaSubrogacionService.consultarFolioOrden(request, authentication);
		return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	@PostMapping("/consultar/proveedor")
	public CompletableFuture<Object>consultarProveedor(@RequestBody DatosRequest request, Authentication authentication) throws IOException, SQLException{
		Response<?>response = hojaSubrogacionService.consultarProveedor(request, authentication);
		return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	
	
	/**
	 * fallbacks generico
	 * 
	 * @return respuestas
	 * @throws IOException 
	 */
	CompletableFuture<Object> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			CallNotPermittedException e) throws IOException {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());
		logUtil.crearArchivoLog(Level.INFO.toString(),this.getClass().getSimpleName(),this.getClass().getPackage().toString(),RESILENCIA, CONSULTA,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

	CompletableFuture<Object> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			RuntimeException e) throws IOException {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());
		logUtil.crearArchivoLog(Level.INFO.toString(),this.getClass().getSimpleName(),this.getClass().getPackage().toString(),RESILENCIA, CONSULTA,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

	CompletableFuture<Object> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			NumberFormatException e) throws IOException {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());
		logUtil.crearArchivoLog(Level.INFO.toString(),this.getClass().getSimpleName(),this.getClass().getPackage().toString(),RESILENCIA, CONSULTA,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

}