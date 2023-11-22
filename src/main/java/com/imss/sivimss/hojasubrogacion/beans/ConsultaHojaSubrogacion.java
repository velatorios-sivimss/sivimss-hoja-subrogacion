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

    public Map<String, Object> generarReporteConsultaHojaSubrogacion(ReporteRequest reporteRequest,
            String rutaNombreReporte) {
        Map<String, Object> envioDatos = new HashMap<>();
        StringBuilder condicciones = new StringBuilder();

        if (reporteRequest.getIdVelatorio() != null) {
            condicciones.append(" AND SOS.ID_VELATORIO  = ").append(reporteRequest.getIdVelatorio());
        }
        if (reporteRequest.getFolioOrdenServicio() != null) {
            condicciones.append(" AND SOS.ID_ORDEN_SERVICIO = '").append(reporteRequest.getFolioOrdenServicio())
                    .append("'");
        }
        if (reporteRequest.getIdProveedor() != null) {
            condicciones.append(" AND PRO.ID_PROVEEDOR = ").append(reporteRequest.getIdProveedor());
        }
        if (reporteRequest.getFecha() != null) {
            condicciones.append(" AND SOS.FEC_ALTA like '%").append(reporteRequest.getFecha()).append("%'");
        }

        log.info("condicion::  " + condicciones);
        log.info("tipoRepirte::  " + reporteRequest.getTipoReporte());

        envioDatos.put("condicion", condicciones.toString());
        envioDatos.put("tipoReporte", reporteRequest.getTipoReporte());
        envioDatos.put("rutaNombreReporte", rutaNombreReporte);
        if (reporteRequest.getTipoReporte().equals("xls")) {
            envioDatos.put("IS_IGNORE_PAGINATION", true);
        }

        return envioDatos;
    }

    public DatosRequest buscarServicios(String idOrdenServicio) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("SS.REF_SERVICIO AS servicio", "SS.ID_SERVICIO  as idServicio",
                "SP.REF_PROVEEDOR AS nombreProveedor",
                "SP.ID_PROVEEDOR AS idProveedor",
                "IFNULL(CPT.REF_ORIGEN,'') AS origen",
                "IFNULL(CPT.REF_DESTINO,'') AS destino", "IFNULL(CPT.CAN_TOTAL_KILOMETROS,'') AS totalKilometros")
                .from("SVC_ORDEN_SERVICIO SOS")
                .join("SVC_CARAC_PRESUPUESTO SCP", "SOS.ID_ORDEN_SERVICIO = SCP.ID_ORDEN_SERVICIO")
                .join("SVC_DETALLE_CARAC_PRESUP SDCP", "SCP.ID_CARAC_PRESUPUESTO = SDCP.ID_CARAC_PRESUPUESTO")
                .and("SDCP.ID_SERVICIO NOT IN (SELECT HS.ID_SERVICIO FROM SVT_HOJA_SUBROGACION HS " +
                        "WHERE HS.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO)")
                .and("SDCP.IND_ACTIVO = 1")
                .join("SVT_SERVICIO SS", "SDCP.ID_SERVICIO = SS.ID_SERVICIO")
                .join("SVT_CONTRATO_SERVICIO SCS", "SS.ID_SERVICIO = SCS.ID_SERVICIO")
                .join("SVT_CONTRATO SC", "SCS.ID_CONTRATO = SC.ID_CONTRATO AND SDCP.ID_PROVEEDOR = SC.ID_PROVEEDOR")
                .join("SVT_PROVEEDOR SP", "SC.ID_PROVEEDOR = SP.ID_PROVEEDOR")
                .leftJoin("SVC_CARAC_PRESUP_TRASLADO CPT",
                        "SDCP.ID_DETALLE_CARACTERISTICAS = CPT.ID_DETALLE_CARACTERISTICAS")
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
        if (!Objects.isNull(request.getFolio())) {
            folio = " and SOS.ID_ORDEN_SERVICIO = '" + request.getFolio() + "'";
        }
        if (!Objects.isNull(request.getIdProveedor())) {
            proveedor = " and PRO.ID_PROVEEDOR = " + request.getIdProveedor();
        }
        if (!Objects.isNull(request.getFecha())) {
            fecha = " and SOS.FEC_ALTA like '%" + request.getFecha() + "%'";
        }
        String consulta = "  SELECT IFNULL(temp.fechaOds,'') AS fechaOds,  " +
                " IFNULL(temp.idOds,'') AS idOds,   " +
                " IFNULL(temp.folioOds,'') AS folioOds,   " +
                " IFNULL(temp.proveedor,'') AS proveedor,   " +
                " IFNULL(temp.nombreFinado,'') AS nombreFinado,  " +
                " temp.tipoServicio,  " +
                " temp.idHojaSubrogacion,   " +
                " IFNULL(temp.idProveedor,'') AS idProveedor,  " +
                "  IFNULL(temp.idFinado,'') AS idFinado,   " +
                "  CASE WHEN (temp.registrados - temp.serviciosRegistrados) = 0 THEN 'false'   " +
                "  ELSE 'true' END AS puedeRegistrar  " +
                " FROM (  " +
                " SELECT   " +
                "  serv.ID_TIPO_SERVICIO,   " +
                " '' AS tipoServicio,   " +
                " (DATE_FORMAT(SOS.FEC_ALTA, '%d-%m-%Y')) AS fechaOds,   " +
                " SOS.CVE_FOLIO AS folioOds,   " +
                " SOS.ID_ORDEN_SERVICIO AS idOds,   " +
                " '' AS idProveedor,   " +
                " '' AS proveedor,   " +
                " SF.ID_FINADO AS idFinado, CONCAT(SP.NOM_PERSONA, ' ', SP.NOM_PRIMER_APELLIDO, ' ',  " +
                " SP.NOM_SEGUNDO_APELLIDO) AS nombreFinado,  " +
                " '' AS idHojaSubrogacion,  " +
                " (  " +
                " SELECT COUNT(shs.ID_ORDEN_SERVICIO)  " +
                " FROM   " +
                " SVT_HOJA_SUBROGACION shs  " +
                " WHERE   " +
                " shs.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO) AS registrados,  " +
                " (  " +
                " SELECT COUNT(ods.ID_ORDEN_SERVICIO)  " +
                " FROM SVC_ORDEN_SERVICIO ods  " +
                " JOIN SVC_CARAC_PRESUPUESTO scp ON   " +
                " ods.ID_ORDEN_SERVICIO = scp.ID_ORDEN_SERVICIO  " +
                " JOIN SVC_DETALLE_CARAC_PRESUP sdcp ON   " +
                " scp.ID_CARAC_PRESUPUESTO = sdcp.ID_CARAC_PRESUPUESTO  " +
                " JOIN SVT_SERVICIO ss ON   " +
                " sdcp.ID_SERVICIO = ss.ID_SERVICIO  " +
                " WHERE ods.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO  " +
                " GROUP BY ods.ID_ORDEN_SERVICIO) AS serviciosRegistrados  " +
                " FROM SVC_ORDEN_SERVICIO SOS  " +
                " LEFT JOIN SVC_FINADO SF ON   " +
                " SF.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO  " +
                " LEFT JOIN SVC_PERSONA SP ON   " +
                " SP.ID_PERSONA = SF.ID_PERSONA  " +
                " LEFT JOIN SVC_CARAC_PRESUPUESTO scp ON   " +
                " scp.ID_ORDEN_SERVICIO =  SOS.ID_ORDEN_SERVICIO  " +
                " LEFT JOIN SVC_DETALLE_CARAC_PRESUP dcp ON   " +
                " dcp.ID_CARAC_PRESUPUESTO = scp.ID_CARAC_PRESUPUESTO  " +
                " LEFT JOIN SVT_PROVEEDOR PRO ON   " +
                " PRO.ID_PROVEEDOR= dcp.ID_PROVEEDOR  " +
                " LEFT JOIN SVT_SERVICIO serv ON   " +
                " serv.ID_SERVICIO = dcp.ID_SERVICIO  " +
                " LEFT JOIN SVC_TIPO_SERVICIO ts ON   " +
                "   ts.ID_TIPO_SERVICIO= serv.ID_TIPO_SERVICIO  " +
                " LEFT JOIN SVC_CARAC_PRESUP_TRASLADO cpt ON   " +
                " cpt.ID_DETALLE_CARACTERISTICAS = dcp.ID_DETALLE_CARACTERISTICAS  " +
                " WHERE SOS.ID_ESTATUS_ORDEN_SERVICIO IN(4, 6)  " +
                velatorio +
                folio +
                proveedor +
                fecha +
                " GROUP BY SOS.ID_ORDEN_SERVICIO" +
                " UNION ALL  " +
                " SELECT   " +
                " serv.ID_TIPO_SERVICIO,   " +
                "  (  " +
                " SELECT ts.DES_TIPO_SERVICIO  " +
                " FROM SVC_TIPO_SERVICIO ts  " +
                " WHERE ts.ID_TIPO_SERVICIO= serv.ID_TIPO_SERVICIO) AS tipoServicio,  " +
                " (DATE_FORMAT(SOS.FEC_ALTA, '%d-%m-%Y')) AS fechaOds,   " +
                " SOS.CVE_FOLIO AS folioOds,   " +
                " SOS.ID_ORDEN_SERVICIO AS idOds,   " +
                " SHS.ID_PROVEEDOR AS idProveedor,   " +
                " PRO.REF_PROVEEDOR AS proveedor,   " +
                " SF.ID_FINADO AS idFinado, CONCAT(SP.NOM_PERSONA, ' ', SP.NOM_PRIMER_APELLIDO, ' ',  " +
                " SP.NOM_SEGUNDO_APELLIDO) AS nombreFinado,  " +
                " SHS.ID_HOJA_SUBROGACION,  " +
                " (  " +
                " SELECT COUNT(shs.ID_ORDEN_SERVICIO)  " +
                " FROM   " +
                " SVT_HOJA_SUBROGACION shs  " +
                " WHERE   " +
                " shs.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO) AS registrados,  " +
                " (  " +
                " SELECT COUNT(ods.ID_ORDEN_SERVICIO)  " +
                " FROM SVC_ORDEN_SERVICIO ods  " +
                " JOIN SVC_CARAC_PRESUPUESTO scp ON   " +
                " ods.ID_ORDEN_SERVICIO = scp.ID_ORDEN_SERVICIO  " +
                " JOIN SVC_DETALLE_CARAC_PRESUP sdcp ON   " +
                " scp.ID_CARAC_PRESUPUESTO = sdcp.ID_CARAC_PRESUPUESTO  " +
                " JOIN SVT_SERVICIO ss ON   " +
                " sdcp.ID_SERVICIO = ss.ID_SERVICIO  " +
                " WHERE ods.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO  " +
                " GROUP BY ods.ID_ORDEN_SERVICIO) AS serviciosRegistrados  " +
                " FROM SVC_ORDEN_SERVICIO SOS  " +
                " LEFT JOIN SVC_FINADO SF ON   " +
                " SF.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO  " +
                " JOIN SVT_HOJA_SUBROGACION SHS ON   " +
                " SOS.ID_ORDEN_SERVICIO = SHS.ID_ORDEN_SERVICIO  " +
                " LEFT JOIN SVC_PERSONA SP ON   " +
                " SP.ID_PERSONA = SF.ID_PERSONA  " +
                " JOIN SVT_PROVEEDOR PRO ON   " +
                " PRO.ID_PROVEEDOR= SHS.ID_PROVEEDOR  " +
                " JOIN SVT_SERVICIO serv ON   " +
                " serv.ID_SERVICIO = SHS.ID_SERVICIO  " +
                " JOIN SVC_TIPO_SERVICIO ts ON   " +
                "   ts.ID_TIPO_SERVICIO= serv.ID_TIPO_SERVICIO  " +
                " WHERE SOS.ID_ESTATUS_ORDEN_SERVICIO IN(4, 6)   " +
                velatorio +
                folio +
                proveedor +
                fecha +
                "   " +
                " ) temp  " +
                "  " +
                " ORDER BY temp.idOds, temp.idHojaSubrogacion   ";

        log.info(consulta);
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        parametro.put("tamanio", tamanio);
        parametro.put("pagina", pagina);
        dr.setDatos(parametro);
        return dr;
    }

    public DatosRequest buscarDetalle(String idHojaSubrogacion) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        String consulta = "SELECT SHS.ID_HOJA_SUBROGACION AS idHojaSubrogacion,    " +
                " SHS.ID_ORDEN_SERVICIO AS idOds,    " +
                "  SHS.ID_PROVEEDOR AS idProveedor,    " +
                " SHS.TIP_TRASLADO AS tipoTraslado,    " +
                " SHS.REF_ORIGEN AS origen,    " +
                "  SHS.REF_DESTINO AS destino,    " +
                "   SHS.REF_DISTANCIA_RECORRER AS distancia,    " +
                " SHS.ID_SERVICIO AS idServicio,    " +
                " SHS.REF_ESPECIFICACIONES AS especificaciones,    " +
                "  SHS.NOM_OPERADOR AS operador,     " +
                "  SHS.REF_CARROZA_NUM AS carroza,    " +
                " SHS.REF_NUMERO_PLACAS AS placas,     " +
                " SHS.FEC_DIA_PARTIDA AS diaPartida,     " +
                " SHS.TIM_HORA_PARTIDA AS horaPArtida,    " +
                " SHS.NOM_ACOMPANIANTE AS acompaniante ,  " +
                "  p.REF_PROVEEDOR AS proveedor ," +
                "  ts.DES_TIPO_SERVICIO AS servicio" +
                " FROM SVC_ORDEN_SERVICIO SOS     " +
                " JOIN SVT_HOJA_SUBROGACION SHS ON     " +
                " SOS.ID_ORDEN_SERVICIO = SHS.ID_ORDEN_SERVICIO    " +
                " JOIN SVT_PROVEEDOR p ON p.ID_PROVEEDOR = SHS.ID_PROVEEDOR" +
                " JOIN SVT_SERVICIO serv ON " +
                "  serv.ID_SERVICIO = SHS.ID_SERVICIO" +
                " JOIN SVC_TIPO_SERVICIO ts ON " +
                "  ts.ID_TIPO_SERVICIO= serv.ID_TIPO_SERVICIO" +
                " WHERE SHS.ID_HOJA_SUBROGACION= " + idHojaSubrogacion;
        log.info(consulta);
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }
}
