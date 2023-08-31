package com.imss.sivimss.hojasubrogacion.beans;


import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.hojasubrogacion.model.request.FiltrosRequest;
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
        queryUtil.select("DISTINCT DATE_FORMAT(SOS.FEC_ALTA,'%d/%m/%Y') AS fechaOrdenServicio", "IFNULL(SOS.CVE_FOLIO, '') AS folioOrdenServicio",
                        "IFNULL(SPO.NOM_PROVEEDOR, '') AS nombreProveedor", "CONCAT_WS(' ',SPE.NOM_PERSONA,SPE.NOM_PRIMER_APELLIDO,SPE.NOM_SEGUNDO_APELLIDO ) AS nomFinado")
                .from("SVT_HOJA_SUBROGACION SHS")
                .innerJoin("SVC_ORDEN_SERVICIO SOS", "SOS.ID_ORDEN_SERVICIO = SHS.ID_ORDEN_SERVICIO")
                .innerJoin("SVC_FINADO SFO", "SFO.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO")
                .innerJoin("SVC_PERSONA SPE", "SPE.ID_PERSONA = SFO.ID_PERSONA")
                .innerJoin("SVT_PROVEEDOR SPO", "SPO.ID_PROVEEDOR = SHS.ID_PROVEEDOR")
                .where("IFNULL(SHS.ID_HOJA_SUBROGACION ,0) > 0");
        if (reporteRequest.getIdVelatorio() != null) {
            queryUtil.and("SHS.ID_VELATORIO = :idVelatorio").setParameter("idVelatorio", reporteRequest.getIdVelatorio());
        }
        if (reporteRequest.getIdOrdenServicio() != null) {
            queryUtil.and("SHS.ID_ORDEN_SERVICIO = :idOrdenServicio").setParameter("idOrdenServicio", reporteRequest.getIdOrdenServicio());
        }
        if (reporteRequest.getIdProveedor() != null) {
            queryUtil.and("SHS.ID_PROVEEDOR = :idProveedor").setParameter("idProveedor", reporteRequest.getIdProveedor());
        }
        if (reporteRequest.getFechaInicio() != null && reporteRequest.getFechaFin() != null) {
            queryUtil.and("SHS.FEC_GENERACION_HOJA  BETWEEN '" + reporteRequest.getFechaInicio() + "' AND '" + reporteRequest.getFechaFin() + "'");
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

        if (reporteRequest.getIdVelatorio() != null) {
            condicciones.append(" AND SHS.ID_VELATORIO = ").append(reporteRequest.getIdVelatorio());
        }
        if (reporteRequest.getIdOrdenServicio() != null) {
            condicciones.append(" AND SHS.ID_ORDEN_SERVICIO = ").append(reporteRequest.getIdOrdenServicio());
        }
        if (reporteRequest.getIdProveedor() != null) {
            condicciones.append(" AND SHS.ID_PROVEEDOR = ").append(reporteRequest.getIdProveedor());
        }
        if (reporteRequest.getFechaInicio() != null && reporteRequest.getFechaFin() != null) {
            condicciones.append(" AND SHS.FEC_GENERACION_HOJA  BETWEEN '" + reporteRequest.getFechaInicio() + "' AND '" + reporteRequest.getFechaFin() + "'");
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

    public DatosRequest buscarServicios() {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("SS.REF_SERVICIO AS servicio", "ss.ID_SERVICIO  as idServicio")
                .from("SVT_HOJA_SUBROGACION SHS")
                .join("SVC_ORDEN_SERVICIO SOS", "SHS.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO")
                .join("SVC_CARAC_PRESUPUESTO SCP", "SOS.ID_ORDEN_SERVICIO = SCP.ID_ORDEN_SERVICIO")
                .join("SVC_DETALLE_CARAC_PRESUP SDCP", "SCP.ID_CARAC_PRESUPUESTO = SDCP.ID_CARAC_PRESUPUESTO")
                .and("SDCP.ID_SERVICIO NOT IN (SELECT HS.ID_SERVICIO FROM SVT_HOJA_SUBROGACION HS " +
                        "WHERE HS.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO)").and("SDCP.IND_ACTIVO = 1")
                .join("SVT_SERVICIO SS", "SDCP.ID_SERVICIO = SS.ID_SERVICIO")
                .where("SOS.ID_ORDEN_SERVICIO = 1");
        String consulta = query.build();
        log.info(consulta);
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }

    public DatosRequest busquedaFiltros(FiltrosRequest request) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("SHS.ID_HOJA_SUBROGACION AS idHojaSubrogacion", "(DATE_FORMAT(SOS.FEC_ALTA , '%d-%m-%Y')) AS fechaOds",
                        "SOS.CVE_FOLIO AS folioOds", "SOS.ID_ORDEN_SERVICIO AS idOds", "PRO.NOM_PROVEEDOR AS proveedor",
                        "CONCAT(SP.NOM_PERSONA, ' ', SP.NOM_PRIMER_APELLIDO, ' ', SP.NOM_SEGUNDO_APELLIDO) AS nombreFinado")
                .from("SVT_HOJA_SUBROGACION SHS")
                .leftJoin("SVC_FINADO SF", "SHS.ID_FINADO = SF.ID_FINADO")
                .leftJoin("SVC_PERSONA SP", "SF.ID_PERSONA = SP.ID_PERSONA")
                .leftJoin("SVC_ORDEN_SERVICIO SOS", "SHS.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO")
                .leftJoin("SVT_PROVEEDOR PRO", "SHS.ID_PROVEEDOR = PRO.ID_PROVEEDOR")
                .where("SHS.IND_ACTIVO = 1");
        if (!Objects.isNull(request.getIdVelatorio())) {
            query.and("SHS.ID_VELATORIO =" + request.getIdVelatorio());
        }
        if(!Objects.isNull(request.getFolio())){
            query.and("SOS.CVE_FOLIO = '" + request.getFolio() + "'");
        }
        if(!Objects.isNull(request.getIdProveedor())){
            query.and("PRO.ID_PROVEEDOR = " + request.getIdProveedor());
        }
        if(!Objects.isNull(request.getFecha())){
            query.and("SHS.FEC_GENERACION_HOJA  = '" + request.getFecha() + "'");
        }
        String consulta = query.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        parametro.put("tamanio","10");
        parametro.put("pagina","0");
        dr.setDatos(parametro);
        return dr;
    }
}