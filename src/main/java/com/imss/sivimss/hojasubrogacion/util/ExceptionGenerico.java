package com.imss.sivimss.hojasubrogacion.util;

import org.slf4j.Logger;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExceptionGenerico extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String code;
	private final Throwable cause;
	private final String mensaje;
	
    public ExceptionGenerico(String code) {
        super();
        this.code = code;
		this.cause = new Throwable();
		this.mensaje = "";
    }
    public ExceptionGenerico(String mensaje, Throwable cause, Logger log) {
		super(mensaje, cause);
        this.code = "";
		this.cause = cause;
		this.mensaje = mensaje;
        log.error(this.mensaje , this.cause.getCause().getMessage());
    }
    public ExceptionGenerico(String mensaje, String code) {
        super(mensaje);
        this.code = code;
		this.cause = new Throwable();
		this.mensaje = "";
    }

    public ExceptionGenerico( String code, Throwable cause) {
        super(cause.getMessage());
        this.code = code;
		this.cause = new Throwable();
		this.mensaje = "";
    }
}
