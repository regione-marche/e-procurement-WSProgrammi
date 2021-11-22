/*
 * Created on 01/giu/2017
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wsprogrammi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Risultato 
 *
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = {
		"esito",
		"messaggio"
})
@ApiModel(description="Contenitore per il risultato di una operazione")
public class EsitoResult implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	/** Codice di errore in fase di controllo di validazione dei dati */
	public static final String ERROR_NO_PARAMETRI 			= "Indicare i valori richiesti (codiceLavoro e codiceCUI)";
	public static final String ERROR_INTERVENTO_NO_CUI      = "Non esiste un intervento/acquisto con il codice CUI indicato";
	public static final String ERROR_INTERVENTO_CUI_ASSOCIATO = "L'intervento/acquisto con il codice CUI indicato e' gia' associato ad un lavoro";
	/** Codice indicante un errore inaspettato. */
	public static final String ERROR_UNEXPECTED = "unexpected-error";

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="Risutato dell'operazione")
	private boolean esito;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="Eventuale messaggio di errore in caso di operazione fallita")
	private String messaggio;

	public void setEsito(boolean esito) {
		this.esito = esito;
	}

	public boolean isEsito() {
		return esito;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = StringUtils.stripToNull(messaggio);
	}

	public String getMessaggio() {
		return messaggio;
	}

}
