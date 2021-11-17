/*
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

/**
 * Dati di un immobile.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"descrizione",
		"tipoProprieta", 
		"valoreStimato"
		 })
@ApiModel(description = "Contenitore per dati di un immobile")
public class ImmobileDM112011Entry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@ApiModelProperty(hidden=true)
	private Long contri;
	
	@ApiModelProperty(hidden=true)
	private Long conint;
	
	@ApiModelProperty(hidden=true)
	private Long numimm;
	
	@ApiModelProperty(value = "Descrizione dell'immobile")
	@Size(max=400)
	private String descrizione;
	
	@ApiModelProperty(value="Tipo proprietà (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=TipoProprieta)")
	private Long tipoProprieta;

	@ApiModelProperty(value="Valore stimato dell'immobile")
	private Double valoreStimato;

	public void setDescrizione(String descrizione) {
		this.descrizione = StringUtils.stripToNull(descrizione);
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setTipoProprieta(Long tipoProprieta) {
		this.tipoProprieta = tipoProprieta;
	}

	public Long getTipoProprieta() {
		return tipoProprieta;
	}

	public void setValoreStimato(Double valoreStimato) {
		this.valoreStimato = valoreStimato;
	}

	public Double getValoreStimato() {
		return valoreStimato;
	}

	public void setContri(Long contri) {
		this.contri = contri;
	}

	@XmlTransient
	public Long getContri() {
		return contri;
	}

	public void setConint(Long conint) {
		this.conint = conint;
	}

	@XmlTransient
	public Long getConint() {
		return conint;
	}

	public void setNumimm(Long numimm) {
		this.numimm = numimm;
	}

	@XmlTransient
	public Long getNumimm() {
		return numimm;
	}

}
