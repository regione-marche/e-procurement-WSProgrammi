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
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

/**
 * Dettaglio di un intervento / acquisto.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"cui",
		"cup",
		"descrizione",
		"annoProgramma",
		"annualita",
		"importoTotale",
		"rup", 
		"priorita", 
		"istat", 
		"nuts", 
		"risorseVincolatePerLegge1",
		"risorseVincolatePerLegge2",
		"risorseVincolatePerLegge3",
		"risorseVincolatePerLeggeSucc",
		"risorseMutuo1",
		"risorseMutuo2",
		"risorseMutuo3",
		"risorseMutuoSucc",
		"risorsePrivati1",
		"risorsePrivati2",
		"risorsePrivati3",
		"risorsePrivatiSucc",
		"risorseBilancio1",
		"risorseBilancio2",
		"risorseBilancio3",
		"risorseBilancioSucc",
		"risorseArt3_1",
		"risorseArt3_2",
		"risorseArt3_3",
		"risorseArt3_Succ",
		"risorseImmobili1",
		"risorseImmobili2",
		"risorseImmobili3",
		"risorseImmobiliSucc",
		"risorseAltro1",
		"risorseAltro2",
		"risorseAltro3",
		"risorseAltroSucc"
		 })
@ApiModel(description = "Contenitore per i dati di un intervento / acquisto")
public class DettaglioInterventoAcquistoEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@ApiModelProperty(value = "Codice CUI")
	private String cui;
	
	@ApiModelProperty(value = "Codice CUP di progetto (assegnato da CIPE)")
	private String cup;
	
	@ApiModelProperty(value = "Descrizione")
	private String descrizione;
	
	@ApiModelProperty(value = "Anno del programma")
	private Long annoProgramma;
	
	@ApiModelProperty(value = "Annualità di riferimento dell'intervento / acquisto")
	private Long annualita;
	
	@ApiModelProperty(value="Importo totale")
	private Double importoTotale;
	
	@ApiModelProperty(value = "Responsabile unico RUP")
	private String rup;

	@ApiModelProperty(value="Localizzazione - Codice ISTAT del Comune")
	private String istat;

	@ApiModelProperty(value="Localizzazione - NUTS")
	private String nuts;
	
	@ApiModelProperty(value="Livello di Priorità (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Priorita)")
	private Long priorita;
	
	@ApiModelProperty(value="Risorse derivanti da entrate aventi destinazione vincolata per legge - Primo anno")
	private Double risorseVincolatePerLegge1;
	
	@ApiModelProperty(value="Risorse derivanti da entrate aventi destinazione vincolata per legge - Secondo anno")
	private Double risorseVincolatePerLegge2;
	
	@ApiModelProperty(value="Risorse derivanti da entrate aventi destinazione vincolata per legge - Terzo anno")
	private Double risorseVincolatePerLegge3;
	
	@ApiModelProperty(value="Risorse derivanti da entrate aventi destinazione vincolata per legge - Annualità successive")
	private Double risorseVincolatePerLeggeSucc;
	
	@ApiModelProperty(value="Risorse derivanti da entrate acquisite mediante contrazione di mutuo - Primo anno")
	private Double risorseMutuo1;
	
	@ApiModelProperty(value="Risorse derivanti da entrate acquisite mediante contrazione di mutuo - Secondo anno")
	private Double risorseMutuo2;
	
	@ApiModelProperty(value="Risorse derivanti da entrate acquisite mediante contrazione di mutuo - Terzo anno")
	private Double risorseMutuo3;
	
	@ApiModelProperty(value="Risorse derivanti da entrate acquisite mediante contrazione di mutuo - Annualità successive")
	private Double risorseMutuoSucc;
	
	@ApiModelProperty(value="Risorse acquisite mediante apporti di capitale privato - Primo anno")
	private Double risorsePrivati1;
	
	@ApiModelProperty(value="Risorse acquisite mediante apporti di capitale privato - Secondo anno")
	private Double risorsePrivati2;
	
	@ApiModelProperty(value="Risorse acquisite mediante apporti di capitale privato - Terzo anno")
	private Double risorsePrivati3;
	
	@ApiModelProperty(value="Risorse acquisite mediante apporti di capitale privato - Annualità successive")
	private Double risorsePrivatiSucc;
	
	@ApiModelProperty(value="Stanziamenti di bilancio - Primo anno")
	private Double risorseBilancio1;
	
	@ApiModelProperty(value="Stanziamenti di bilancio - Secondo anno")
	private Double risorseBilancio2;
	
	@ApiModelProperty(value="Stanziamenti di bilancio - Terzo anno")
	private Double risorseBilancio3;
	
	@ApiModelProperty(value="Stanziamenti di bilancio - Annualità successive")
	private Double risorseBilancioSucc;
	
	@ApiModelProperty(value="Finanziamenti art. 3 DL 310/1990 - Primo anno")
	private Double risorseArt3_1;
	
	@ApiModelProperty(value="Finanziamenti art. 3 DL 310/1990 - Secondo anno")
	private Double risorseArt3_2;
	
	@ApiModelProperty(value="Finanziamenti art. 3 DL 310/1990 - Terzo anno")
	private Double risorseArt3_3;
	
	@ApiModelProperty(value="Finanziamenti art. 3 DL 310/1990 - Annualità successive")
	private Double risorseArt3_Succ;
	
	@ApiModelProperty(value="Risorse derivanti da trasferimento immobili - Primo anno")
	private Double risorseImmobili1;
	
	@ApiModelProperty(value="Risorse derivanti da trasferimento immobili - Secondo anno")
	private Double risorseImmobili2;
	
	@ApiModelProperty(value="Risorse derivanti da trasferimento immobili - Terzo anno")
	private Double risorseImmobili3;
	
	@ApiModelProperty(value="Risorse derivanti da trasferimento immobili - Annualità successive")
	private Double risorseImmobiliSucc;
	
	@ApiModelProperty(value="Altra tipologia - Primo anno")
	private Double risorseAltro1;
	
	@ApiModelProperty(value="Altra tipologia - Secondo anno")
	private Double risorseAltro2;
	
	@ApiModelProperty(value="Altra tipologia - Terzo anno")
	private Double risorseAltro3;
	
	@ApiModelProperty(value="Altra tipologia - Annualità successive")
	private Double risorseAltroSucc;

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

	public void setCup(String cup) {
		this.cup = StringUtils.stripToNull(cup);
	}

	public String getCup() {
		return cup;
	}

	public void setIstat(String istat) {
		this.istat = StringUtils.stripToNull(istat);
	}

	public String getIstat() {
		return istat;
	}

	public void setNuts(String nuts) {
		this.nuts = StringUtils.stripToNull(nuts);
	}

	public String getNuts() {
		return nuts;
	}

	public void setPriorita(Long priorita) {
		this.priorita = priorita;
	}

	public Long getPriorita() {
		return priorita;
	}

	public void setRisorseVincolatePerLegge1(Double risorseVincolatePerLegge1) {
		this.risorseVincolatePerLegge1 = risorseVincolatePerLegge1;
	}

	public Double getRisorseVincolatePerLegge1() {
		return risorseVincolatePerLegge1;
	}

	public void setRisorseVincolatePerLegge2(Double risorseVincolatePerLegge2) {
		this.risorseVincolatePerLegge2 = risorseVincolatePerLegge2;
	}

	public Double getRisorseVincolatePerLegge2() {
		return risorseVincolatePerLegge2;
	}

	public void setRisorseVincolatePerLegge3(Double risorseVincolatePerLegge3) {
		this.risorseVincolatePerLegge3 = risorseVincolatePerLegge3;
	}

	public Double getRisorseVincolatePerLegge3() {
		return risorseVincolatePerLegge3;
	}

	public void setRisorseVincolatePerLeggeSucc(
			Double risorseVincolatePerLeggeSucc) {
		this.risorseVincolatePerLeggeSucc = risorseVincolatePerLeggeSucc;
	}

	public Double getRisorseVincolatePerLeggeSucc() {
		return risorseVincolatePerLeggeSucc;
	}

	public void setRisorseMutuo1(Double risorseMutuo1) {
		this.risorseMutuo1 = risorseMutuo1;
	}

	public Double getRisorseMutuo1() {
		return risorseMutuo1;
	}

	public void setRisorseMutuo2(Double risorseMutuo2) {
		this.risorseMutuo2 = risorseMutuo2;
	}

	public Double getRisorseMutuo2() {
		return risorseMutuo2;
	}

	public void setRisorseMutuo3(Double risorseMutuo3) {
		this.risorseMutuo3 = risorseMutuo3;
	}

	public Double getRisorseMutuo3() {
		return risorseMutuo3;
	}

	public void setRisorseMutuoSucc(Double risorseMutuoSucc) {
		this.risorseMutuoSucc = risorseMutuoSucc;
	}

	public Double getRisorseMutuoSucc() {
		return risorseMutuoSucc;
	}

	public void setRisorsePrivati1(Double risorsePrivati1) {
		this.risorsePrivati1 = risorsePrivati1;
	}

	public Double getRisorsePrivati1() {
		return risorsePrivati1;
	}

	public void setRisorsePrivati2(Double risorsePrivati2) {
		this.risorsePrivati2 = risorsePrivati2;
	}

	public Double getRisorsePrivati2() {
		return risorsePrivati2;
	}

	public void setRisorsePrivati3(Double risorsePrivati3) {
		this.risorsePrivati3 = risorsePrivati3;
	}

	public Double getRisorsePrivati3() {
		return risorsePrivati3;
	}

	public void setRisorsePrivatiSucc(Double risorsePrivatiSucc) {
		this.risorsePrivatiSucc = risorsePrivatiSucc;
	}

	public Double getRisorsePrivatiSucc() {
		return risorsePrivatiSucc;
	}

	public void setRisorseBilancio1(Double risorseBilancio1) {
		this.risorseBilancio1 = risorseBilancio1;
	}

	public Double getRisorseBilancio1() {
		return risorseBilancio1;
	}

	public void setRisorseBilancio2(Double risorseBilancio2) {
		this.risorseBilancio2 = risorseBilancio2;
	}

	public Double getRisorseBilancio2() {
		return risorseBilancio2;
	}

	public void setRisorseBilancio3(Double risorseBilancio3) {
		this.risorseBilancio3 = risorseBilancio3;
	}

	public Double getRisorseBilancio3() {
		return risorseBilancio3;
	}

	public void setRisorseBilancioSucc(Double risorseBilancioSucc) {
		this.risorseBilancioSucc = risorseBilancioSucc;
	}

	public Double getRisorseBilancioSucc() {
		return risorseBilancioSucc;
	}

	public void setRisorseArt3_1(Double risorseArt3_1) {
		this.risorseArt3_1 = risorseArt3_1;
	}

	public Double getRisorseArt3_1() {
		return risorseArt3_1;
	}

	public void setRisorseArt3_2(Double risorseArt3_2) {
		this.risorseArt3_2 = risorseArt3_2;
	}

	public Double getRisorseArt3_2() {
		return risorseArt3_2;
	}

	public void setRisorseArt3_3(Double risorseArt3_3) {
		this.risorseArt3_3 = risorseArt3_3;
	}

	public Double getRisorseArt3_3() {
		return risorseArt3_3;
	}

	public void setRisorseArt3_Succ(Double risorseArt3_Succ) {
		this.risorseArt3_Succ = risorseArt3_Succ;
	}

	public Double getRisorseArt3_Succ() {
		return risorseArt3_Succ;
	}

	public void setRisorseImmobili1(Double risorseImmobili1) {
		this.risorseImmobili1 = risorseImmobili1;
	}

	public Double getRisorseImmobili1() {
		return risorseImmobili1;
	}

	public void setRisorseImmobili2(Double risorseImmobili2) {
		this.risorseImmobili2 = risorseImmobili2;
	}

	public Double getRisorseImmobili2() {
		return risorseImmobili2;
	}

	public void setRisorseImmobili3(Double risorseImmobili3) {
		this.risorseImmobili3 = risorseImmobili3;
	}

	public Double getRisorseImmobili3() {
		return risorseImmobili3;
	}

	public void setRisorseImmobiliSucc(Double risorseImmobiliSucc) {
		this.risorseImmobiliSucc = risorseImmobiliSucc;
	}

	public Double getRisorseImmobiliSucc() {
		return risorseImmobiliSucc;
	}

	public void setRisorseAltro1(Double risorseAltro1) {
		this.risorseAltro1 = risorseAltro1;
	}

	public Double getRisorseAltro1() {
		return risorseAltro1;
	}

	public void setRisorseAltro2(Double risorseAltro2) {
		this.risorseAltro2 = risorseAltro2;
	}

	public Double getRisorseAltro2() {
		return risorseAltro2;
	}

	public void setRisorseAltro3(Double risorseAltro3) {
		this.risorseAltro3 = risorseAltro3;
	}

	public Double getRisorseAltro3() {
		return risorseAltro3;
	}

	public void setRisorseAltroSucc(Double risorseAltroSucc) {
		this.risorseAltroSucc = risorseAltroSucc;
	}

	public Double getRisorseAltroSucc() {
		return risorseAltroSucc;
	}

	public void setImportoTotale(Double importoTotale) {
		this.importoTotale = importoTotale;
	}

	public Double getImportoTotale() {
		return importoTotale;
	}

	public void setRup(String rup) {
		this.rup = StringUtils.stripToNull(rup);
	}

	public String getRup() {
		return rup;
	}

	public void setAnnoProgramma(Long annoProgramma) {
		this.annoProgramma = annoProgramma;
	}

	public Long getAnnoProgramma() {
		return annoProgramma;
	}

	public void setAnnualita(Long annualita) {
		this.annualita = annualita;
	}

	public Long getAnnualita() {
		return annualita;
	}

}
