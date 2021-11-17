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
import it.maggioli.eldasoft.wsprogrammi.vo.tecnici.DatiGeneraliTecnicoEntry;

import java.io.Serializable;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

/**
 * Dati di un intervento forniture e servizi.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"codiceInterno",
		"descrizione",
		"istat", 
		"nuts", 
		"esenteCup",
		"cup", 
		"cpv",
		"settore",
		"priorita", 
		"normativaRiferimento",
		"strumentoProgrammazione",
		"previstaManodopera",
		"risorseMutuo", 
		"risorsePrivati",
		"risorseBilancio",
		"risorseAltro",
		"importoRisorseFinanziarie",
		"importoRisorseFinanziarieRegionali",
		"meseAvvioProcedura",
		"rup"
		 })
@ApiModel(description = "Contenitore per i dati di un intervento per forniture e servizi per Programma DM 11/2011")
public class FornitureServiziDM112011Entry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@ApiModelProperty(hidden=true)
	private Long contri;
	
	@ApiModelProperty(hidden=true)
	private Long conint;
	
	@ApiModelProperty(hidden=true)
	private String codiceRup;
	
	@ApiModelProperty(value = "Codice interno attribuito dall'amministrazione")
	@Size(max=20)
	private String codiceInterno;
	
	@ApiModelProperty(value = "Descrizione intervento", required = true)
	@Size(max=2000)
	private String descrizione;
	
	@ApiModelProperty(value="Localizzazione - Codice ISTAT del Comune")
	@Size(max=9)
	private String istat;

	@ApiModelProperty(value="Localizzazione - NUTS")
	@Size(max=20)
	private String nuts;
	
	@ApiModelProperty(value="Esente dalla richiesta CUP? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String esenteCup;
	
	@ApiModelProperty(value = "Codice CUP di progetto (assegnato da CIPE)")
	@Size(max=15)
	private String cup;
	
	@ApiModelProperty(value = "Codice CPV")
	@Size(max=20)
	private String cpv;
	
	@ApiModelProperty(value="Settore F=Forniture, S=Servizi", required = true, allowableValues="F,S")
	@Size(max=1)
	private String settore;
	
	@ApiModelProperty(value="Livello di Priorità (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Priorita)")
	private Long priorita;
	
	@ApiModelProperty(value="Normativa di riferimento")
	@Size(max=200)
	private String normativaRiferimento;
	
	@ApiModelProperty(value="Eventuale strumento di programma")
	@Size(max=100)
	private String strumentoProgrammazione;
	
	@ApiModelProperty(value="E' previsto impiego di manodopera/posa in opera? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String previstaManodopera;
	
	@ApiModelProperty(value="Risorse acquisite mediante contrazioni di mutuo")
	private Double risorseMutuo;
	
	@ApiModelProperty(value="Risorse acquisite mediante apporti di capitali privati")
	private Double risorsePrivati;
	
	@ApiModelProperty(value="Stanziamenti di bilancio")
	private Double risorseBilancio;
	
	@ApiModelProperty(value="Altre risorse disponibili")
	private Double risorseAltro;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo risorse finanziarie stato/UE")
	private Double importoRisorseFinanziarie;

	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo risorse finanziarie regionali")
	private Double importoRisorseFinanziarieRegionali;

	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Mese previsto per avvio procedura contrattuale")
	private Long meseAvvioProcedura;
	
	@ApiModelProperty(value = "Responsabile unico RUP")
	private DatiGeneraliTecnicoEntry rup;
	
	public void setSettore(String settore) {
		this.settore = StringUtils.stripToNull(settore);
	}

	public String getSettore() {
		return settore;
	}

	public void setCodiceInterno(String codiceInterno) {
		this.codiceInterno = StringUtils.stripToNull(codiceInterno);
	}

	public String getCodiceInterno() {
		return codiceInterno;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = StringUtils.stripToNull(descrizione);
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setEsenteCup(String esenteCup) {
		this.esenteCup = StringUtils.stripToNull(esenteCup);
	}

	public String getEsenteCup() {
		return esenteCup;
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

	public void setRup(DatiGeneraliTecnicoEntry rup) {
		this.rup = rup;
	}

	public DatiGeneraliTecnicoEntry getRup() {
		return rup;
	}

	public void setNormativaRiferimento(String normativaRiferimento) {
		this.normativaRiferimento = StringUtils.stripToNull(normativaRiferimento);
	}

	public String getNormativaRiferimento() {
		return normativaRiferimento;
	}

	public void setStrumentoProgrammazione(String strumentoProgrammazione) {
		this.strumentoProgrammazione = StringUtils.stripToNull(strumentoProgrammazione);
	}

	public String getStrumentoProgrammazione() {
		return strumentoProgrammazione;
	}

	public void setPrevistaManodopera(String previstaManodopera) {
		this.previstaManodopera = StringUtils.stripToNull(previstaManodopera);
	}

	public String getPrevistaManodopera() {
		return previstaManodopera;
	}

	public void setRisorseMutuo(Double risorseMutuo) {
		this.risorseMutuo = risorseMutuo;
	}

	public Double getRisorseMutuo() {
		return risorseMutuo;
	}

	public void setRisorsePrivati(Double risorsePrivati) {
		this.risorsePrivati = risorsePrivati;
	}

	public Double getRisorsePrivati() {
		return risorsePrivati;
	}

	public void setRisorseBilancio(Double risorseBilancio) {
		this.risorseBilancio = risorseBilancio;
	}

	public Double getRisorseBilancio() {
		return risorseBilancio;
	}

	public void setRisorseAltro(Double risorseAltro) {
		this.risorseAltro = risorseAltro;
	}

	public Double getRisorseAltro() {
		return risorseAltro;
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

	public void setCodiceRup(String codiceRup) {
		this.codiceRup = StringUtils.stripToNull(codiceRup);
	}

	@XmlTransient
	public String getCodiceRup() {
		return codiceRup;
	}

	public void setImportoRisorseFinanziarie(Double importoRisorseFinanziarie) {
		this.importoRisorseFinanziarie = importoRisorseFinanziarie;
	}

	public Double getImportoRisorseFinanziarie() {
		return importoRisorseFinanziarie;
	}

	public void setImportoRisorseFinanziarieRegionali(
			Double importoRisorseFinanziarieRegionali) {
		this.importoRisorseFinanziarieRegionali = importoRisorseFinanziarieRegionali;
	}

	public Double getImportoRisorseFinanziarieRegionali() {
		return importoRisorseFinanziarieRegionali;
	}

	public void setMeseAvvioProcedura(Long meseAvvioProcedura) {
		this.meseAvvioProcedura = meseAvvioProcedura;
	}

	public Long getMeseAvvioProcedura() {
		return meseAvvioProcedura;
	}

}
