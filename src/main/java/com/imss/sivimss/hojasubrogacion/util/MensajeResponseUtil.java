package com.imss.sivimss.hojasubrogacion.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MensajeResponseUtil {
	
	private static final Logger log = LoggerFactory.getLogger(MensajeResponseUtil.class);
	
	private static final String ERROR = "Error.. {}";

	private MensajeResponseUtil() {
		super();
	}

	public static Response<?> mensajeResponse(Response<?> respuestaGenerado, String numeroMensaje) {
		Integer codigo = respuestaGenerado.getCodigo();
		if (codigo == 200) {
			respuestaGenerado.setMensaje(numeroMensaje);
		} else {
			log.error(ERROR, respuestaGenerado.getMensaje());
			respuestaGenerado.setMensaje("5");
		}
		return respuestaGenerado;
	}
	
	public static Response<Object> mensajeResponseObject(Response<Object> respuestaGenerado, String numeroMensaje) {
		Integer codigo = respuestaGenerado.getCodigo();
		if (codigo == 200) {
			respuestaGenerado.setMensaje(numeroMensaje);
		} else {
			log.error(ERROR, respuestaGenerado.getMensaje());
			respuestaGenerado.setMensaje("5");
		}
		return respuestaGenerado;
	}

	public static Response<?> mensajeConsultaResponse(Response<?> respuestaGenerado, String numeroMensaje) {
		Integer codigo = respuestaGenerado.getCodigo();
		if (codigo == 200 && (!respuestaGenerado.getDatos().toString().contains("id"))) {
			respuestaGenerado.setMensaje(numeroMensaje);
		}
		return respuestaGenerado;
	}
	
	public  static Response<Object>mensajeConsultaResponseObject(Response<Object> respuestaGenerado, String numeroMensaje) {
		Integer codigo = respuestaGenerado.getCodigo();
		if (codigo == 200 &&  !(respuestaGenerado.getDatos().toString().contains("[]")) ){
			respuestaGenerado.setMensaje(AppConstantes.EXITO);
		}else if (codigo == 400 || codigo == 404 || codigo == 500 ) {
			log.error(ERROR, respuestaGenerado.getMensaje());
			respuestaGenerado.setMensaje(numeroMensaje);
		}
		return respuestaGenerado;
	}

}
