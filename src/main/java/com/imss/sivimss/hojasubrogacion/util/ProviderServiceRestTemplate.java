package com.imss.sivimss.hojasubrogacion.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.hojasubrogacion.security.jwt.JwtTokenProvider;




@Service
public class ProviderServiceRestTemplate {

	@Autowired
	private RestTemplateUtil restTemplateUtil;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private LogUtil logUtil;

	private static final String GENERA_DOCUMENTO = "Genera_Documento";
	private static final Logger log = LoggerFactory.getLogger(ProviderServiceRestTemplate.class);

	public Response<Object> consumirServicio(Map<String, Object> dato, String url, Authentication authentication) throws IOException {
		dato.remove(AppConstantes.DATOS);
		Response<Object> respuestaGenerado = restTemplateUtil.sendPostRequestByteArrayToken(url,
				new EnviarDatosRequest(dato), jwtTokenProvider.createToken((String) authentication.getPrincipal()),
				Response.class);
		return validarResponse(respuestaGenerado);
	}

	public Response<Object> consumirServicioReportes(Map<String, Object> dato,
			String url, Authentication authentication) throws IOException {
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName() + ".consumirServicioReportes", this.getClass().getPackage().toString(), "Resilencia", GENERA_DOCUMENTO, authentication);
		
			Response<Object> respuestaGenerado = restTemplateUtil.sendPostRequestByteArrayReportesToken(url,
					new DatosReporteDTO(dato),
					jwtTokenProvider.createToken((String) authentication.getPrincipal()), Response.class);
			return validarResponse(respuestaGenerado);
		} catch (IOException exception) {
			log.error("Ha ocurrido un error al recuperar la informacion");
			throw exception;
		}
	}

	public Response<Object> validarResponse(Response<Object> respuestaGenerado) {
		String codigo = respuestaGenerado.getMensaje().substring(0, 3);
		if (codigo.equals("500") || codigo.equals("404") || codigo.equals("400") || codigo.equals("403")) {
			Gson gson = new Gson();
			String mensaje = respuestaGenerado.getMensaje().substring(7, respuestaGenerado.getMensaje().length() - 1);

			ErrorsMessageResponse apiExceptionResponse = gson.fromJson(mensaje, ErrorsMessageResponse.class);

			respuestaGenerado = Response.builder().codigo((int) apiExceptionResponse.getCodigo()).error(true)
					.mensaje(apiExceptionResponse.getMensaje()).datos(apiExceptionResponse.getDatos()).build();

		}
		return respuestaGenerado;
	}

	public Response<Object> respuestaProvider(String e) {
		StringTokenizer exeception = new StringTokenizer(e, ":");
		Gson gson = new Gson();
		int i = 0;
		int totalToken = exeception.countTokens();
		StringBuilder mensaje = new StringBuilder("");
		int codigoError = HttpStatus.INTERNAL_SERVER_ERROR.value();
		int isExceptionResponseMs = 0;
		while (exeception.hasMoreTokens()) {
			String str = exeception.nextToken().trim();
			i++;
			if (i == 2) {
				codigoError = getError(str);				
			} else if (i == 3) {
				isExceptionResponseMs = esNumero(str);
				mensaje.append(getCodigoError(codigoError, str));

			} else if (i >= 4 && isExceptionResponseMs == 1) {
				if (i == 4) {
					mensaje.delete(0, mensaje.length());
				}
				mensaje.append(str).append(i != totalToken ? ":" : "");

			}
		}

		Response<Object> response;
		try {
			response = isExceptionResponseMs == 1 && !mensaje.toString().trim().equals("")
					? gson.fromJson(mensaje.substring(2, mensaje.length() - 1), Response.class)
					: new Response<>(true, codigoError, mensajeRespuesta(mensaje.toString().trim()),
							Collections.emptyList());
			log.info("respuestaProvider error: {}", e);
		} catch (Exception e2) {
			log.info("respuestaProvider error: {}", e2.getMessage());
			return new Response<>(true, HttpStatus.INTERNAL_SERVER_ERROR.value(), AppConstantes.OCURRIO_ERROR_GENERICO,
					Collections.emptyList());
		}

		return response;
	}

	private String mensajeRespuesta(String e) {
		return e.trim().equals("") ? AppConstantes.CIRCUITBREAKER : e.trim();
	}
	private int getError(String str) {
		int returnVal = HttpStatus.INTERNAL_SERVER_ERROR.value();
		String[] palabras = str.split("\\.");
		for (String palabra : palabras) {
			if ("BadRequestException".contains(palabra)) {
				returnVal = HttpStatus.BAD_REQUEST.value();
			} else if ("ResourceAccessException".contains(palabra)) {
				returnVal = HttpStatus.INTERNAL_SERVER_ERROR.value();

			}
		}
		return returnVal;
	}

	private int esNumero (String str) {
		if(str.trim().chars().allMatch(Character::isDigit))
			return 1;
		else 
			return 0;
	}
	private String getCodigoError (int codigoError, String str) {
		return (codigoError == HttpStatus.INTERNAL_SERVER_ERROR.value()? AppConstantes.CIRCUITBREAKER : str);
	}

}
