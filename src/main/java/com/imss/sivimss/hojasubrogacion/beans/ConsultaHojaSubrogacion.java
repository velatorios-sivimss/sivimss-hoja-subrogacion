package com.imss.sivimss.hojasubrogacion.beans;

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

    public Map<String, Object> generarReporteConsultaHojaSubrogacion(ReporteRequest reporteRequest, String rutaNombreReporte) {
        Map<String, Object> envioDatos = new HashMap<>();
        StringBuilder condicciones1 = new StringBuilder();
        StringBuilder condicciones2 = new StringBuilder();
        
        if (reporteRequest.getIdVelatorio() != null) {
        	condicciones1.append(" AND SHS.ID_VELATORIO = ").append(reporteRequest.getIdVelatorio());
        }
        if (reporteRequest.getFolioOrdenServicio() != null) {
        	condicciones1.append(" AND SOS.CVE_FOLIO = ").append(reporteRequest.getFolioOrdenServicio());
        }
        if (reporteRequest.getIdProveedor() != null) {
        	condicciones1.append(" AND PRO.ID_PROVEEDOR = ").append(reporteRequest.getIdProveedor());
        }
        if (reporteRequest.getFecha() != null) {
        	condicciones2.append(" AND SHS.FEC_GENERACION_HOJA  = '" + reporteRequest.getFecha() + "'");
        }
        
        log.info("condicion::  " + condicciones1);
        log.info("condicion::  " + condicciones2);
        log.info("tipoRepirte::  " + reporteRequest.getTipoReporte());

        envioDatos.put("condicion", condicciones1.toString());
        envioDatos.put("condicion1", condicciones2.toString());
        envioDatos.put("tipoReporte", reporteRequest.getTipoReporte());
        envioDatos.put("rutaNombreReporte", rutaNombreReporte);

        return envioDatos;
    }

    public DatosRequest buscarServicios(String idOrdenServicio) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("SS.REF_SERVICIO AS servicio", "SS.ID_SERVICIO  as idServicio")
                .from("SVT_HOJA_SUBROGACION SHS")
                .join("SVC_ORDEN_SERVICIO SOS", "SHS.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO")
                .join("SVC_CARAC_PRESUPUESTO SCP", "SOS.ID_ORDEN_SERVICIO = SCP.ID_ORDEN_SERVICIO")
                .join("SVC_DETALLE_CARAC_PRESUP SDCP", "SCP.ID_CARAC_PRESUPUESTO = SDCP.ID_CARAC_PRESUPUESTO")
                .and("SDCP.ID_SERVICIO NOT IN (SELECT HS.ID_SERVICIO FROM SVT_HOJA_SUBROGACION HS " +
                        "WHERE HS.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO)").and("SDCP.IND_ACTIVO = 1")
                .join("SVT_SERVICIO SS", "SDCP.ID_SERVICIO = SS.ID_SERVICIO")
                .where("SOS.ID_ORDEN_SERVICIO = " + idOrdenServicio);
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
        String velatorio = "";
        String folio = "";
        String proveedor = "";
        String fecha = "";
        if (!Objects.isNull(request.getIdVelatorio())) {
            velatorio = " and SOS.ID_VELATORIO =" + request.getIdVelatorio();
        }
        if(!Objects.isNull(request.getFolio())){
            folio = " and SOS.CVE_FOLIO = '" + request.getFolio() + "'";
        }
        if(!Objects.isNull(request.getIdProveedor())){
            proveedor = " and PRO.ID_PROVEEDOR = " + request.getIdProveedor();
        }
        if(!Objects.isNull(request.getFecha())){
            fecha = " and SHS.FEC_GENERACION_HOJA  = '" + request.getFecha() + "'";
        }
       String consulta = "select pablito.idHojaSubrogacion,pablito.fechaOds,pablito.folioOds,pablito.idOds,pablito.proveedor,pablito.nombreFinado,pablito.origen,\n" +
               "pablito.destino,pablito.totalKilometros,\n" +
               "case when (pablito.registrados - pablito.serviciosRegistrados) = 0 then 'false' \n" +
               "else 'true' end as puedeRegistrar\n" +
               ",pablito.tipoServicio\n" +
               "from\n" +
               "(\n" +
               "select SHS.ID_HOJA_SUBROGACION as idHojaSubrogacion,\n" +
               "serv.REF_SERVICIO as tipoServicio,\n" +
               "\t(DATE_FORMAT(SOS.FEC_ALTA , '%d-%m-%Y')) as fechaOds,\n" +
               "\tSOS.CVE_FOLIO as folioOds,\n" +
               "\tSOS.ID_ORDEN_SERVICIO as idOds,\n" +
               "\tPRO.NOM_PROVEEDOR as proveedor,\n" +
               "\tCONCAT(SP.NOM_PERSONA, ' ', SP.NOM_PRIMER_APELLIDO, ' ', SP.NOM_SEGUNDO_APELLIDO) as nombreFinado,\n" +
               "\tcpt.DES_ORIGEN as origen,\n" +
               "\tcpt.DES_DESTINO as destino,\n" +
               "\tcpt.CAN_TOTAL_KILOMETROS as totalKilometros,\n" +
               "\t(select count(shs.ID_ORDEN_SERVICIO)  from SVT_HOJA_SUBROGACION shs \n" +
               "where shs.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO) as registrados,\n" +
               "\t(select count(ods.ID_ORDEN_SERVICIO)  from SVC_ORDEN_SERVICIO ods \n" +
               "join SVC_CARAC_PRESUPUESTO scp on ods.ID_ORDEN_SERVICIO = scp.ID_ORDEN_SERVICIO \n" +
               "join SVC_DETALLE_CARAC_PRESUP sdcp on scp.ID_CARAC_PRESUPUESTO  = sdcp.ID_CARAC_PRESUPUESTO \n" +
               "join SVT_SERVICIO ss on sdcp.ID_SERVICIO = ss.ID_SERVICIO \n" +
               "where ods.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO \n" +
               "group by ods.ID_ORDEN_SERVICIO) as serviciosRegistrados\n" +
               "from\n" +
               "\t SVC_ORDEN_SERVICIO SOS\n" +
               "left join SVT_HOJA_SUBROGACION SHS on\n" +
               "\tSOS.ID_ORDEN_SERVICIO = SHS.ID_ORDEN_SERVICIO " +  fecha +
               "left join SVC_FINADO SF on\n" +
               "\tSHS.ID_FINADO = SF.ID_FINADO\n" +
               "\tleft join SVC_PERSONA SP on\n" +
               "\tSF.ID_PERSONA = SP.ID_PERSONA\n" +
               "left join SVT_PROVEEDOR PRO on\n" +
               "\tSHS.ID_PROVEEDOR = PRO.ID_PROVEEDOR\n" +
               "left join SVC_CARAC_PRESUPUESTO scp on\n" +
               "\tSOS.ID_ORDEN_SERVICIO = scp.ID_ORDEN_SERVICIO\n" +
               " join SVC_DETALLE_CARAC_PRESUP dcp on\n" +
               "\tscp.ID_CARAC_PRESUPUESTO = scp.ID_CARAC_PRESUPUESTO\n" +
               "\tjoin SVT_SERVICIO serv on dcp.ID_SERVICIO = serv.ID_SERVICIO \n" +
               "left join SVC_CARAC_PRESUP_TRASLADO cpt on\n" +
               "\tdcp.ID_DETALLE_CARACTERISTICAS = cpt.ID_DETALLE_CARACTERISTICAS\n" +
               "where\n" +
               "SOS.ID_ESTATUS_ORDEN_SERVICIO IN(4,6)\n" +
                       velatorio +
                       folio +
                       proveedor +
               " \tgroup by scp.ID_CARAC_PRESUPUESTO , SHS.ID_HOJA_SUBROGACION ) as pablito";
        log.info(consulta);
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        parametro.put("tamanio","10");
        parametro.put("pagina","0");
        dr.setDatos(parametro);
        return dr;
    }
}