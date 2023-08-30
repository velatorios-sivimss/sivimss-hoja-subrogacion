package com.imss.sivimss.hojasubrogacion.util;

public enum ConstantsMensajes {
	REGISTRO_NO_ENCONTRADO("Registro no encontrado"),
	REGISTRO_CREADO("Registro creado con exito"),
	REGISTRO_EXISTENTE("Registro existente"),
	EXITO("Exito"),
	DENEGADO("denegado"),
	ERROR_GUARDAR("Error al guardar la informacion"),
	OCURRIO_ERROR_GENERICO("\" - Ocurrio un error al procesar tu solicitud. "
					+ "Verifica tu informaci\\u00f3n e intenta nuevamente. "
					+ "Si el problema persiste, contacta al responsable de la administraci\\u00f3n "
					+ "del sistema.\""),
	ERROR_NO_AUTORIZADO("Error de Autenticacion, no cuenta con los permisos necesarios."),
	ERROR_VALIDACION_CAPTURA("Error, faltan vehiculos por capturar"),
	ERROR_VALIDACION_VALIDAR("Error, faltan vehiculos por validar"),
	REPORTE_CAPTURA("captura"),
	REPORTE_VALIDA("validacion"),
	FORMATO_FECHA("yyyy-MM-dd hh:mm:ss"),
	ANNIO("Annio: {}"),
	MES("Mes: {}"),
	IDOOAD("idOoad: {}");
	
	String mensaje;

	private ConstantsMensajes(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getMensaje() {
		return mensaje;
	}
	
	
}
