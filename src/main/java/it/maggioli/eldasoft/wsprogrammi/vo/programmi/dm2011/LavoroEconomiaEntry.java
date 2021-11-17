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
 * Dati di un lavoro in economia.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"descrizione",
		"cup", 
		"stimaLavori"
		 })
@ApiModel(description = "Contenitore per dati di un lavoro in ecomomia")
public class LavoroEconomiaEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@ApiModelProperty(hidden=true)
	private Long contri;
	
	@ApiModelProperty(hidden=true)
	private Long coneco;
	
	@ApiModelProperty(value = "Descrizione")
	@Size(max=2000)
	private String descrizione;
	
	@ApiModelProperty(value="Codice CUP")
	@Size(max=15)
	private String cup;

	@ApiModelProperty(value="Stima lavori")
	private Double stimaLavori;

	public void setDescrizione(String descrizione) {
		this.descrizione = StringUtils.stripToNull(descrizione);
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setCup(String cup) {
		this.cup = StringUtils.stripToNull(cup);
	}

	public String getCup() {
		return cup;
	}

	public void setStimaLavori(Double stimaLavori) {
		this.stimaLavori = stimaLavori;
	}

	public Double getStimaLavori() {
		return stimaLavori;
	}

	public void setContri(Long contri) {
		this.contri = contri;
	}

	@XmlTransient
	public Long getContri() {
		return contri;
	}

	public void setConeco(Long coneco) {
		this.coneco = coneco;
	}

	@XmlTransient
	public Long getConeco() {
		return coneco;
	}

}
