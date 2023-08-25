package com.imss.sivimss.hojasubrogacion.beans;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.hojasubrogacion.model.request.FolioRequest;
import com.imss.sivimss.hojasubrogacion.model.request.UsuarioDto;
import com.imss.sivimss.hojasubrogacion.util.AppConstantes;
import com.imss.sivimss.hojasubrogacion.util.DatosRequest;
import com.imss.sivimss.hojasubrogacion.util.SelectQueryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FolioOrdenServicio {
	
	public DatosRequest obtenerFolios(DatosRequest request, UsuarioDto usuarioDto, FolioRequest folioRequest ) {
		log.info(" INICIO - obtenerFolios");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("DISTINCT SOS.ID_ORDEN_SERVICIO AS idOrdenServicio", "SOS.CVE_FOLIO AS folioOrdenServicio")
		.from("SVC_ORDEN_SERVICIO SOS")
		.innerJoin("SVC_VELATORIO SV", "SOS.ID_VELATORIO = SV.ID_VELATORIO")
		.where("IFNULL(SOS.ID_ORDEN_SERVICIO,0) > 0")
		.and("SOS.ID_ESTATUS_ORDEN_SERVICIO IN (4,6)")
		.and("SOS.CVE_FOLIO LIKE'%"+folioRequest.getFolio()+"%'")
		.and("SOS.ID_VELATORIO = :idVelatorio").setParameter("idVelatorio", usuarioDto.getIdVelatorio())
		.orderBy("SOS.CVE_FOLIO ASC");
		final String query = queryUtil.build();
		log.info(" obtenerFolios: " + query);
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8)));
		log.info(" TERMINO - obtenerFolios");
		return request;
	}

}
