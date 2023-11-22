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
        String consulta = "  select   " +
                "                 IFNULL(temp.idHojaSubrogacion,'') AS idHojaSubrogacion,  " +
                "                    IFNULL(temp.tipoTranslado,'') AS tipoTranslado,  " +
                "                   IFNULL(temp.nombreOperador,'') AS nombreOperador,  " +
                "                  IFNULL(temp.nombreAcompaniante,'') AS nombreAcompaniante,  " +
                "                  IFNULL(temp.numCarroza,'') AS numCarroza,  " +
                "                   IFNULL(temp.numPlacas,'') AS numPlacas,  " +
                "                    IFNULL(temp.horaPartida,'') AS horaPartida,   " +
                "                     IFNULL(temp.diaPartida,'') AS diaPartida,  " +
                "                     IFNULL(temp.fechaOds,'') AS fechaOds,  " +
                "                    IFNULL(temp.folioOds,'') AS folioOds,  " +
                "                    IFNULL(temp.idOds,'') AS idOds,  " +
                "                     IFNULL(temp.proveedor,'') AS proveedor,  " +
                "                     IFNULL(temp.idProveedor,'') AS idProveedor,  " +
                "                     IFNULL(temp.idFinado,'') AS idFinado,  " +
                "                     IFNULL(temp.nombreFinado,'') AS nombreFinado,  " +
                "                      IFNULL(temp.origen,'') AS origen,  " +
                "                       IFNULL(temp.destino,'') AS destino,  " +
                "                      IFNULL(temp.totalKilometros,'') AS totalKilometros,  " +
                "                       IFNULL(temp.especificaciones,'') AS especificaciones,  " +
                "                       IFNULL(temp.idServicio,'') AS idServicio,  " +
                "                       case   " +
                "                         when (temp.registrados - temp.serviciosRegistrados) = 0 then 'false'  " +
                "                         else 'true'  " +
                "                     end as puedeRegistrar  " +
                "                 , (SELECT ts.DES_TIPO_SERVICIO from SVC_TIPO_SERVICIO ts  " +
                "       where  ts.ID_TIPO_SERVICIO= temp.ID_TIPO_SERVICIO ) as tipoServicio   " +
                "                   from   " +
                "                       (  " +
                "                    select  " +
                "                  serv.ID_TIPO_SERVICIO, " +
                "                      SHS.ID_HOJA_SUBROGACION as idHojaSubrogacion,  " +
                "                     SHS.TIP_TRASLADO as tipoTranslado,  " +
                "                     SHS.NOM_OPERADOR as nombreOperador,  " +
                "                     SHS.NOM_ACOMPANIANTE as nombreAcompaniante,  " +
                "                       SHS.REF_CARROZA_NUM as numCarroza,  " +
                "                        SHS.REF_NUMERO_PLACAS as numPlacas,  " +
                "                        SHS.TIM_HORA_PARTIDA as horaPartida,  " +
                "                         SHS.FEC_DIA_PARTIDA as diaPartida,  " +
                "                         SHS.REF_ESPECIFICACIONES as especificaciones,  " +
                "                           SHS.ID_SERVICIO as idServicio,  " +
                "                          serv.REF_SERVICIO as tipoServicio,  " +
                "                         (DATE_FORMAT(SOS.FEC_ALTA , '%d-%m-%Y')) as fechaOds,  " +
                "                    SOS.CVE_FOLIO as folioOds,  " +
                "                      SOS.ID_ORDEN_SERVICIO as idOds,  " +
                "                     dcp.ID_PROVEEDOR as idProveedor,  " +
                "                  PRO.REF_PROVEEDOR as proveedor,  " +
                "                    SF.ID_FINADO as idFinado,  " +
                "                    CONCAT(SP.NOM_PERSONA, ' ', SP.NOM_PRIMER_APELLIDO, ' ', SP.NOM_SEGUNDO_APELLIDO) as nombreFinado, "
                +
                "                  " +
                "                         cpt.REF_ORIGEN as origen,  " +
                "                      cpt.REF_DESTINO as destino,  " +
                "                     cpt.CAN_TOTAL_KILOMETROS as totalKilometros,  " +
                "                  (  " +
                "                  select  " +
                "                    count(shs.ID_ORDEN_SERVICIO)  " +
                "                 from  " +
                "                   SVT_HOJA_SUBROGACION shs  " +
                "                    where  " +
                "                   shs.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO) as registrados,  " +
                "                  (  " +
                "                 select  " +
                "                        count(ods.ID_ORDEN_SERVICIO)  " +
                "                     from SVC_ORDEN_SERVICIO ods  " +
                "                      join SVC_CARAC_PRESUPUESTO scp on  " +
                "                          ods.ID_ORDEN_SERVICIO = scp.ID_ORDEN_SERVICIO  " +
                "                    join SVC_DETALLE_CARAC_PRESUP sdcp on  " +
                "                           scp.ID_CARAC_PRESUPUESTO = sdcp.ID_CARAC_PRESUPUESTO  " +
                "                   join SVT_SERVICIO ss on  " +
                "                          sdcp.ID_SERVICIO = ss.ID_SERVICIO  " +
                "                    where  ods.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO  " +
                "                   group by   ods.ID_ORDEN_SERVICIO) as serviciosRegistrados  " +
                "                     " +
                "                 from   SVC_ORDEN_SERVICIO SOS  " +
                "                    left join SVC_FINADO SF on  " +
                "                   SF.ID_ORDEN_SERVICIO = SOS.ID_ORDEN_SERVICIO   " +
                "                    left join SVC_PERSONA SP on  " +
                "                   SP.ID_PERSONA = SF.ID_PERSONA   " +
                "                    left join SVC_CARAC_PRESUPUESTO scp on  " +
                "                  scp.ID_ORDEN_SERVICIO =  SOS.ID_ORDEN_SERVICIO   " +
                "                  left join SVC_DETALLE_CARAC_PRESUP dcp on  " +
                "                   dcp.ID_CARAC_PRESUPUESTO = scp.ID_CARAC_PRESUPUESTO  " +
                "                    left join SVT_PROVEEDOR PRO on  " +
                "                    PRO.ID_PROVEEDOR= dcp.ID_PROVEEDOR   " +
                "                     left join SVT_SERVICIO serv on  " +
                "                      serv.ID_SERVICIO = dcp.ID_SERVICIO   " +
                "                  " +
                "                      left join SVC_CARAC_PRESUP_TRASLADO cpt on  " +
                "                   cpt.ID_DETALLE_CARACTERISTICAS  = dcp.ID_DETALLE_CARACTERISTICAS   " +
                "                     left join SVT_HOJA_SUBROGACION SHS on  " +
                "                       SOS.ID_ORDEN_SERVICIO = SHS.ID_ORDEN_SERVICIO  " +
                "                  where  SOS.ID_ESTATUS_ORDEN_SERVICIO IN(4, 6)   " +
                velatorio +
                folio +
                proveedor +
                fecha +
                "                  GROUP BY SOS.ID_ORDEN_SERVICIO, SHS.ID_HOJA_SUBROGACION    " +
                "                 ) as temp   ";

        log.info(consulta);
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        parametro.put("tamanio", tamanio);
        parametro.put("pagina", pagina);
        dr.setDatos(parametro);
        return dr;
    }
}
