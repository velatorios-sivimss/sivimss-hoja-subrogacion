package com.imss.sivimss.hojasubrogacion.util;

public class AppConstantes {

	public static final String EXITO = "Exito";
	public static final String NUMERO_DE_PAGINA = "0";
	public static final String TAMANIO_PAGINA = "10";
	public static final String ORDER_BY = "id";
	public static final String ORDER_DIRECTION = "asc";
	public static final String SUPERVISOR = "Supervisor";
	public static final String CATALOGO_CONSULTAR= "/consulta";
	public static final String CATALOGO_CONSULTA_PAGINADO= "/paginado";
	public static final String CATALOGO_CREAR_MULTIPLE= "/crearMultiple";
	public static final String CATALOGO_ACTUALIZAR= "/actualizar";
	public static final String PROCESO= "/lote/generico";
	
	public static final String DATOS = "datos";
	public static final String QUERY = "query";
	public static final String STATUSEXCEPTION = "status";
	public static final String EXPIREDJWTEXCEPTION = "expired";
	public static final String MALFORMEDJWTEXCEPTION = "malformed";
	public static final String UNSUPPORTEDJWTEXCEPTION = "unsupported";
	public static final String ILLEGALARGUMENTEXCEPTION = "illegalArgument";
	public static final String SIGNATUREEXCEPTION = "signature";
	public static final String FORBIDDENEXCEPTION = "forbidden";

	public static final String EXPIREDJWTEXCEPTION_MENSAJE = "Token expirado.";
	public static final String MALFORMEDJWTEXCEPTION_MENSAJE = "Token mal formado.";
	public static final String UNSUPPORTEDJWTEXCEPTION_MENSAJE = "Token no soportado.";
	public static final String ILLEGALARGUMENTEXCEPTION_MENSAJE = "Token vacío.";
	public static final String SIGNATUREEXCEPTION_MENSAJE = "Fallo la firma.";
	public static final String FORBIDDENEXCEPTION_MENSAJE = "No tiene autorización para realizar la solicitud.";
	public static final String CIRCUITBREAKER = "186"; // El servicio no responde, no permite más llamadas.
	public static final String OCURRIO_ERROR_GENERICO = "187";// Ocurrio un error al procesar tu solicitud.
	
	public static final String CONSULTA = "consulta";
	public static final String ERROR_QUERY = "Error al ejecutar el query ";
	public static final String ERROR_LOG_QUERY = "Fallo al ejecutar el query:  ";
	public static final String ERROR_CONSULTAR = "52";//Error al consultar la información.
	public static final String ERROR_GUARDAR = "5";//Error al guardar la información. Intenta nuevamente.
	public static final String MODIFICACION = "modificacion";

	private AppConstantes() {
		throw new IllegalStateException("AppConstantes class");
	}

}
