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
package it.maggioli.eldasoft.wsprogrammi.vo.lfs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Elenco degli interventi acquisti
 *
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = {
		"interventiAcquisti",
		"error"
})
@ApiModel(description="Contenitore per l'elenco degli interventi acquisti")
public class InterventiAcquistiResult implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	public static final String ERROR_TIPO_PROGRAMMA = "Il parametro tipoProgramma accetta i valori 1 - Programma Lavori, 2 - Programma Forniture/Servizi";
	public static final String ERROR_NO_PARAMETRI = "Indicare almeno un parametro di ricerca tra quelli previsti (tipoProgramma, codiceCUI, descrizione)";
	/** Codice indicante un errore inaspettato. */
	public static final String ERROR_UNEXPECTED = "unexpected-error";

	@ApiModelProperty(value="Elenco Interventi/Acquisti")
	private List<InterventoAcquistoEntry> interventiAcquisti = new ArrayList<InterventoAcquistoEntry>();


	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="Eventuale messaggio di errore in caso di operazione fallita")
	private String error;


	public void setError(String error) {
		this.error = StringUtils.stripToNull(error);
	}

	public String getError() {
		return error;
	}

	public void setInterventiAcquisti(List<InterventoAcquistoEntry> interventiAcquisti) {
		this.interventiAcquisti = interventiAcquisti;
	}

	public List<InterventoAcquistoEntry> getInterventiAcquisti() {
		return interventiAcquisti;
	}

}
