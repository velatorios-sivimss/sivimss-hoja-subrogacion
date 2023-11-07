package com.imss.sivimss.hojasubrogacion.beans;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.hojasubrogacion.model.request.FolioRequest;
import com.imss.sivimss.hojasubrogacion.util.AppConstantes;
import com.imss.sivimss.hojasubrogacion.util.DatosRequest;
import com.imss.sivimss.hojasubrogacion.util.SelectQueryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FolioOrdenServicio {
	
	public DatosRequest obtenerFolios(DatosRequest request, FolioRequest folioRequest ) {
		log.info(" INICIO - obtenerFolios");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("DISTINCT SOS.ID_ORDEN_SERVICIO AS idOrdenServicio", "SOS.CVE_FOLIO AS folioOrdenServicio")
		.from("SVC_ORDEN_SERVICIO SOS")
		.innerJoin("SVC_VELATORIO SV", "SOS.ID_VELATORIO = SV.ID_VELATORIO")
		.where("IFNULL(SOS.ID_ORDEN_SERVICIO,0) > 0")
		.and("SOS.ID_ESTATUS_ORDEN_SERVICIO IN (4,6)")
		.and("SOS.CVE_FOLIO LIKE'%"+folioRequest.getFolioOrdenServicio()+"%'")
		.and("SOS.ID_VELATORIO = :idVelatorio").setParameter("idVelatorio", folioRequest.getIdVelatorio())
		.orderBy("SOS.CVE_FOLIO ASC");
		final String query = queryUtil.build();
		log.info(" obtenerFolios: " + query);
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8)));
		log.info(" TERMINO - obtenerFolios");
		return request;
	}
	
	public DatosRequest obtenerProveedores(DatosRequest request, FolioRequest folioRequest ) {
		log.info(" INICIO - obtenerProveedores");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SP.ID_PROVEEDOR AS folioProveedor", "SP.REF_PROVEEDOR AS nombreProveedor")
		.from("SVT_CONTRATO SC")
		.innerJoin("SVT_PROVEEDOR SP", "SP.ID_PROVEEDOR = SC.ID_PROVEEDOR").and("SC.ID_TIPO_ASIGNACION = 2")
		.innerJoin("SVC_VELATORIO SV", "SV.ID_VELATORIO = SC.ID_VELATORIO")
		.where("IFNULL(SC.ID_CONTRATO,0) > 0")
		.and("SP.REF_PROVEEDOR LIKE'%"+folioRequest.getNombreProveedor()+"%'")
		.and("SC.ID_VELATORIO = :idVelatorio").setParameter("idVelatorio", folioRequest.getIdVelatorio())
		.orderBy("SP.ID_PROVEEDOR ASC");
		final String query = queryUtil.build();
		log.info(" obtenerProveedores: " + query);
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8)));
		log.info(" TERMINO - obtenerProveedores");
		return request;
	}

}
