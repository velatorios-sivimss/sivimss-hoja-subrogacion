package com.imss.sivimss.hojasubrogacion.service.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imss.sivimss.hojasubrogacion.beans.ConsultaHojaSubrogacion;
import com.imss.sivimss.hojasubrogacion.model.request.FiltrosRequest;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.hojasubrogacion.beans.HojaSubrogacion;
import com.imss.sivimss.hojasubrogacion.beans.FolioOrdenServicio;
import com.imss.sivimss.hojasubrogacion.model.request.FolioRequest;
import com.imss.sivimss.hojasubrogacion.model.request.HojaSubrogacionRequest;
import com.imss.sivimss.hojasubrogacion.model.request.ReporteRequest;
import com.imss.sivimss.hojasubrogacion.model.request.UsuarioDto;
import com.imss.sivimss.hojasubrogacion.model.response.FolioResponse;
import com.imss.sivimss.hojasubrogacion.model.response.ProveedorResponse;
import com.imss.sivimss.hojasubrogacion.service.HojaSubrogacionService;
import com.imss.sivimss.hojasubrogacion.util.AppConstantes;
import com.imss.sivimss.hojasubrogacion.util.ConstantsMensajes;
import com.imss.sivimss.hojasubrogacion.util.Database;
import com.imss.sivimss.hojasubrogacion.util.DatosRequest;
import com.imss.sivimss.hojasubrogacion.util.ExceptionGenerico;
import com.imss.sivimss.hojasubrogacion.util.LogUtil;
import com.imss.sivimss.hojasubrogacion.util.MensajeResponseUtil;
import com.imss.sivimss.hojasubrogacion.util.ProviderServiceRestTemplate;
import com.imss.sivimss.hojasubrogacion.util.Response;

@Service
public class HojaSubrogacionServiceImpl implements HojaSubrogacionService{

	private static final Logger log = LoggerFactory.getLogger(HojaSubrogacionServiceImpl.class);
	
	@Autowired
	private ProviderServiceRestTemplate providerServiceRestTemplate;
	
	@Autowired 
	private ModelMapper modelMapper;
	
	@Autowired
	private LogUtil logUtil;
	
	@Value("${endpoints.mod-catalogos}")
	private String urlDominio;
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	@Value("${reporte.consulta-hoja-subrogacion}")
	private String reporteConsultaHojaSubrogacion;
	
	@Autowired
	private Database database;
	
	private Connection connection; 
	private ResultSet rs;	
	private Statement statement;
	
	Response<Object> response;


	private static final String CU037_NOMBRE = "Hoja-Subrogacion: ";
	private static final String INGRESAR_HOJA_SUBROGACION = "Ingresar Nueva Hoja Subrogacion: " ;
	private static final String MODIFICAR_HOJA_SUBROGACION = "Modificar Hoja Subrogacion: " ;
	private static final String FALLO_QUERY = "Fallo al ejecutar el Query  ";
	
	private static final String AGREGADO_CORRECTAMENTE = "30"; // Agregado correctamente..
	private static final String MODIFICADO_CORRECTAMENTE = "18"; // Modificado correctamente.
	
	private static final String ERROR_AL_DESCARGAR_DOCUMENTO = "64"; // Error en la descarga del documento.Intenta  nuevamente.
	private static final String ERROR_AL_EJECUTAR_EL_QUERY = "Error al ejecutar el query ";
	private static final String ERROR_INFORMACION = "52"; // Error al consultar la información.
	private static final String GENERAR_DOCUMENTO = "Generar Reporte: " ;
	private static final String GENERA_DOCUMENTO = "Genera_Documento";
	private static final String CONSULTA = "consulta";
	
	public HojaSubrogacionServiceImpl(ProviderServiceRestTemplate providerServiceRestTemplate, ModelMapper modelMapper, LogUtil logUtil) {
		this.providerServiceRestTemplate = providerServiceRestTemplate;
		this.modelMapper=modelMapper;
		this.logUtil=logUtil;
	}

	@Override
	public Response<Object> consultarFolioOrden(DatosRequest request, Authentication authentication) throws IOException {
		String consulta = "";
		try {
            logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), "consultarFolioOrden", AppConstantes.CONSULTA, authentication);
            List<FolioResponse>folioResponses;
            FolioRequest folioRequest  = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), FolioRequest.class);
			Response<Object>response;
			response = providerServiceRestTemplate.consumirServicio(new FolioOrdenServicio().obtenerFolios(request, folioRequest).getDatos(), 
					urlDominio.concat(AppConstantes.CATALOGO_CONSULTAR), authentication);
			consulta =new FolioOrdenServicio().obtenerFolios(request, folioRequest).getDatos().get(AppConstantes.QUERY).toString();
			if (response.getCodigo()==200 && !response.getDatos().toString().contains("[]")) {
				folioResponses= Arrays.asList(modelMapper.map(response.getDatos(), FolioResponse[].class));
				response.setDatos(folioResponses);
			}
			return MensajeResponseUtil.mensajeConsultaResponseObject(response, AppConstantes.ERROR_CONSULTAR);
		} catch (Exception e) {
	        String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
	        log.error(AppConstantes.ERROR_QUERY.concat(decoded));
	        log.error(e.getMessage());
	        logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), AppConstantes.ERROR_LOG_QUERY + decoded, AppConstantes.CONSULTA, authentication);
	        throw new IOException(AppConstantes.ERROR_CONSULTAR, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarProveedor(DatosRequest request, Authentication authentication) throws IOException {
		String consulta = "";
		try {
            logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), "consultarFolioOrden", AppConstantes.CONSULTA, authentication);
            List<ProveedorResponse>proveedorResponse;
            FolioRequest folioRequest  = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), FolioRequest.class);
			Response<Object>response;
			response = providerServiceRestTemplate.consumirServicio(new FolioOrdenServicio().obtenerProveedores(request, folioRequest).getDatos(), 
					urlDominio.concat(AppConstantes.CATALOGO_CONSULTAR), authentication);
			consulta =new FolioOrdenServicio().obtenerProveedores(request, folioRequest).getDatos().get(AppConstantes.QUERY).toString();
			if (response.getCodigo()==200 && !response.getDatos().toString().contains("[]")) {
				proveedorResponse= Arrays.asList(modelMapper.map(response.getDatos(), ProveedorResponse[].class));
				response.setDatos(proveedorResponse);
			}
			return MensajeResponseUtil.mensajeConsultaResponseObject(response, AppConstantes.ERROR_CONSULTAR);
		} catch (Exception e) {
	        String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
	        log.error(AppConstantes.ERROR_QUERY.concat(decoded));
	        log.error(e.getMessage());
	        logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), AppConstantes.ERROR_LOG_QUERY + decoded, AppConstantes.CONSULTA, authentication);
	        throw new IOException(AppConstantes.ERROR_CONSULTAR, e.getCause());
		}
	}

	@Override
	public Response<?> busquedaFiltros(DatosRequest request, Authentication authentication) throws IOException {
		Gson json = new Gson();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		JsonObject jsonObject = JsonParser.parseString(String.valueOf(request.getDatos())).getAsJsonObject();
		String pagina = jsonObject.get("pagina").getAsString();
		String tamanio = jsonObject.get("tamanio").getAsString();
		FiltrosRequest filtros = json.fromJson(datosJson,FiltrosRequest.class);
		return providerServiceRestTemplate.consumirServicio( new ConsultaHojaSubrogacion().busquedaFiltros(filtros,pagina,tamanio).getDatos()
		,urlDominio + AppConstantes.CATALOGO_CONSULTA_PAGINADO,authentication);
	}

	@Override
	public Response<?> generarHojaSubrogacion(DatosRequest request, Authentication authentication) throws IOException {
		JsonObject jsonObj = JsonParser.parseString((String) request.getDatos().get(AppConstantes.DATOS)).getAsJsonObject();
		String idHojaSr = jsonObj.get("idHojaSubrogacion").getAsString();
		Map<String, Object> datosReporte = new HashMap<>();
		datosReporte.put("rutaNombreReporte","reportes/plantilla/ANEXO7_ORDEN_SUBROGACION.jrxml");
		datosReporte.put("tipoReporte","pdf");
		datosReporte.put("idHojaSubrogacion",idHojaSr);
		return providerServiceRestTemplate.consumirServicioReportes(datosReporte, urlReportes, authentication);
	}

	@Override
	public Response<?> busquedaServicios(DatosRequest request, Authentication authentication) throws IOException {
		JsonObject jsonObj = JsonParser.parseString((String) request.getDatos().get(AppConstantes.DATOS)).getAsJsonObject();
		String idOrdenServicio = jsonObj.get("idOrdenServicio").getAsString();
		return providerServiceRestTemplate.consumirServicio( new ConsultaHojaSubrogacion().buscarServicios(idOrdenServicio).getDatos()
				,urlDominio + AppConstantes.CATALOGO_CONSULTAR,authentication);
	}

	
	@Override
	public Response<Object> insertaHojaSubrogacion(DatosRequest request, Authentication authentication) throws IOException {
		Response<Object>response = null;
		String query = ""; 
		try { 
			Gson gson = new Gson();
			UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
			HojaSubrogacion hojaSubrogacion = new HojaSubrogacion();
			String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
			HojaSubrogacionRequest hojaSubrogacionRequest = gson.fromJson(datosJson, HojaSubrogacionRequest.class);
			query = hojaSubrogacion.queryInsertHojaSubrogacion(hojaSubrogacionRequest, usuario.getIdUsuario());
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), "insertaHojaSubrogacion: ", AppConstantes.QUERY, authentication);
			response = execQueryInsert(query);
			return MensajeResponseUtil.mensajeConsultaResponseObject(response, AGREGADO_CORRECTAMENTE);
		
		} catch (Exception e) {
			logUtil.crearArchivoLog(Level.WARNING.toString(), CU037_NOMBRE + e.getCause().getMessage() + "- " + this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(),query, INGRESAR_HOJA_SUBROGACION,
					authentication);
	     throw new IOException(AppConstantes.ERROR_GUARDAR, e.getCause());
		}
	}

	@Override
	public Response<Object> modificarHojaSubrogacion(DatosRequest request, Authentication authentication) throws IOException {
		Response<Object> response = null;
		String query = "";
		try {
			Gson gson = new Gson();
			UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
			HojaSubrogacion hojaSubrogacion = new HojaSubrogacion();
			String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
			HojaSubrogacionRequest hojaSubrogacionRequest = gson.fromJson(datosJson, HojaSubrogacionRequest.class);
			query = hojaSubrogacion.queryUpdateHojaSubrogacion(hojaSubrogacionRequest, usuario.getIdUsuario());
			response = execQueryActualiza(query);
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), "modificarHojaSubrogacion: ", AppConstantes.CONSULTA, authentication);
	        
			return MensajeResponseUtil.mensajeConsultaResponseObject(response, MODIFICADO_CORRECTAMENTE);
		
		} catch (Exception e) {
			log.error( CU037_NOMBRE + MODIFICAR_HOJA_SUBROGACION + ", {}",query);
			logUtil.crearArchivoLog(Level.WARNING.toString(), CU037_NOMBRE + e.getCause().getMessage() + "- " + this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(),query, MODIFICAR_HOJA_SUBROGACION,
					authentication);
	     throw new IOException(AppConstantes.ERROR_GUARDAR, e.getCause());
		}
	}

	public Response<Object> execQueryInsert(String query) throws SQLException, ExceptionGenerico{
		try {
			connection = database.getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			statement.execute(query, Statement.RETURN_GENERATED_KEYS);
			rs = statement.getGeneratedKeys();
			if (rs.next()) {
				connection.setAutoCommit(true);
				return new Response<>(false, HttpStatus.OK.value(), ConstantsMensajes.EXITO.getMensaje(),
						rs.getInt(1) );
			}
		}catch (Exception e) {			
			throw new ExceptionGenerico(FALLO_QUERY, e.getCause(), log);
		} finally {
			if (statement!=null) {
					statement.close();
			}
			if (rs!= null) {
					rs.close();
			}
		}
		return null;
	}

	public Response<Object> execQueryActualiza(String query) throws SQLException, ExceptionGenerico{
		try {
			connection = database.getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			connection.setAutoCommit(true);
			return new Response<>(false, HttpStatus.OK.value(), ConstantsMensajes.EXITO.getMensaje(), true);
		}catch (Exception e) {
			throw new ExceptionGenerico(FALLO_QUERY, e.getCause(), log);
		} finally {
			if (statement!=null) {
				statement.close();
			}
			if (rs!= null) {
				rs.close();
			}
		}
	}
	
	@Override
	public Response<Object> generarReporteHojaSubrogacion(DatosRequest request, Authentication authentication)
			throws IOException {
		String consulta = "";
		try {
			ReporteRequest reporteRequest= new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ReporteRequest.class);
			logUtil.crearArchivoLog(Level.INFO.toString(), CU037_NOMBRE + GENERAR_DOCUMENTO + " Reporte Plan SFPA " + this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), "generarReporteHojaSubrogacion", GENERA_DOCUMENTO, authentication);
			Map<String, Object> envioDatos = new ConsultaHojaSubrogacion().generarReporteConsultaHojaSubrogacion(reporteRequest,reporteConsultaHojaSubrogacion);
			consulta = envioDatos.get("condicion").toString();
			response = providerServiceRestTemplate.consumirServicio(envioDatos, urlReportes, authentication);
			MensajeResponseUtil.mensajeResponseObject(response, ERROR_AL_DESCARGAR_DOCUMENTO);
		} catch (Exception e) {
			log.error( CU037_NOMBRE + ERROR_AL_EJECUTAR_EL_QUERY + ", {}",consulta);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_QUERY + consulta, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
		return response;
	}
	
}
