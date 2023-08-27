package com.imss.sivimss.hojasubrogacion.beans;


import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.hojasubrogacion.model.request.ReporteRequest;
import com.imss.sivimss.hojasubrogacion.util.AppConstantes;
import com.imss.sivimss.hojasubrogacion.util.DatosRequest;
import com.imss.sivimss.hojasubrogacion.util.SelectQueryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsultaHojaSubrogacion {
	
	public DatosRequest consultaHojaSubrogacion(DatosRequest request, ReporteRequest reporteRequest) {
		log.info(" INICIO - consultaHojaSubrogacion");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("DISTINCT DATE_FORMAT(SOS.FEC_ALTA,'%d/%m/%Y') AS fechaOrdenServicio","IFNULL(SOS.CVE_FOLIO, '') AS folioOrdenServicio",
		"IFNULL(SPO.NOM_PROVEEDOR, '') AS nombreProveedor","CONCAT_WS(' ',SPE.NOM_PERSONA,SPE.NOM_PRIMER_APELLIDO,SPE.NOM_SEGUNDO_APELLIDO ) AS nomFinado")
		.from("SVT_HOJA_SUBROGACION SHS")
		.innerJoin("SVC_ORDEN_SERVICIO SOS", "SOS.ID_ORDEN_SERVICIO = SHS.ID_FOLIOODS")
		.innerJoin("SVC_FINADO SFO","SFO.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO")
		.innerJoin("SVC_PERSONA SPE","SPE.ID_PERSONA = SFO.ID_PERSONA")
		.innerJoin("SVT_PROVEEDOR SPO", "SPO.ID_PROVEEDOR = SHS.ID_PROVEEDOR")
		.where("IFNULL(SHS.ID_HOJA_SUBROGACION ,0) > 0");
		if(reporteRequest.getIdVelatorio() != null) {
			queryUtil.and("SHS.ID_VELATORIO = :idVelatorio").setParameter("idVelatorio", reporteRequest.getIdVelatorio());
		}
		if(reporteRequest.getIdOrdenServicio() != null) {
			queryUtil.and("SHS.ID_FOLIOODS = :idOrdenServicio").setParameter("idOrdenServicio", reporteRequest.getIdOrdenServicio());
		}
		if(reporteRequest.getIdProveedor() != null) {
			queryUtil.and("SHS.ID_PROVEEDOR = :idProveedor").setParameter("idProveedor", reporteRequest.getIdProveedor());
		}
		if(reporteRequest.getFechaInicio() != null && reporteRequest.getFechaFin() != null) {
			queryUtil.and("SHS.FEC_GENERACION_HOJA  BETWEEN '"+reporteRequest.getFechaInicio()+"' AND '"+reporteRequest.getFechaFin()+"'");
		}
		queryUtil.orderBy("SHS.ID_HOJA_SUBROGACION ASC");
		final String query = queryUtil.build();
		log.info(" consultaHojaSubrogacion: " + query);
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8)));
		log.info(" TERMINO - consultaHojaSubrogacion");
		return request;
	}
	
	public String consultaHojaSubrogacion(ReporteRequest reporteRequest) {
		StringBuilder condicciones = new StringBuilder();
		
		if(reporteRequest.getIdVelatorio() != null) {
			condicciones.append(" AND SHS.ID_VELATORIO = ").append(reporteRequest.getIdVelatorio());
		}
		if(reporteRequest.getIdOrdenServicio() != null) {
			condicciones.append(" AND SHS.ID_FOLIOODS = ").append(reporteRequest.getIdOrdenServicio());
		}
		if(reporteRequest.getIdProveedor() != null) {
			condicciones.append(" AND SHS.ID_PROVEEDOR = ").append(reporteRequest.getIdProveedor());
		}
		if(reporteRequest.getFechaInicio() != null && reporteRequest.getFechaFin() != null) {
			condicciones.append(" AND SHS.FEC_GENERACION_HOJA  BETWEEN '"+reporteRequest.getFechaInicio()+"' AND '"+reporteRequest.getFechaFin()+"'");
		}
		return condicciones.toString();
	}
	
	public Map<String, Object> generarReporteConsultaHojaSubrogacion(ReporteRequest reporteRequest, String rutaNombreReporte) {
		Map<String, Object> envioDatos = new HashMap<>();
		String condicion = consultaHojaSubrogacion(reporteRequest);
		
		log.info("condicion::  " + condicion);
		log.info("tipoRepirte::  " + reporteRequest.getTipoReporte());
		
		envioDatos.put("condicion", condicion);
		envioDatos.put("tipoReporte", reporteRequest.getTipoReporte());
		envioDatos.put("rutaNombreReporte", rutaNombreReporte);

		return envioDatos;
	}
}