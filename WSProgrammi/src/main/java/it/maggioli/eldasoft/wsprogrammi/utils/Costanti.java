package it.maggioli.eldasoft.wsprogrammi.utils;

public class Costanti {

	public static Long tipoInstallazione = null;	//1 - Vigilanza, 2 - SA, 3 - OR
	
	public enum Operazione {
	    INSERIMENTO,
	    CANCELLAZIONE,
	    MODIFICA;
	}
	
	public static final int NUMERO_MAX_TENTATIVI_INSERT = 10;
	
	public static final String TIPO_ATTO_ESITO = "E";
	
	public static final String TIPO_GARA_DESERTA = "D";
	
	public static final String TIPO_ATTO_BANDO = "B";
	
	public static final String CONFIG_CODICE_APP = "W9";
	
	public static final String CONFIG_TIPO_INSTALL_WSREST = "it.eldasoft.wspubblicazioni.tipoInstallazione";
	
}
