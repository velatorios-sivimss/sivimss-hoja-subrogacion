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
        String consulta = "select\n" +
                "\tpablito.idHojaSubrogacion,\n" +
                "\tpablito.tipoTranslado,\n" +
                "\tpablito.nombreOperador,\n" +
                "\tpablito.nombreAcompaniante,\n" +
                "\tpablito.numCarroza,\n" +
                "\tpablito.numPlacas,\n" +
                "\tpablito.horaPartida,\n" +
                "\tpablito.diaPartida,\n" +
                "\tpablito.fechaOds,\n" +
                "\tpablito.folioOds,\n" +
                "\tpablito.idOds,\n" +
                "\tpablito.proveedor,\n" +
                "\tpablito.nombreFinado,\n" +
                "\tpablito.origen,\n" +
                "\tpablito.destino,\n" +
                "\tpablito.totalKilometros,\n" +
                "\tcase\n" +
                "\t\twhen (pablito.registrados - pablito.serviciosRegistrados) = 0 then 'false'\n" +
                "\t\telse 'true'\n" +
                "\tend as puedeRegistrar\n" +
                ",\n" +
                "\tpablito.tipoServicio\n" +
                "from\n" +
                "\t(\n" +
                "\tselect\n" +
                "\t\tSHS.ID_HOJA_SUBROGACION as idHojaSubrogacion,\n" +
                "\t\tSHS.TIP_TRASLADO as tipoTranslado,\n" +
                "\t\tSHS.NOM_OPERADOR as nombreOperador,\n" +
                "\t\tSHS.NOM_ACOMPANIANTE as nombreAcompaniante,\n" +
                "\t\tSHS.REF_CARROZA_NUM as numCarroza,\n" +
                "\t\tSHS.REF_NUMERO_PLACAS as numPlacas,\n" +
                "\t\tSHS.TIM_HORA_PARTIDA as horaPartida,\n" +
                "\t\tSHS.FEC_DIA_PARTIDA as diaPartida,\n" +
                "\t\tserv.REF_SERVICIO as tipoServicio,\n" +
                "\t\t(DATE_FORMAT(SOS.FEC_ALTA , '%d-%m-%Y')) as fechaOds,\n" +
                "\t\tSOS.CVE_FOLIO as folioOds,\n" +
                "\t\tSOS.ID_ORDEN_SERVICIO as idOds,\n" +
                "\t\tPRO.NOM_PROVEEDOR as proveedor,\n" +
                "\t\tCONCAT(SP.NOM_PERSONA, ' ', SP.NOM_PRIMER_APELLIDO, ' ', SP.NOM_SEGUNDO_APELLIDO) as nombreFinado,\n" +
                "\t\tcpt.DES_ORIGEN as origen,\n" +
                "\t\tcpt.DES_DESTINO as destino,\n" +
                "\t\tcpt.CAN_TOTAL_KILOMETROS as totalKilometros,\n" +
                "\t\t(\n" +
                "\t\tselect\n" +
                "\t\t\tcount(shs.ID_ORDEN_SERVICIO)\n" +
                "\t\tfrom\n" +
                "\t\t\tSVT_HOJA_SUBROGACION shs\n" +
                "\t\twhere\n" +
                "\t\t\tshs.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO) as registrados,\n" +
                "\t\t(\n" +
                "\t\tselect\n" +
                "\t\t\tcount(ods.ID_ORDEN_SERVICIO)\n" +
                "\t\tfrom\n" +
                "\t\t\tSVC_ORDEN_SERVICIO ods\n" +
                "\t\tjoin SVC_CARAC_PRESUPUESTO scp on\n" +
                "\t\t\tods.ID_ORDEN_SERVICIO = scp.ID_ORDEN_SERVICIO\n" +
                "\t\tjoin SVC_DETALLE_CARAC_PRESUP sdcp on\n" +
                "\t\t\tscp.ID_CARAC_PRESUPUESTO = sdcp.ID_CARAC_PRESUPUESTO\n" +
                "\t\tjoin SVT_SERVICIO ss on\n" +
                "\t\t\tsdcp.ID_SERVICIO = ss.ID_SERVICIO\n" +
                "\t\twhere\n" +
                "\t\t\tods.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO\n" +
                "\t\tgroup by\n" +
                "\t\t\tods.ID_ORDEN_SERVICIO) as serviciosRegistrados\n" +
                "\tfrom\n" +
                "\t\tSVC_ORDEN_SERVICIO SOS\n" +
                "\tleft join SVT_HOJA_SUBROGACION SHS on\n" +
                "\t\tSOS.ID_ORDEN_SERVICIO = SHS.ID_ORDEN_SERVICIO\n" +   fecha +
                "\t\tand SHS.FEC_GENERACION_HOJA = '2023-08-25'\n" +
                "\tleft join SVC_FINADO SF on\n" +
                "\t\tSHS.ID_FINADO = SF.ID_FINADO\n" +
                "\tleft join SVC_PERSONA SP on\n" +
                "\t\tSF.ID_PERSONA = SP.ID_PERSONA\n" +
                "\tleft join SVT_PROVEEDOR PRO on\n" +
                "\t\tSHS.ID_PROVEEDOR = PRO.ID_PROVEEDOR\n" +
                "\tleft join SVC_CARAC_PRESUPUESTO scp on\n" +
                "\t\tSOS.ID_ORDEN_SERVICIO = scp.ID_ORDEN_SERVICIO\n" +
                "\tjoin SVC_DETALLE_CARAC_PRESUP dcp on\n" +
                "\t\tscp.ID_CARAC_PRESUPUESTO = scp.ID_CARAC_PRESUPUESTO\n" +
                "\tjoin SVT_SERVICIO serv on\n" +
                "\t\tdcp.ID_SERVICIO = serv.ID_SERVICIO\n" +
                "\tleft join SVC_CARAC_PRESUP_TRASLADO cpt on\n" +
                "\t\tdcp.ID_DETALLE_CARACTERISTICAS = cpt.ID_DETALLE_CARACTERISTICAS\n" +
                "\twhere\n" +
                "\t\tSOS.ID_ESTATUS_ORDEN_SERVICIO IN(4, 6)\n" +
                velatorio +
                folio +
                proveedor +
                "\tgroup by\n" +
                "\t\tscp.ID_CARAC_PRESUPUESTO ,\n" +
                "\t\tSHS.ID_HOJA_SUBROGACION ) as pablito";

        log.info(consulta);
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        parametro.put("tamanio","10");
        parametro.put("pagina","0");
        dr.setDatos(parametro);
        return dr;
    }
}