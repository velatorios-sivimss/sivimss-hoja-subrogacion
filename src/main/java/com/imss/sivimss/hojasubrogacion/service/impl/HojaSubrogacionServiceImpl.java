package com.imss.sivimss.hojasubrogacion.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imss.sivimss.hojasubrogacion.beans.ConsultaHojaSubrogacion;
import com.imss.sivimss.hojasubrogacion.model.request.FiltrosRequest;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.hojasubrogacion.beans.FolioOrdenServicio;
import com.imss.sivimss.hojasubrogacion.model.request.FolioRequest;
import com.imss.sivimss.hojasubrogacion.model.response.FolioResponse;
import com.imss.sivimss.hojasubrogacion.model.response.ProveedorResponse;
import com.imss.sivimss.hojasubrogacion.service.HojaSubrogacionService;
import com.imss.sivimss.hojasubrogacion.util.AppConstantes;
import com.imss.sivimss.hojasubrogacion.util.DatosRequest;
import com.imss.sivimss.hojasubrogacion.util.LogUtil;
import com.imss.sivimss.hojasubrogacion.util.MensajeResponseUtil;
import com.imss.sivimss.hojasubrogacion.util.ProviderServiceRestTemplate;
import com.imss.sivimss.hojasubrogacion.util.Response;

@Service
public class HojaSubrogacionServiceImpl  implements HojaSubrogacionService {
	
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
		FiltrosRequest filtros = json.fromJson(datosJson,FiltrosRequest.class);
		return providerServiceRestTemplate.consumirServicio( new ConsultaHojaSubrogacion().busquedaFiltros(filtros).getDatos()
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
		return providerServiceRestTemplate.consumirServicio( new ConsultaHojaSubrogacion().buscarServicios().getDatos()
				,urlDominio + AppConstantes.CATALOGO_CONSULTAR,authentication);
	}


}
