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
package it.maggioli.eldasoft.wsprogrammi.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * Controlli di validazione sui dati
 *
 * @author Mirco.Franzoni
 */
public class ControlloEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -4433185026855332865L;

	private Long numero;

	private String sezione;
	
	private String titolo;
	
	private String messaggio;
	
	private String tipo;
	
	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}
	public String getMessaggio() {
		return messaggio;
	}
	public void setNumero(Long numero) {
		this.numero = numero;
	}
	public Long getNumero() {
		return numero;
	}
	public void setSezione(String sezione) {
		this.sezione = StringUtils.stripToNull(sezione);
	}
	public String getSezione() {
		return sezione;
	}
	public void setTitolo(String titolo) {
		this.titolo = StringUtils.stripToNull(titolo);
	}
	public String getTitolo() {
		return titolo;
	}
	public void setTipo(String tipo) {
		this.tipo = StringUtils.stripToNull(tipo);
	}
	public String getTipo() {
		return tipo;
	}
}
