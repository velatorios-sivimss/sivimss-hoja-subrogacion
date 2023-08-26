package com.imss.sivimss.hojasubrogacion.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

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

}
