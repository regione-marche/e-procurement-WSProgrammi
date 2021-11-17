/*
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.maggioli.eldasoft.wsprogrammi.vo.tecnici.DatiGeneraliTecnicoEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Dati per la pubblicazione di un programma per forniture e servizi.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"id",
		"codiceFiscaleSA",
	    "ufficio",
		"anno",
		"descrizione", 
		"referente",
		"numeroApprovazione",
		"dataApprovazione",
		"dataPubblicazioneApprovazione",
		"titoloAttoApprovazione",
		"urlAttoApprovazione",
		"acquisti",
		"acquistiNonRiproposti",
		"idRicevuto"
		})
@ApiModel(description = "Contenitore per i dati di pubblicazione di un programma di forniture e servizi")
public class PubblicaProgrammaFornitureServiziEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@ApiModelProperty(hidden=true)
	private String codiceSA;
	
	@ApiModelProperty(hidden=true)
	private Long contri;
	
	@ApiModelProperty(hidden=true)
	private Long syscon;
	
	@ApiModelProperty(value = "ID del programma ('FS' + CodiceFiscaleSA(11 caratteri) + anno(4 cifre) + progressivo(3 cifre))", required = true)
	@Size(max=25, min=20)
	private String id;
	
	@ApiModelProperty(hidden=true)
	private String clientId;

	@ApiModelProperty(value = "Codice Fiscale Stazione appaltante", required = true)
	@Size(max=16, min=11)
	private String codiceFiscaleSA;

	@ApiModelProperty(value = "Codice Unità Organizzativa")
	@Size(max=20)
	private String codiceUnitaOrganizzativa;
	
	@ApiModelProperty(hidden=true)
	private Long idUfficio;
	  
	@ApiModelProperty(value="Ufficio/area di pertinenza")
	@Size(max=250)
	private String ufficio;
	  
	@ApiModelProperty(value = "Anno di inizio del programma", required = true)
	private Long anno;

	@ApiModelProperty(value = "Descrizione del Programma", required = true)
	@Size(max=500, min=1)
	private String descrizione;
	
	@ApiModelProperty(value = "Numero approvazione")
	@Size(max=50)
	private String numeroApprovazione;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value="Data approvazione")  
	private Date dataApprovazione; 

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value="Data pubblicazione approvazione")  
	private Date dataPubblicazioneApprovazione; 
	  
	@ApiModelProperty(value = "Titolo atto approvazione")
	@Size(max=250)
	private String titoloAttoApprovazione;
		
	@ApiModelProperty(value = "Url atto di approvazione")
	@Size(max=2000)
	private String urlAttoApprovazione;
	
	  @JsonInclude(JsonInclude.Include.NON_NULL)
	  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	  @ApiModelProperty(value="Data pubblicazione programma su sito SCP (valorizzato solo nel metodo /Programmi/DettaglioFornitureServizi)")  
	  private Date primaPubblicazioneSCP;
	  
	  @JsonInclude(JsonInclude.Include.NON_NULL)
	  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	  @ApiModelProperty(value="Utima modifica pubblicazione programma su sito SCP (valorizzato solo nel metodo /Programmi/DettaglioFornitureServizi)")  
	  private Date ultimaModificaSCP;
	  
	@ApiModelProperty(hidden=true)
	private String codiceReferente;
	
	@ApiModelProperty(value = "Referente del programma", required = true)
	private DatiGeneraliTecnicoEntry referente;
	
	@ApiModelProperty(value="Lista acquisti")
	@Size(min=0)
    private List<AcquistoEntry> acquisti = new ArrayList<AcquistoEntry>();

	@ApiModelProperty(value="Lista acquisti non riproposti")
	@Size(min=0)
    private List<AcquistoNonRipropostoEntry> acquistiNonRiproposti = new ArrayList<AcquistoNonRipropostoEntry>();

	@ApiModelProperty(value = "Id univoco generato dal sistema remoto; deve essere utilizzato per le chiamate successive")
	private Long idRicevuto;
	
	public void setIdRicevuto(Long idRicevuto) {
		this.idRicevuto = idRicevuto;
	}

	public Long getIdRicevuto() {
		return idRicevuto;
	}

	public void setCodiceFiscaleSA(String codiceFiscaleSA) {
		this.codiceFiscaleSA = StringUtils.stripToNull(codiceFiscaleSA);
	}

	public String getCodiceFiscaleSA() {
		return codiceFiscaleSA;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = StringUtils.stripToNull(descrizione);
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setCodiceSA(String codiceSA) {
		this.codiceSA = StringUtils.stripToNull(codiceSA);
	}

	@XmlTransient
	public String getCodiceSA() {
		return codiceSA;
	}

	public void setContri(Long contri) {
		this.contri = contri;
	}

	@XmlTransient
	public Long getContri() {
		return contri;
	}

	public void setId(String id) {
		this.id = StringUtils.stripToNull(id);
	}

	public String getId() {
		return id;
	}

	public void setAnno(Long anno) {
		this.anno = anno;
	}

	public Long getAnno() {
		return anno;
	}

	public void setNumeroApprovazione(String numeroApprovazione) {
		this.numeroApprovazione = StringUtils.stripToNull(numeroApprovazione);
	}

	public String getNumeroApprovazione() {
		return numeroApprovazione;
	}

	public void setDataApprovazione(Date dataApprovazione) {
		this.dataApprovazione = dataApprovazione;
	}

	public Date getDataApprovazione() {
		return dataApprovazione;
	}

	public void setDataPubblicazioneApprovazione(
			Date dataPubblicazioneApprovazione) {
		this.dataPubblicazioneApprovazione = dataPubblicazioneApprovazione;
	}

	public Date getDataPubblicazioneApprovazione() {
		return dataPubblicazioneApprovazione;
	}

	public void setTitoloAttoApprovazione(String titoloAttoApprovazione) {
		this.titoloAttoApprovazione = StringUtils.stripToNull(titoloAttoApprovazione);
	}

	public String getTitoloAttoApprovazione() {
		return titoloAttoApprovazione;
	}

	public void setUrlAttoApprovazione(String urlAttoApprovazione) {
		this.urlAttoApprovazione = StringUtils.stripToNull(urlAttoApprovazione);
	}

	public String getUrlAttoApprovazione() {
		return urlAttoApprovazione;
	}

	public void setCodiceReferente(String codiceReferente) {
		this.codiceReferente = StringUtils.stripToNull(codiceReferente);
	}

	@XmlTransient
	public String getCodiceReferente() {
		return codiceReferente;
	}

	public void setReferente(DatiGeneraliTecnicoEntry referente) {
		this.referente = referente;
	}

	public DatiGeneraliTecnicoEntry getReferente() {
		return referente;
	}

	public void setClientId(String clientId) {
		this.clientId = StringUtils.stripToNull(clientId);
	}

	@XmlTransient
	public String getClientId() {
		return clientId;
	}

	public void setIdUfficio(Long idUfficio) {
		this.idUfficio = idUfficio;
	}

	@XmlTransient
	public Long getIdUfficio() {
		return idUfficio;
	}

	public void setUfficio(String ufficio) {
		this.ufficio = StringUtils.stripToNull(ufficio);
	}

	public String getUfficio() {
		return ufficio;
	}

	public void setSyscon(Long syscon) {
		this.syscon = syscon;
	}

	@XmlTransient
	public Long getSyscon() {
		return syscon;
	}

	public void setAcquisti(List<AcquistoEntry> acquisti) {
		this.acquisti = acquisti;
	}

	public List<AcquistoEntry> getAcquisti() {
		return acquisti;
	}

	public void setAcquistiNonRiproposti(List<AcquistoNonRipropostoEntry> acquistiNonRiproposti) {
		this.acquistiNonRiproposti = acquistiNonRiproposti;
	}

	public List<AcquistoNonRipropostoEntry> getAcquistiNonRiproposti() {
		return acquistiNonRiproposti;
	}

	public void setPrimaPubblicazioneSCP(Date primaPubblicazioneSCP) {
		this.primaPubblicazioneSCP = primaPubblicazioneSCP;
	}

	public Date getPrimaPubblicazioneSCP() {
		return primaPubblicazioneSCP;
	}

	public void setUltimaModificaSCP(Date ultimaModificaSCP) {
		this.ultimaModificaSCP = ultimaModificaSCP;
	}

	public Date getUltimaModificaSCP() {
		return ultimaModificaSCP;
	}

	public void setCodiceUnitaOrganizzativa(String codiceUnitaOrganizzativa) {
		this.codiceUnitaOrganizzativa = StringUtils.stripToNull(codiceUnitaOrganizzativa);
	}

	public String getCodiceUnitaOrganizzativa() {
		if (codiceUnitaOrganizzativa != null)
			return codiceUnitaOrganizzativa.toUpperCase();
		else
			return codiceUnitaOrganizzativa;
	}

}
