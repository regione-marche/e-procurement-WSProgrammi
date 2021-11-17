/*
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

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

/**
 * Dati di un intervento non riproposto.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"cui",
		"cup", 
		"descrizione", 
		"importo", 
		"priorita",
		"motivo" 
		
		 })
@ApiModel(description = "Contenitore per i dati di un intervento non riproposto")
public class InterventoNonRipropostoEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@ApiModelProperty(hidden=true)
	private Long contri;
	
	@ApiModelProperty(hidden=true)
	private Long conint;
	
	@ApiModelProperty(value = "Codice CUI", required=true)
	@Size(max=25)
	private String cui;
	
	@ApiModelProperty(value = "Codice CUP di progetto (assegnato da CIPE)")
	@Size(max=15)
	private String cup;
	
	@ApiModelProperty(value = "Descrizione dell'intervento", required=true)
	@Size(max=2000)
	private String descrizione;
	
	@ApiModelProperty(value="Importo complessivo", required=true)
	private Double importo;
	
	@ApiModelProperty(value="Livello di Priorit� (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Priorita)", required=true)
	private Long priorita;
	
	@ApiModelProperty(value = "Motivo per il quale l'intervento non � riproposto", required=true)
	@Size(max=2000)
	private String motivo;

	public void setCui(String cui) {
		this.cui = StringUtils.stripToNull(cui);
	}

	public String getCui() {
		return cui;
	}

	public void setCup(String cup) {
		this.cup = StringUtils.stripToNull(cup);
	}

	public String getCup() {
		return cup;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = StringUtils.stripToNull(descrizione);
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setImporto(Double importo) {
		this.importo = importo;
	}

	public Double getImporto() {
		return importo;
	}

	public void setPriorita(Long priorita) {
		this.priorita = priorita;
	}

	public Long getPriorita() {
		return priorita;
	}

	public void setMotivo(String motivo) {
		this.motivo = StringUtils.stripToNull(motivo);
	}

	public String getMotivo() {
		return motivo;
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

	
}
