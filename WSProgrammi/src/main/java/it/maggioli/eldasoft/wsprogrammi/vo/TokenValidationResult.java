package it.maggioli.eldasoft.wsprogrammi.vo;

import org.apache.commons.lang3.StringUtils;

public class TokenValidationResult {

	private boolean validated;
	private String clientId;
	private Long syscon;
	private String error;
	
	public boolean isValidated() {
		return validated;
	}
	public void setValidated(boolean validated) {
		this.validated = validated;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = StringUtils.stripToNull(clientId);
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = StringUtils.stripToNull(error);
	}
	public void setSyscon(Long syscon) {
		this.syscon = syscon;
	}
	public Long getSyscon() {
		return syscon;
	}

}
