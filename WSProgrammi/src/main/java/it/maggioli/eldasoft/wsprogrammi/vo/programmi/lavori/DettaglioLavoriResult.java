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
package it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Risultato Dettaglio programma lavori
 *
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = {
		"error",
		"programma"
})
@ApiModel(description="Contenitore per il risultato del dettaglio di un programma lavori")
public class DettaglioLavoriResult implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	public static final String ERROR_VALIDATION = "Il programma richiesto non è un programma di Lavori";
	public static final String ERROR_NOT_FOUND = "Identificativo programma non trovato nella banca dati";
	public static final String ERROR_UNAUTHORIZED = "Non si possiedono le credenziali per questo programma";
	public static final String ERROR_UNEXPECTED = "Errore inaspettato";

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="Eventuale messaggio di errore in caso di operazione fallita")
	private String error;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="Dettaglio del programma lavori")
	private PubblicaProgrammaLavoriEntry programma;

	public void setError(String error) {
		this.error = StringUtils.stripToNull(error);
	}

	public String getError() {
		return error;
	}

	public void setProgramma(PubblicaProgrammaLavoriEntry programma) {
		this.programma = programma;
	}

	public PubblicaProgrammaLavoriEntry getProgramma() {
		return programma;
	}

}
