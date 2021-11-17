/*
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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

/**
 * Dati di un intervento acquisto .
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"anno",
		"annualitaRiferimento", 
		"cui", 
		"descrizione", 
		"codiceFiscaleRUP",
		"nomeRUP",
		"cognomeRUP",
		"cup",
		"cpv",
		"codiceLavoro"
	})
@ApiModel(description = "Contenitore per i dati di un intervento / acquisto")
public class InterventoAcquistoEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@ApiModelProperty(value="Anno iniziale del programma")
	private Long anno;
	
	@ApiModelProperty(value="Annualità di riferimento dell'intervento/acquisto nel programma [1,2,3]")
	private Long annualitaRiferimento;
	
	@ApiModelProperty(value = "Codice CUI")
	private String cui;
	
	@ApiModelProperty(value = "Descrizione dell'intervento/acquisto")
	private String descrizione;
	
	@ApiModelProperty(value = "Codice Fiscale del RUP")
	private String codiceFiscaleRUP;
	
	@ApiModelProperty(value = "Nome del RUP")
	private String nomeRUP;
	
	@ApiModelProperty(value = "Cognome del RUP ")
	private String cognomeRUP;
	
	@ApiModelProperty(value = "Codice Lavoro")
	private String codiceLavoro;

	@ApiModelProperty(value = "Codice CUP")
	private String cup;
	
	@ApiModelProperty(value = "Codice CPV")
	private String cpv;
	
	@ApiModelProperty(value = "Flag che indica se l'intervento / acquisto è già associato ad un lavoro", hidden=true)
	private String associato;
	
	public void setAnno(Long anno) {
		this.anno = anno;
	}

	public Long getAnno() {
		return anno;
	}

	public void setAnnualitaRiferimento(Long annualitaRiferimento) {
		this.annualitaRiferimento = annualitaRiferimento;
	}

	public Long getAnnualitaRiferimento() {
		return annualitaRiferimento;
	}

	public void setCui(String cui) {
		this.cui = StringUtils.stripToNull(cui);
	}

	public String getCui() {
		return cui;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = StringUtils.stripToNull(descrizione);
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setCodiceFiscaleRUP(String codiceFiscaleRUP) {
		this.codiceFiscaleRUP = StringUtils.stripToNull(codiceFiscaleRUP);
	}

	public String getCodiceFiscaleRUP() {
		return codiceFiscaleRUP;
	}

	public void setNomeRUP(String nomeRUP) {
		this.nomeRUP = StringUtils.stripToNull(nomeRUP);
	}

	public String getNomeRUP() {
		return nomeRUP;
	}

	public void setCognomeRUP(String cognomeRUP) {
		this.cognomeRUP = StringUtils.stripToNull(cognomeRUP);
	}

	public String getCognomeRUP() {
		return cognomeRUP;
	}

	public void setCodiceLavoro(String codiceLavoro) {
		this.codiceLavoro = StringUtils.stripToNull(codiceLavoro);
	}

	public String getCodiceLavoro() {
		return codiceLavoro;
	}

	public void setAssociato(String associato) {
		this.associato = StringUtils.stripToNull(associato);
	}
	
	@XmlTransient
	public String getAssociato() {
		return associato;
	}

	public void setCup(String cup) {
		this.cup = StringUtils.stripToNull(cup);
	}

	public String getCup() {
		return cup;
	}

	public void setCpv(String cpv) {
		this.cpv = StringUtils.stripToNull(cpv);
	}

	public String getCpv() {
		return cpv;
	}
	
}
