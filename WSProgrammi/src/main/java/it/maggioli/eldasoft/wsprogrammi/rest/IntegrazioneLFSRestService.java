package it.maggioli.eldasoft.wsprogrammi.rest;

import java.io.IOException;
import java.text.ParseException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.maggioli.eldasoft.wsprogrammi.bl.IntegrazioneLFSManager;
import it.maggioli.eldasoft.wsprogrammi.vo.EsitoResult;
import it.maggioli.eldasoft.wsprogrammi.vo.TokenValidationResult;
import it.maggioli.eldasoft.wsprogrammi.vo.lfs.DettaglioInterventoAcquistoResult;
import it.maggioli.eldasoft.wsprogrammi.vo.lfs.InterventiAcquistiResult;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Servizio REST esposto al path "/IntegrazioneLFS".
 */
@Path("/IntegrazioneLFS")
@Component
@Api(value = "/IntegrazioneLFS")
public class IntegrazioneLFSRestService extends BaseRestService{

	/** Logger di classe. */
	protected Logger logger = LoggerFactory.getLogger(IntegrazioneLFSRestService.class);

	@Context ServletContext servletContext;
	/**
	 * Wrapper della business logic a cui viene demandata la gestione
	 */
	private IntegrazioneLFSManager integrazioneLFSManager;

	/**
	 * @param integrazioneLFSManager
	 *            avvisiManager da settare internamente alla classe.
	 */
	@Required
	@Autowired
	public void setIntegrazioneLFSManager(IntegrazioneLFSManager integrazioneLFSManager) {
		this.integrazioneLFSManager = integrazioneLFSManager;
	}
	
	/**
	 * Estrae l'elenco interventi/acquisti che soddisfano i filtri passati mediante chiamata GET e risposta JSON.
	 *
	 * @param tipoProgramma
	 *        Tipologia programma, accetta i valori 1 - Programma Lavori, 2 - Programma Forniture/Servizi
	 * @param codiceCUI
	 *        Codice CUI dell'intervento/acquisto
	 * @param descrizione
	 *        Descrizione dell'intervento/acquisto
	 * @param codiceFiscaleSA
	 *        Codice fiscale stazione appaltante richiedente 
	 * @param token
	 *        token
	 * @return JSON contenente la classe InterventiAcquistiResult
	 */
	
	@GET
	@Path("/LeggiInterventoAcquisto")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "IntegrazioneLFSRestService.leggiInterventoAcquisto", value = "Estrae l'elenco interventi/acquisti che soddisfano i filtri passati", response = InterventiAcquistiResult.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
			public Response leggiInterventoAcquisto(
					@ApiParam(value = "Tipologia programma, accetta i valori 1 - Programma Lavori, 2 - Programma Forniture/Servizi") @QueryParam("tipoProgramma") String tipoProgramma,
					@ApiParam(value = "Codice CUI dell'intervento/acquisto") @QueryParam("codiceCUI") String codiceCUI, 
					@ApiParam(value = "Descrizione dell'intervento/acquisto") @QueryParam("descrizione") String descrizione,
					@ApiParam(value = "Codice fiscale stazione appaltante richiedente") @QueryParam("codiceFiscaleSA") String codiceFiscaleSA,
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token) {

		InterventiAcquistiResult risultato;

		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new InterventiAcquistiResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("leggiInterventoAcquisto(" + tipoProgramma + "," + codiceCUI + "," + descrizione + "," + codiceFiscaleSA + "): inizio metodo");
		}
		try {
			tipoProgramma = StringUtils.stripToNull(tipoProgramma);
			codiceCUI = StringUtils.stripToNull(codiceCUI);
			if (codiceCUI != null) {
				codiceCUI = "%" + codiceCUI.toUpperCase() + "%";
			}
			descrizione = StringUtils.stripToNull(descrizione);
			if (descrizione != null) {
				descrizione = "%" + descrizione.toUpperCase() + "%";
			}
			codiceFiscaleSA = StringUtils.stripToNull(codiceFiscaleSA);

			String error = this.validateManager.verificaLeggiInterventoAcquisto(tipoProgramma, codiceCUI, descrizione, codiceFiscaleSA);

			if (error.equals("")) {
				risultato = this.integrazioneLFSManager.getInterventiAcquisti(tipoProgramma, codiceCUI, descrizione, codiceFiscaleSA);
			} else {
				risultato = new InterventiAcquistiResult();
				risultato.setError(error);
			}
		} catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante l'estrazione degli interventi / acquisti",
					ex);
			risultato = new InterventiAcquistiResult();
			risultato.setError(InterventiAcquistiResult.ERROR_UNEXPECTED
					+ ": " + ex.getMessage());
		}
		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (InterventiAcquistiResult.ERROR_NO_PARAMETRI.equals(risultato.getError()) ||
					InterventiAcquistiResult.ERROR_TIPO_PROGRAMMA.equals(risultato.getError())) {
				status = HttpStatus.BAD_REQUEST;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		logger.debug("leggiInterventoAcquisto: fine metodo [http status " + status.value() + "]");
		return Response.status(status.value()).entity(risultato).build();
	}
	
	@POST
	@Path("/CollegaInterventoAcquisto")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "IntegrazioneLFSRestService.collegaInterventoAcquisto", value = "Associa l'intervento indicato dal codiceCUI con il lavoro indicato dal codiceLavoro", response = EsitoResult.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo messaggio della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo messaggio della risposta)") })
			public Response collegaInterventoAcquisto(
					@ApiParam(value = "Codice lavoro da associare all'intervento / acquisto") @QueryParam("codiceLavoro") String codiceLavoro,
					@ApiParam(value = "Codice CUI dell'intervento/acquisto") @QueryParam("codiceCUI") String codiceCUI, 
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token
			)
	throws ParseException, IOException {

		EsitoResult risultato;
		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new EsitoResult();
			risultato.setEsito(false);
			risultato.setMessaggio(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		// FASE PRELIMINARE DI CONTROLLO DEI PARAMETRI PRIMA DI INOLTRARE ALLA
		// BUSINESS LOGIC
		if (logger.isDebugEnabled()) {
			logger.debug("collegaInterventoAcquisto(" + codiceLavoro 
					+ "," + codiceCUI
					+ "): inizio metodo");
		}

		try{
			codiceLavoro = StringUtils.stripToNull(codiceLavoro);
			if (codiceLavoro != null) {
				codiceLavoro = codiceLavoro.toUpperCase();
			}
			codiceCUI = StringUtils.stripToNull(codiceCUI);
			if (codiceCUI != null) {
				codiceCUI = codiceCUI.toUpperCase();
			}
			String error = this.validateManager.verificaCollegaInterventoAcquisto(codiceLavoro, codiceCUI);

			if (error.equals("")) {
				risultato = this.integrazioneLFSManager.collegaInterventoAcquisto(codiceLavoro, codiceCUI);
			} else {
				risultato = new EsitoResult();
				risultato.setEsito(false);
				risultato.setMessaggio(error);
			}
		}
		catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante il collegamento tra codice CUI e codice lavoro",
					ex);
			risultato = new EsitoResult();
			risultato.setEsito(false);
			risultato.setMessaggio(EsitoResult.ERROR_UNEXPECTED);
		}

		HttpStatus status = HttpStatus.OK;
		if (risultato.getMessaggio() != null) {
			if (EsitoResult.ERROR_NO_PARAMETRI.equals(risultato.getMessaggio()) ||
					EsitoResult.ERROR_INTERVENTO_NO_CUI.equals(risultato.getMessaggio()) ||
					EsitoResult.ERROR_INTERVENTO_CUI_ASSOCIATO.equals(risultato.getMessaggio())) {
				status = HttpStatus.BAD_REQUEST;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}

		logger.debug("collegaInterventoAcquisto: fine metodo [http status " + status.value()
				+ "]");
		return Response.status(status.value()).entity(risultato).build();
	}
	
	/**
	 * Estrae il dettaglio dell'intervento / acquisto associato al CUI indicato mediante chiamata GET e risposta JSON.
	 *
	 * @param codiceCUI
	 *        Codice CUI dell'intervento/acquisto
	 * @param token
	 *        token
	 * @return JSON contenente la classe DettaglioInterventoAcquistoResult
	 */
	
	@GET
	@Path("/DettaglioInterventoAcquisto")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "IntegrazioneLFSRestService.dettaglioInterventoAcquisto", value = "Estrae il dettaglio dell'intervento / acquisto associato al CUI indicato", response = DettaglioInterventoAcquistoResult.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 404, message = "Il CUI richiesto non è stato trovato (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
			public Response dettaglioInterventoAcquisto(
					@ApiParam(value = "Codice CUI dell'intervento/acquisto") @QueryParam("codiceCUI") String codiceCUI, 
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token) {

		DettaglioInterventoAcquistoResult risultato;

		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new DettaglioInterventoAcquistoResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("dettaglioInterventoAcquisto(" + codiceCUI + "): inizio metodo");
		}
		try {
			codiceCUI = StringUtils.stripToNull(codiceCUI);
			if (codiceCUI != null) {
				codiceCUI = codiceCUI.toUpperCase();
			}
			
			String error = this.validateManager.verificaDettaglioInterventoAcquisto(codiceCUI);

			if (error.equals("")) {
				risultato = this.integrazioneLFSManager.getDettaglioInterventoAcquisto(codiceCUI);
			} else {
				risultato = new DettaglioInterventoAcquistoResult();
				risultato.setError(error);
			}
		} catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante l'estrazione del dettaglio intervento / acquisto",
					ex);
			risultato = new DettaglioInterventoAcquistoResult();
			risultato.setError(InterventiAcquistiResult.ERROR_UNEXPECTED
					+ ": " + ex.getMessage());
		}
		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (DettaglioInterventoAcquistoResult.ERROR_NO_PARAMETRI.equals(risultato.getError())) {
				status = HttpStatus.BAD_REQUEST;
			} else if (DettaglioInterventoAcquistoResult.ERROR_NO_DATA.equals(risultato.getError())) {
				status = HttpStatus.NOT_FOUND;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		logger.debug("dettaglioInterventoAcquisto: fine metodo [http status " + status.value() + "]");
		return Response.status(status.value()).entity(risultato).build();
	}
	
}
