package com.imss.sivimss.hojasubrogacion.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imss.sivimss.hojasubrogacion.model.request.HojaSubrogacionRequest;
import com.imss.sivimss.hojasubrogacion.util.QueryHelper;


public class HojaSubrogacion {


	private static final Logger log = LoggerFactory.getLogger(HojaSubrogacion.class);
	

	public String queryInsertHojaSubrogacion(HojaSubrogacionRequest hojaSubrogacionResponse, Integer idUsuarioAlta) {
		QueryHelper q = new QueryHelper("INSERT INTO SVT_HOJA_SUBROGACION");
		q = setValores(q, hojaSubrogacionResponse);
		q.agregarParametroValues("ID_USUARIO_ALTA", "" + idUsuarioAlta);
		q.agregarParametroValues("FEC_ALTA", "CURRENT_TIMESTAMP()");
		String query = q.obtenerQueryInsertar();
		log.info(query);
		return query;
	}
	public String queryUpdateHojaSubrogacion(HojaSubrogacionRequest hojaSubrogacionResponse, Integer idUsuarioAlta) {
		QueryHelper q = new QueryHelper("UPDATE SVT_HOJA_SUBROGACION");
		q = setValores(q, hojaSubrogacionResponse);
		q.agregarParametroValues("ID_USUARIO_MODIFICA", "" + idUsuarioAlta);
		q.agregarParametroValues("FEC_ACTUALIZACION", "CURRENT_TIMESTAMP()");
		q.addWhere("ID_HOJA_SUBROGACION = " + hojaSubrogacionResponse.getIdHojaSubrogacion());
		String query = q.obtenerQueryActualizar();
		log.info(query);
		return query;
	}
	private QueryHelper setValores (QueryHelper q, HojaSubrogacionRequest hojaSubrogacionResponse) {
		if(hojaSubrogacionResponse.getIdOrdenServicio() != null)
			q.agregarParametroValues("ID_ORDEN_SERVICIO", "" + hojaSubrogacionResponse.getIdOrdenServicio());
		if(hojaSubrogacionResponse.getIdDelegacion() != null)
			q.agregarParametroValues("ID_DELEGACION", "" + hojaSubrogacionResponse.getIdDelegacion()); 
		if(hojaSubrogacionResponse.getIdVelatorio() != null)
			q.agregarParametroValues("ID_VELATORIO", "" + hojaSubrogacionResponse.getIdVelatorio());
		if(hojaSubrogacionResponse.getIdProveedor() != null)
			q.agregarParametroValues("ID_PROVEEDOR", "" + hojaSubrogacionResponse.getIdProveedor()); 
		if(hojaSubrogacionResponse.getFecGeneracionHoja() != null)
			q.agregarParametroValues("FEC_GENERACION_HOJA", "'" + hojaSubrogacionResponse.getFecGeneracionHoja() + "'");
		if(hojaSubrogacionResponse.getTipoTraslado() != null)
			q.agregarParametroValues("TIP_TRASLADO", "'" + hojaSubrogacionResponse.getTipoTraslado() + "'") ;
		if(hojaSubrogacionResponse.getIdFinado() != null)
			q.agregarParametroValues("ID_FINADO", "" + hojaSubrogacionResponse.getIdFinado()) ;
		if(hojaSubrogacionResponse.getOrigen() != null)
			q.agregarParametroValues("REF_ORIGEN", "'" +  hojaSubrogacionResponse.getOrigen() + "'");
		if( hojaSubrogacionResponse.getDestino() != null)
			q.agregarParametroValues("REF_DESTINO", "'" +  hojaSubrogacionResponse.getDestino() + "'"); 
		if(hojaSubrogacionResponse.getDistanciaRecorrer() != null)
			q.agregarParametroValues("REF_DISTANCIA_RECORRER", "'" +  hojaSubrogacionResponse.getDistanciaRecorrer() + "'");
		if(hojaSubrogacionResponse.getIdServicio() != null)
			q.agregarParametroValues("ID_SERVICIO", "" + hojaSubrogacionResponse.getIdServicio()); 
		if(hojaSubrogacionResponse.getEspecificaciones() != null)
			q.agregarParametroValues("REF_ESPECIFICACIONES", "'" +  hojaSubrogacionResponse.getEspecificaciones() + "'"); 
		if(hojaSubrogacionResponse.getNombreOperador() != null)
			q.agregarParametroValues("NOM_OPERADOR", "'" +  hojaSubrogacionResponse.getNombreOperador() + "'");
		if(hojaSubrogacionResponse.getCarrozaNum() != null)
			q.agregarParametroValues("REF_CARROZA_NUM", "'" +  hojaSubrogacionResponse.getCarrozaNum() + "'");
		if(hojaSubrogacionResponse.getNumeroPlacas() != null)
			q.agregarParametroValues("REF_NUMERO_PLACAS", "'" +  hojaSubrogacionResponse.getNumeroPlacas() + "'");
		if(hojaSubrogacionResponse.getDiaPartida() !=null)
			q.agregarParametroValues("FEC_DIA_PARTIDA", "'" +  hojaSubrogacionResponse.getDiaPartida() + "'");
		if(hojaSubrogacionResponse.getHoraPartida() != null)
			q.agregarParametroValues("TIM_HORA_PARTIDA", "'" +  hojaSubrogacionResponse.getHoraPartida() + "'");
		if(hojaSubrogacionResponse.getNomAcompaniante() != null)
			q.agregarParametroValues("NOM_ACOMPANIANTE", "'" +  hojaSubrogacionResponse.getNomAcompaniante() + "'"); 
		return q;
	}
}
