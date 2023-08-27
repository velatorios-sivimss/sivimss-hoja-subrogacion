package com.imss.sivimss.hojasubrogacion.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.hojasubrogacion.beans.ConsultaHojaSubrogacion;
import com.imss.sivimss.hojasubrogacion.model.request.ReporteRequest;
import com.imss.sivimss.hojasubrogacion.service.ConsultaHojaSubrogacionService;
import com.imss.sivimss.hojasubrogacion.util.AppConstantes;
import com.imss.sivimss.hojasubrogacion.util.DatosRequest;
import com.imss.sivimss.hojasubrogacion.util.LogUtil;
import com.imss.sivimss.hojasubrogacion.util.MensajeResponseUtil;
import com.imss.sivimss.hojasubrogacion.util.ProviderServiceRestTemplate;
import com.imss.sivimss.hojasubrogacion.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConsultaHojaSubrogacionServiceImpl  implements ConsultaHojaSubrogacionService {
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Value("${endpoints.mod-catalogos}")
	private String urlModCatalogos;
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	@Autowired
	private LogUtil logUtil;
	
	@Value("${reporte.consulta-hoja-subrogacion}")
	private String reporteConsultaHojaSubrogacion;
	
	Response<Object> response;
	
	private static final String ERROR_AL_DESCARGAR_DOCUMENTO = "64"; // Error en la descarga del documento.Intenta  nuevamente.
	private static final String NO_SE_ENCONTRO_INFORMACION = "45"; // No se encontró información relacionada a tu
	private static final String ERROR_AL_EJECUTAR_EL_QUERY = "Error al ejecutar el query ";
	private static final String FALLO_AL_EJECUTAR_EL_QUERY = "Fallo al ejecutar el query: ";
	private static final String ERROR_INFORMACION = "52"; // Error al consultar la información.
	private static final String GENERAR_DOCUMENTO = "Generar Reporte: " ;
	private static final String GENERA_DOCUMENTO = "Genera_Documento";
	private static final String CONSULTA_PAGINADO = "/paginado";
	private static final String CU37_NAME= "consulta hoja subrogacion : ";
	private static final String CONSULTA = "consulta";

	@Override
	public Response<Object> consultaHojaSubrogacion(DatosRequest request, Authentication authentication) throws IOException {
		String consulta = "";
		try {
			ReporteRequest reporteRequest= new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ReporteRequest.class);
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consulta plan SFPA ", CONSULTA, authentication);
			Map<String, Object> envioDatos = new ConsultaHojaSubrogacion().consultaHojaSubrogacion(request, reporteRequest).getDatos();
			consulta = queryDecoded(envioDatos);
			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicio(envioDatos,
					urlModCatalogos.concat(CONSULTA_PAGINADO), authentication),NO_SE_ENCONTRO_INFORMACION);
		} catch (Exception e) {
			e.printStackTrace();
			log.error( CU37_NAME + ERROR_AL_EJECUTAR_EL_QUERY + consulta);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + consulta, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}
	
	private String queryDecoded (Map<String, Object> envioDatos ) {
		return new String(DatatypeConverter.parseBase64Binary(envioDatos.get(AppConstantes.QUERY).toString()));
	}
	
	@Override
	public Response<Object> generarReporteHojaSubrogacion(DatosRequest request, Authentication authentication)
			throws IOException {
		String consulta = "";
		try {
			ReporteRequest reporteRequest= new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ReporteRequest.class);
			logUtil.crearArchivoLog(Level.INFO.toString(), CU37_NAME + GENERAR_DOCUMENTO + " Reporte Plan SFPA " + this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), "generarReporteHojaSubrogacion", GENERA_DOCUMENTO, authentication);
			Map<String, Object> envioDatos = new ConsultaHojaSubrogacion().generarReporteConsultaHojaSubrogacion(reporteRequest,reporteConsultaHojaSubrogacion);
			consulta = envioDatos.get("condicion").toString();
			response = providerRestTemplate.consumirServicio(envioDatos, urlReportes, authentication);
			MensajeResponseUtil.mensajeResponseObject(response, ERROR_AL_DESCARGAR_DOCUMENTO);
		} catch (Exception e) {
			log.error( CU37_NAME + ERROR_AL_EJECUTAR_EL_QUERY + consulta);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + consulta, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
		return response;
	}

}
