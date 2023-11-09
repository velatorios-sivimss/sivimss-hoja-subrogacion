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
        StringBuilder condicciones = new StringBuilder();
        
        if (reporteRequest.getIdVelatorio() != null) {
        	condicciones.append(" AND SOS.ID_VELATORIO  = ").append(reporteRequest.getIdVelatorio());
        }
        if (reporteRequest.getFolioOrdenServicio() != null) {
        	condicciones.append(" AND SOS.ID_ORDEN_SERVICIO = '").append(reporteRequest.getFolioOrdenServicio()).append("'");
        }
        if (reporteRequest.getIdProveedor() != null) {
        	condicciones.append(" AND PRO.ID_PROVEEDOR = ").append(reporteRequest.getIdProveedor());
        }
        if (reporteRequest.getFecha() != null) {
        	condicciones.append(" AND SOS.FEC_ALTA like '%").append(reporteRequest.getFecha() ).append("%'");
        }
        
        log.info("condicion::  " + condicciones);
        log.info("tipoRepirte::  " + reporteRequest.getTipoReporte());

        envioDatos.put("condicion", condicciones.toString());
        envioDatos.put("tipoReporte", reporteRequest.getTipoReporte());
        envioDatos.put("rutaNombreReporte", rutaNombreReporte);
		if(reporteRequest.getTipoReporte().equals("xls")) {
			envioDatos.put("IS_IGNORE_PAGINATION", true);
		}

        return envioDatos;
    }

    public DatosRequest buscarServicios(String idOrdenServicio) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("SS.REF_SERVICIO AS servicio", "SS.ID_SERVICIO  as idServicio","IFNULL(CPT.REF_ORIGEN,'') AS origen",
                        "IFNULL(CPT.REF_DESTINO,'') AS destino","IFNULL(CPT.CAN_TOTAL_KILOMETROS,'') AS totalKilometros")
                .from("SVC_ORDEN_SERVICIO SOS")
                .join("SVC_CARAC_PRESUPUESTO SCP", "SOS.ID_ORDEN_SERVICIO = SCP.ID_ORDEN_SERVICIO")
                .join("SVC_DETALLE_CARAC_PRESUP SDCP", "SCP.ID_CARAC_PRESUPUESTO = SDCP.ID_CARAC_PRESUPUESTO")
                .and("SDCP.ID_SERVICIO NOT IN (SELECT HS.ID_SERVICIO FROM SVT_HOJA_SUBROGACION HS " +
                        "WHERE HS.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO)").and("SDCP.IND_ACTIVO = 1")
                .join("SVT_SERVICIO SS", "SDCP.ID_SERVICIO = SS.ID_SERVICIO")
                .leftJoin("SVC_CARAC_PRESUP_TRASLADO CPT" ,"SDCP.ID_DETALLE_CARACTERISTICAS = CPT.ID_DETALLE_CARACTERISTICAS")
                .where("SOS.ID_ORDEN_SERVICIO = " + idOrdenServicio);
        String consulta = query.build();
        log.info(consulta);
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }

    public DatosRequest busquedaFiltros(FiltrosRequest request, String pagina, String tamanio) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        String velatorio = "";
        String folio = "";
        String proveedor = "";
        String fecha = "";
        if (!Objects.isNull(request.getIdVelatorio())) {
            velatorio = " and SOS.ID_VELATORIO =" + request.getIdVelatorio();
        }
        if(!Objects.isNull(request.getFolio())){
            folio = " and SOS.ID_ORDEN_SERVICIO = '" + request.getFolio() + "'";
        }
        if(!Objects.isNull(request.getIdProveedor())){
            proveedor = " and PRO.ID_PROVEEDOR = " + request.getIdProveedor();
        }
        if(!Objects.isNull(request.getFecha())){
            fecha = " and SOS.FEC_ALTA like '%" + request.getFecha() + "%'";
        }
        String consulta = "select\n" +
                "\tIFNULL(pablito.idHojaSubrogacion,'') AS idHojaSubrogacion,\n" +
                "\tIFNULL(pablito.tipoTranslado,'') AS tipoTranslado,\n" +
                "\tIFNULL(pablito.nombreOperador,'') AS nombreOperador,\n" +
                "\tIFNULL(pablito.nombreAcompaniante,'') AS nombreAcompaniante,\n" +
                "\tIFNULL(pablito.numCarroza,'') AS numCarroza,\n" +
                "\tIFNULL(pablito.numPlacas,'') AS numPlacas,\n" +
                "\tIFNULL(pablito.horaPartida,'') AS horaPartida,\n" +
                "\tIFNULL(pablito.diaPartida,'') AS diaPartida,\n" +
                "\tIFNULL(pablito.fechaOds,'') AS fechaOds,\n" +
                "\tIFNULL(pablito.folioOds,'') AS folioOds,\n" +
                "\tIFNULL(pablito.idOds,'') AS idOds,\n" +
                "\tIFNULL(pablito.proveedor,'') AS proveedor,\n" +
                "\tIFNULL(pablito.idProveedor,'') AS idProveedor,\n" +
                "\tIFNULL(pablito.idFinado,'') AS idFinado,\n" +
                "\tIFNULL(pablito.nombreFinado,'') AS nombreFinado,\n" +
                "\tIFNULL(pablito.origen,'') AS origen,\n" +
                "\tIFNULL(pablito.destino,'') AS destino,\n" +
                "\tIFNULL(pablito.totalKilometros,'') AS totalKilometros,\n" +
                "\tIFNULL(pablito.especificaciones,'') AS especificaciones,\n" +
                "\tIFNULL(pablito.idServicio,'') AS idServicio,\n" +
                "\tcase\n" +
                "\t\twhen (pablito.registrados - pablito.serviciosRegistrados) = 0 then 'false'\n" +
                "\t\telse 'true'\n" +
                "\tend as puedeRegistrar\n" +
                ",\n" +
                "\tserv.REF_SERVICIO as tipoServicio \n" +
                "from \n" +
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
                "\t\tSHS.REF_ESPECIFICACIONES as especificaciones,\n" +
                "\t\tSHS.ID_SERVICIO as idServicio,\n" +
                "\t\tserv.REF_SERVICIO as tipoServicio,\n" +
                "\t\t(DATE_FORMAT(SOS.FEC_ALTA , '%d-%m-%Y')) as fechaOds,\n" +
                "\t\tSOS.CVE_FOLIO as folioOds,\n" +
                "\t\tSOS.ID_ORDEN_SERVICIO as idOds,\n" +
                "\t\tdcp.ID_PROVEEDOR as idProveedor,\n" +
                "\t\tPRO.REF_PROVEEDOR as proveedor,\n" +
                "\t\tSF.ID_FINADO as idFinado,\n" +
                "\t\tCONCAT(SP.NOM_PERSONA, ' ', SP.NOM_PRIMER_APELLIDO, ' ', SP.NOM_SEGUNDO_APELLIDO) as nombreFinado,\n" +
                "\t\tcpt.REF_ORIGEN as origen,\n" +
                "\t\tcpt.REF_DESTINO as destino,\n" +
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
                "\t\tSOS.ID_ORDEN_SERVICIO = SHS.ID_ORDEN_SERVICIO\n" +
                " \tleft join SVC_FINADO SF on\n" +
                "\t\tSOS.ID_ORDEN_SERVICIO = SF.ID_ORDEN_SERVICIO \n" +
                "\tleft join SVC_PERSONA SP on\n" +
                "\t\tSF.ID_PERSONA = SP.ID_PERSONA\n" +
                "\tleft join SVC_CARAC_PRESUPUESTO scp on\n" +
                "\t\tSOS.ID_ORDEN_SERVICIO = scp.ID_ORDEN_SERVICIO\n" +
                "\tjoin SVC_DETALLE_CARAC_PRESUP dcp on\n" +
                 "\t\t dcp.ID_CARAC_PRESUPUESTO = scp.ID_CARAC_PRESUPUESTO\n" +
                "\t\tleft join SVT_PROVEEDOR PRO on\n" +
                "\t\tdcp.ID_PROVEEDOR = PRO.ID_PROVEEDOR\n" +
                "\tjoin SVT_SERVICIO serv on\n" +
                "\t\tdcp.ID_SERVICIO = serv.ID_SERVICIO\n" +
                "\tleft join SVC_CARAC_PRESUP_TRASLADO cpt on\n" +
                "\t\tdcp.ID_DETALLE_CARACTERISTICAS = cpt.ID_DETALLE_CARACTERISTICAS\n" +
                "\twhere\n" +
                "\t\tSOS.ID_ESTATUS_ORDEN_SERVICIO IN(4, 6)\n" +
                velatorio +
                folio +
                proveedor +
                fecha +
                " group by\n" +
                "\t\tscp.ID_CARAC_PRESUPUESTO ,\n" +
                "\t\tSHS.ID_HOJA_SUBROGACION ) as pablito join SVT_SERVICIO serv ON serv.ID_SERVICIO = pablito.idServicio ";

        log.info(consulta);
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        parametro.put("tamanio",tamanio);
        parametro.put("pagina",pagina);
        dr.setDatos(parametro);
        return dr;
    }
}
