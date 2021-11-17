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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Dati per la pubblicazione di un programma.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"id",
		"codiceFiscaleSA",
	    "ufficio",
		"tipologia", 
		"anno",
		"descrizione", 
		"referente",
		"numeroApprovazione",
		"dataApprovazione",
		"dataPubblicazioneApprovazione",
		"titoloAttoApprovazione",
		"urlAttoApprovazione",
		"numeroAdozione",
		"dataAdozione",
		"dataPubblicazioneAdozione",
		"titoloAttoAdozione",
		"urlAttoAdozione",
		"note1",
		"note2",
		"note2b",
		"note3",
		"note4",
		"accantonamento",
		"interventi",
		"fornitureEServizi",
		"lavoriEconomia",
		"idRicevuto"
		})
@ApiModel(description = "Contenitore per i dati di pubblicazione di un programma DM 11/2011")
public class PubblicaProgrammaDM112011Entry implements Serializable {
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
	
	@ApiModelProperty(value = "ID del programma", required = true)
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
	
	@ApiModelProperty(value = "Numero adozione")
	@Size(max=50)
	private String numeroAdozione;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value="Data adozione")  
	private Date dataAdozione; 

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value="Data pubblicazione adozione")  
	private Date dataPubblicazioneAdozione; 
	  
	@ApiModelProperty(value = "Titolo atto adozione")
	@Size(max=250)
	private String titoloAttoAdozione;
		
	@ApiModelProperty(value = "Url atto di adozione")
	@Size(max=2000)
	private String urlAttoAdozione;
	
	@ApiModelProperty(hidden=true)
	private String codiceReferente;
	
	@ApiModelProperty(value = "Note programma triennale")
	@Size(max=2000)
	private String note1;
	
	@ApiModelProperty(value = "Note scheda 2")
	@Size(max=2000)
	private String note2;
	
	@ApiModelProperty(value = "Note Scheda 2/B")
	@Size(max=2000)
	private String note2b;
	
	@ApiModelProperty(value = "Note scheda 3 Elenco annuale")
	@Size(max=2000)
	private String note3;
	
	@ApiModelProperty(value = "Note scheda 4")
	@Size(max=2000)
	private String note4;
	
	@ApiModelProperty(value="Accantonamento primo anno (art. 12 c. 1 dpr 207/2010)")
	private Double accantonamento;
	
	@ApiModelProperty(value = "Referente del programma", required = true)
	private DatiGeneraliTecnicoEntry referente;
	
	@ApiModelProperty(value="Lista interventi lavori")
	@Size(min=0)
    private List<InterventoDM112011Entry> interventi = new ArrayList<InterventoDM112011Entry>();

	@ApiModelProperty(value="Lista interventi forniture e servizi")
	@Size(min=0)
    private List<FornitureServiziDM112011Entry> fornitureEServizi = new ArrayList<FornitureServiziDM112011Entry>();

	@ApiModelProperty(value="Elenco lavori in economia")
	@Size(min=0)
    private List<LavoroEconomiaEntry> lavoriEconomia = new ArrayList<LavoroEconomiaEntry>();
	
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

	public void setNumeroAdozione(String numeroAdozione) {
		this.numeroAdozione = StringUtils.stripToNull(numeroAdozione);
	}

	public String getNumeroAdozione() {
		return numeroAdozione;
	}

	public void setDataAdozione(Date dataAdozione) {
		this.dataAdozione = dataAdozione;
	}

	public Date getDataAdozione() {
		return dataAdozione;
	}

	public void setDataPubblicazioneAdozione(Date dataPubblicazioneAdozione) {
		this.dataPubblicazioneAdozione = dataPubblicazioneAdozione;
	}

	public Date getDataPubblicazioneAdozione() {
		return dataPubblicazioneAdozione;
	}

	public void setTitoloAttoAdozione(String titoloAttoAdozione) {
		this.titoloAttoAdozione = StringUtils.stripToNull(titoloAttoAdozione);
	}

	public String getTitoloAttoAdozione() {
		return titoloAttoAdozione;
	}

	public void setUrlAttoAdozione(String urlAttoAdozione) {
		this.urlAttoAdozione = StringUtils.stripToNull(urlAttoAdozione);
	}

	public String getUrlAttoAdozione() {
		return urlAttoAdozione;
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

	public void setLavoriEconomia(List<LavoroEconomiaEntry> lavoriEconomia) {
		this.lavoriEconomia = lavoriEconomia;
	}

	public List<LavoroEconomiaEntry> getLavoriEconomia() {
		return lavoriEconomia;
	}

	public void setNote1(String note1) {
		this.note1 = StringUtils.stripToNull(note1);
	}

	public String getNote1() {
		return note1;
	}

	public void setNote2(String note2) {
		this.note2 = StringUtils.stripToNull(note2);
	}

	public String getNote2() {
		return note2;
	}

	public void setNote2b(String note2b) {
		this.note2b = StringUtils.stripToNull(note2b);
	}

	public String getNote2b() {
		return note2b;
	}

	public void setNote3(String note3) {
		this.note3 = StringUtils.stripToNull(note3);
	}

	public String getNote3() {
		return note3;
	}

	public void setNote4(String note4) {
		this.note4 = StringUtils.stripToNull(note4);
	}

	public String getNote4() {
		return note4;
	}

	public void setAccantonamento(Double accantonamento) {
		this.accantonamento = accantonamento;
	}

	public Double getAccantonamento() {
		return accantonamento;
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

	public void setInterventi(List<InterventoDM112011Entry> interventi) {
		this.interventi = interventi;
	}

	public List<InterventoDM112011Entry> getInterventi() {
		return interventi;
	}

	public void setFornitureEServizi(List<FornitureServiziDM112011Entry> fornitureEServizi) {
		this.fornitureEServizi = fornitureEServizi;
	}

	public List<FornitureServiziDM112011Entry> getFornitureEServizi() {
		return fornitureEServizi;
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
