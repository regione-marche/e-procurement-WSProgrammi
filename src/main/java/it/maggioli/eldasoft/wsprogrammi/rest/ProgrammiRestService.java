package it.maggioli.eldasoft.wsprogrammi.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.maggioli.eldasoft.wsprogrammi.bl.ProgrammiManager;
import it.maggioli.eldasoft.wsprogrammi.vo.AllegatoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.PubblicazioneResult;
import it.maggioli.eldasoft.wsprogrammi.vo.TokenValidationResult;
import it.maggioli.eldasoft.wsprogrammi.vo.ValidateEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011.PubblicaProgrammaDM112011Entry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi.DettaglioFornitureServiziResult;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi.PubblicaProgrammaFornitureServiziEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.DettaglioLavoriResult;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.PubblicaProgrammaLavoriEntry;

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
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;

/**
 * Servizio REST esposto al path "/Programmi".
 */
@Path("/Programmi")
@Component
@Api(value = "/Programmi")
public class ProgrammiRestService extends BaseRestService{

	@Context ServletContext context;
	
	/** Logger di classe. */
	protected Logger logger = LoggerFactory.getLogger(ProgrammiRestService.class);

	//@Context ServletContext servletContext;
	/**
	 * Wrapper della business logic a cui viene demandata la gestione
	 */
	private ProgrammiManager programmiManager;

	/**
	 * @param programmiManager
	 *            avvisiManager da settare internamente alla classe.
	 */
	@Required
	@Autowired
	public void setProgrammiManager(ProgrammiManager programmiManager) {
		this.programmiManager = programmiManager;
	}

	@POST
	@Path("/PubblicaDM112011")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "ProgrammiRestService.pubblicaDM112011", value = "[Deprecato] Pubblica i dati e i documenti relativi ad un programma DM 11/2011", notes = "Ritorna il risultato della pubblicazione e l'id assegnato dal sistema, che dovrà essere riutilizzato per successive pubblicazioni", response = PubblicazioneResult.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
			public Response pubblicaDM112011(
					@ApiParam(value = "Programma da pubblicare [Model=PubblicaProgrammaDM112011Entry]", required = true) PubblicaProgrammaDM112011Entry programma,
					@ApiParam(value = "Se valorizzato a '1' effettua solo il controllo dei dati, '2' effettua controllo e pubblicazione", required = true) @QueryParam("modalitaInvio") String modalitaInvio,
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token
			)
	throws ParseException, IOException {
		logger.info("PubblicaDM112011");
		PubblicazioneResult risultato;

		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new PubblicazioneResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		modalitaInvio = StringUtils.stripToNull(modalitaInvio);
		// FASE PRELIMINARE DI CONTROLLO DEI PARAMETRI PRIMA DI INOLTRARE ALLA
		// BUSINESS LOGIC
		if (logger.isDebugEnabled()) {
			logger.debug("pubblicaDM112011(" + programma.getId() 
					+ "," + token
					+ "): inizio metodo");
		}

		List<ValidateEntry> controlli = new ArrayList<ValidateEntry>();
		programma.setClientId(tokenValidate.getClientId());
		programma.setSyscon(tokenValidate.getSyscon());
		this.validateManager.validatePubblicaProgrammaDM112011(programma, controlli);
		if (controlli.size() > 0) {
			// se non supero la validazione
			risultato = new PubblicazioneResult();
			risultato.setError(PubblicazioneResult.ERROR_VALIDATION);
			risultato.setValidate(controlli);
		} else {
			if (modalitaInvio != null && (modalitaInvio.equals("2") || modalitaInvio.equals("3"))) {
				// procedo con l'inserimento
				try {
					risultato = this.programmiManager.pubblicaDM112011(programma, modalitaInvio);
					// aggiorno importi e genero pdf
					try {
						this.programmiManager.AggiornaImportiProgramma(programma.getContri(), new Long(3));
						//this.programmiManager.creaPdf(programma.getId(),programma.getContri(), programma.getIdRicevuto(), servletContext);
					} catch (Exception ex) {
						logger.error("Errore durante la generazione PDF del programma dm 11/2011",
								ex);
					}
				} catch (Exception ex) {
					logger.error(
							"Errore inaspettato durante la pubblicazione del programma dm 11/2011",
							ex);
					risultato = new PubblicazioneResult();
					risultato.setError(PubblicazioneResult.ERROR_UNEXPECTED
							+ ": " + ex.getMessage());
				}
			} else {
				risultato = new PubblicazioneResult();
				risultato.setValidate(controlli);
			}
		}

		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (PubblicazioneResult.ERROR_VALIDATION.equals(risultato
					.getError())) {
				status = HttpStatus.BAD_REQUEST;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}

		logger.debug("pubblicaDM112011: fine metodo [http status " + status.value()
				+ "]");
		logger.info("PubblicaDM112011 Result = " + status.value());
		return Response.status(status.value()).entity(risultato).build();
	}

	@POST
	@Path("/PubblicaLavori")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "ProgrammiRestService.pubblicaLavori", value = "Pubblica i dati e i documenti relativi ad un programma di lavori", notes = "Ritorna il risultato della pubblicazione e l'id assegnato dal sistema, che dovrà essere riutilizzato per successive pubblicazioni", response = PubblicazioneResult.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
			public Response pubblicaLavori(
					@ApiParam(value = "Programma da pubblicare [Model=PubblicaProgrammaLavoriEntry]", required = true) PubblicaProgrammaLavoriEntry programma,
					@ApiParam(value = "Se valorizzato a '1' effettua solo il controllo dei dati, '2' effettua controllo e pubblicazione", required = true) @QueryParam("modalitaInvio") String modalitaInvio,
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token
			)
	throws ParseException, IOException {
		logger.info("PubblicaLavori");
		PubblicazioneResult risultato;
		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new PubblicazioneResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		modalitaInvio = StringUtils.stripToNull(modalitaInvio);
		// FASE PRELIMINARE DI CONTROLLO DEI PARAMETRI PRIMA DI INOLTRARE ALLA
		// BUSINESS LOGIC
		if (logger.isDebugEnabled()) {
			logger.debug("pubblicaLavori(" + programma.getId() 
					+ "," + token
					+ "): inizio metodo");
		}

		List<ValidateEntry> controlli = new ArrayList<ValidateEntry>();
		programma.setClientId(tokenValidate.getClientId());
		programma.setSyscon(tokenValidate.getSyscon());
		try{
			this.validateManager.validatePubblicaProgrammaLavori(programma, controlli);
		}
		catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante la validazione del programma lavori",
					ex);
			risultato = new PubblicazioneResult();
			risultato.setError(PubblicazioneResult.ERROR_UNEXPECTED
					+ ": " + ex.getMessage());
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).entity(risultato).build();
		}
		//verifico se ci sono errori di validazione bloccante
		boolean validazioneNonSuperata = false; 
		for(ValidateEntry item:controlli){
			if ("E".equals(item.getTipo())){
				validazioneNonSuperata = true;
				break;
			}
		}
		if (validazioneNonSuperata) {
			// se non supero la validazione
			risultato = new PubblicazioneResult();
			risultato.setError(PubblicazioneResult.ERROR_VALIDATION);
			risultato.setValidate(controlli);
		} else {
			if (modalitaInvio != null && (modalitaInvio.equals("2") || modalitaInvio.equals("3"))) {
				// procedo con l'inserimento
				try {
					risultato = this.programmiManager.pubblicaLavori(programma, modalitaInvio);
					// aggiorno importi e genero pdf
					try {
						this.programmiManager.AggiornaImportiProgramma(programma.getContri(), new Long(1));
						//this.programmiManager.creaPdfNuovaNormativa(programma.getId(),programma.getContri(), new Long(1), programma.getIdRicevuto(), servletContext);
					} catch (Exception ex) {
						logger.error("Errore durante la generazione PDF del programma dm 11/2011",
								ex);
					}
				} catch (Exception ex) {
					logger.error(
							"Errore inaspettato durante la pubblicazione del programma lavori",
							ex);
					risultato = new PubblicazioneResult();
					risultato.setError(PubblicazioneResult.ERROR_UNEXPECTED
							+ ": " + ex.getMessage());
				}
			} else {
				risultato = new PubblicazioneResult();
				risultato.setValidate(controlli);
			}
		}

		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (PubblicazioneResult.ERROR_VALIDATION.equals(risultato
					.getError())) {
				status = HttpStatus.BAD_REQUEST;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}

		logger.debug("pubblicaLavori: fine metodo [http status " + status.value()
				+ "]");
		logger.info("PubblicaLavori Result = " + status.value());
		return Response.status(status.value()).entity(risultato).build();
	}

	@POST
	@Path("/PubblicaFornitureServizi")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "ProgrammiRestService.pubblicaFornitureServizi", value = "Pubblica i dati e i documenti relativi ad un programma di forniture e servizi", notes = "Ritorna il risultato della pubblicazione e l'id assegnato dal sistema, che dovrà essere riutilizzato per successive pubblicazioni", response = PubblicazioneResult.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
			public Response pubblicaFornitureServizi(
					@ApiParam(value = "Programma da pubblicare [Model=PubblicaProgrammaFornitureServiziEntry]", required = true) PubblicaProgrammaFornitureServiziEntry programma,
					@ApiParam(value = "Se valorizzato a '1' effettua solo il controllo dei dati, '2' effettua controllo e pubblicazione", required = true) @QueryParam("modalitaInvio") String modalitaInvio,
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token
			)
	throws ParseException, IOException {
		logger.info("PubblicaFornitureServizi");
		PubblicazioneResult risultato;

		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new PubblicazioneResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		modalitaInvio = StringUtils.stripToNull(modalitaInvio);
		// FASE PRELIMINARE DI CONTROLLO DEI PARAMETRI PRIMA DI INOLTRARE ALLA
		// BUSINESS LOGIC
		if (logger.isDebugEnabled()) {
			logger.debug("pubblicaFornitureServizi(" + programma.getId() 
					+ "," + token
					+ "): inizio metodo");
		}

		List<ValidateEntry> controlli = new ArrayList<ValidateEntry>();
		programma.setClientId(tokenValidate.getClientId());
		programma.setSyscon(tokenValidate.getSyscon());
		try{
			this.validateManager.validatePubblicaProgrammaFornitureServizi(programma, controlli);
		}
		catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante la validazione del programma forniture e servizi",
					ex);
			risultato = new PubblicazioneResult();
			risultato.setError(PubblicazioneResult.ERROR_UNEXPECTED
					+ ": " + ex.getMessage());
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).entity(risultato).build();
		}
		//verifico se ci sono errori di validazione bloccante
		boolean validazioneNonSuperata = false; 
		for(ValidateEntry item:controlli){
			if ("E".equals(item.getTipo())){
				validazioneNonSuperata = true;
				break;
			}
		}
		if (validazioneNonSuperata) {
			// se non supero la validazione
			risultato = new PubblicazioneResult();
			risultato.setError(PubblicazioneResult.ERROR_VALIDATION);
			risultato.setValidate(controlli);
		} else {
			if (modalitaInvio != null && (modalitaInvio.equals("2") || modalitaInvio.equals("3"))) {
				// procedo con l'inserimento
				try {
					risultato = this.programmiManager.pubblicaFornitureServizi(programma, modalitaInvio);
					// aggiorno importi e genero pdf
					try {
						this.programmiManager.AggiornaImportiProgramma(programma.getContri(), new Long(2));
						//this.programmiManager.creaPdfNuovaNormativa(programma.getId(),programma.getContri(), new Long(2), programma.getIdRicevuto(), servletContext);
					} catch (Exception ex) {
						logger.error("Errore durante la generazione PDF del programma dm 11/2011",
								ex);
					}
				} catch (Exception ex) {
					logger.error(
							"Errore inaspettato durante la pubblicazione del programma forniture e servizi",
							ex);
					risultato = new PubblicazioneResult();
					risultato.setError(PubblicazioneResult.ERROR_UNEXPECTED
							+ ": " + ex.getMessage());
				}
			} else {
				risultato = new PubblicazioneResult();
				risultato.setValidate(controlli);
			}
		}

		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (PubblicazioneResult.ERROR_VALIDATION.equals(risultato
					.getError())) {
				status = HttpStatus.BAD_REQUEST;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}

		logger.debug("pubblicaFornitureServizi: fine metodo [http status " + status.value()
				+ "]");
		logger.info("PubblicaFornitureServizi Result = " + status.value());
		return Response.status(status.value()).entity(risultato).build();
	}

	/**
	 * Estrae il dettaglio di un programma di lavori mediante chiamata GET e risposta JSON.
	 *
	 * @param idRicevuto
	 *        identificativo SCP del programma (id_generato/id_ricevuto)
	 * @param token
	 *        token
	 * @return JSON contenente la classe PubblicaProgrammaLavoriEntry
	 */
	
	@GET
	@Path("/DettaglioLavori")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "ProgrammiRestService.dettaglioLavori", value = "Estrae il dettaglio di un programma di lavori in base all'id passato come parametro", response = DettaglioLavoriResult.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 401, message = "Non si possiedono le credenziali per questo programma (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 404, message = "Il programma richiesto non è stato trovato (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
			public Response dettaglioLavori(
					@ApiParam(value = "Identificativo univoco SCP - IdRicevuto", required = true) @QueryParam("idRicevuto") Long idRicevuto, 
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token) {

		DettaglioLavoriResult risultato;

		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new DettaglioLavoriResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("dettaglioLavori(" + idRicevuto + "): inizio metodo");
		}
		try {
			String error = this.validateManager.verificaIdRicevuto(idRicevuto, new Long(1), tokenValidate.getClientId());

			if (error.equals("")) {
				risultato = this.programmiManager.getDettaglioLavori(idRicevuto);
			} else {
				risultato = new DettaglioLavoriResult();
				risultato.setError(error);
			}
		} catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante il recupero del dettaglio del programma lavori",
					ex);
			risultato = new DettaglioLavoriResult();
			risultato.setError(PubblicazioneResult.ERROR_UNEXPECTED
					+ ": " + ex.getMessage());
		}
		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (DettaglioLavoriResult.ERROR_NOT_FOUND.equals(risultato.getError())) {
				status = HttpStatus.NOT_FOUND;
			} else if (DettaglioLavoriResult.ERROR_VALIDATION.equals(risultato.getError())) {
				status = HttpStatus.BAD_REQUEST;
			} else if (DettaglioLavoriResult.ERROR_UNAUTHORIZED.equals(risultato.getError())) {
				status = HttpStatus.UNAUTHORIZED;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		logger.debug("dettaglioLavori: fine metodo [http status " + status.value() + "]");
		return Response.status(status.value()).entity(risultato).build();
	}
	/**
	 * Estrae il dettaglio di un programma di forniture e servizi mediante chiamata GET e risposta JSON.
	 *
	 * @param idRicevuto
	 *        identificativo SCP del programma (id_generato/id_ricevuto)
	 * @param token
	 *        token
	 * @return JSON contenente la classe PubblicaProgrammaFornitureServiziEntry
	 */
	@GET
	@Path("/DettaglioFornitureServizi")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "ProgrammiRestService.dettaglioFornitureServizi", value = "Estrae il dettaglio di un programma di forniture e servizi in base all'id passato come parametro", response = DettaglioFornitureServiziResult.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 401, message = "Non si possiedono le credenziali per questo programma (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 404, message = "Il programma richiesto non è stato trovato (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
			public Response dettaglioFornitureServizi(
					@ApiParam(value = "Identificativo univoco SCP - IdRicevuto", required = true) @QueryParam("idRicevuto") Long idRicevuto, 
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token) {

		DettaglioFornitureServiziResult risultato;

		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new DettaglioFornitureServiziResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("dettaglioFornitureServizi(" + idRicevuto + "): inizio metodo");
		}
		try {
			String error = this.validateManager.verificaIdRicevuto(idRicevuto, new Long(2), tokenValidate.getClientId());

			if (error.equals("")) {
				risultato = this.programmiManager.getDettaglioFornitureServizi(idRicevuto);
			} else {
				risultato = new DettaglioFornitureServiziResult();
				risultato.setError(error);
			}
		} catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante il recupero del dettaglio del programma forniture e servizi",
					ex);
			risultato = new DettaglioFornitureServiziResult();
			risultato.setError(DettaglioFornitureServiziResult.ERROR_UNEXPECTED
					+ ": " + ex.getMessage());
		}
		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (DettaglioFornitureServiziResult.ERROR_NOT_FOUND.equals(risultato.getError())) {
				status = HttpStatus.NOT_FOUND;
			} else if (DettaglioFornitureServiziResult.ERROR_VALIDATION.equals(risultato.getError())) {
				status = HttpStatus.BAD_REQUEST;
			} else if (DettaglioFornitureServiziResult.ERROR_UNAUTHORIZED.equals(risultato.getError())) {
				status = HttpStatus.UNAUTHORIZED;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		logger.debug("dettaglioFornitureServizi: fine metodo [http status " + status.value() + "]");
		return Response.status(status.value()).entity(risultato).build();
	}
	
	@GET
	@Path("/ScaricaPdf")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(nickname = "ProgrammiRestService.scaricaPdf", value = "Effettua il download del pdf relativo al programma")
	public Response scaricaPdf(
			@ApiParam(value = "Identificativo univoco SCP - IdRicevuto", required = true) @QueryParam("idRicevuto") Long idRicevuto, 
			@ApiParam(value = "Token", required = true) @QueryParam("token") String token) {
		
		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			return Response.status(HttpStatus.UNAUTHORIZED.value()).build();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("scaricaPdf(" + idRicevuto + "): inizio metodo");
		}
		byte[] pdf = null;
		String nomeFile = null;
		if (idRicevuto != null) {
			AllegatoEntry risultato = this.programmiManager.getPdf(idRicevuto);
			if (risultato != null) {
				pdf = risultato.getFile();
				nomeFile = risultato.getTitolo();
			}
		} 
		
		if (pdf != null) {
			ResponseBuilder rb = Response.ok(pdf, MediaType.APPLICATION_OCTET_STREAM);
			rb.header("Content-Disposition", "attachment; filename=\"" + nomeFile + ".pdf\"");
			Response response = rb.build();
			return response;
		} else {
			return Response.status(HttpStatus.NOT_FOUND.value()).build();
		}
	}
	
	@POST
	@Path("/PubblicaPdf")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "ProgrammiRestService.pubblicaPdf", value = "[Deprecato] Inserisce il documento pdf del programma", notes = "Ritorna l'esito dell'inserimento", response = PubblicazioneResult.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
			public Response pubblicaPdf(
					@ApiParam(value = "Id univoco generato dal sistema per il programma pubblicato", required = true) @QueryParam("idRicevuto") Long idRicevuto,
					@ApiParam(value = "File pdf del programma", required = true) @FormDataParam("file") InputStream file,
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token
			) throws ParseException, IOException {

		PubblicazioneResult risultato;

		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new PubblicazioneResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}

		// FASE PRELIMINARE DI CONTROLLO DEI PARAMETRI PRIMA DI INOLTRARE ALLA BUSINESS LOGIC
		if (logger.isDebugEnabled()) {
			logger.debug("pubblicaPdf(" + idRicevuto + "): inizio metodo");
		}

		AllegatoEntry documento = new AllegatoEntry();
		try {
			byte[] bytes = ByteStreams.toByteArray(file);
			documento.setFile(bytes);
			documento.setNrDoc(idRicevuto);
			risultato = this.programmiManager.setPdf(documento);
		} catch (Exception ex) {
			logger.error("Errore inaspettato durante l'inserimento del documento pdf del programma", ex);
			risultato = new PubblicazioneResult();
			risultato.setError(PubblicazioneResult.ERROR_UNEXPECTED + ": " + ex.getMessage());
		}

		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if(PubblicazioneResult.ERROR_VALIDATION.equals(risultato.getError())) {
				status = HttpStatus.BAD_REQUEST;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}

		logger.debug("pubblicaPdf: fine metodo [http status " + status.value() + "]");
		return Response.status(status.value()).entity(risultato).build();
	}

	@GET
	  @Path("/Version")
	  @ApiOperation(nickname = "ProgrammiRestService.version", value = "Metodo che ritorna la versione del servizio", response = String.class)
	  public Response version() {
	    if (logger.isDebugEnabled()) {
	      logger.debug("version(): inizio metodo");
	    }
	    String risultato = "NON DISPONIBILE";
	    HttpStatus status = HttpStatus.OK;
	    try {
	    	String pathFileVersione = "/WEB-INF/WSProgrammi_VER.TXT";
	        InputStream inputStreamVersione = context.getResourceAsStream(pathFileVersione);
	        if (inputStreamVersione != null) {
	          BufferedReader br = new BufferedReader(new InputStreamReader(inputStreamVersione));
	          try {
	        	  risultato = br.readLine();
	          } finally {
	            br.close();
	            inputStreamVersione.close();
	          }
	        }
	    } catch(Exception ex) {
	    	risultato = "NON DISPONIBILE";
	    }
	    logger.debug("version: fine metodo [http status " + status.value() + "]");
    	return Response.status(status.value()).entity(risultato).build();
	  }
}
