package com.airhacks;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.keycloak.KeycloakSecurityContext;

@ViewScoped
@Named
public class MonitoreoBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7892950469937104201L;

	Logger log = Logger.getLogger("MonitoreoBean");
	
	@Inject
	HttpServletRequest request;

	public MonitoreoBean() {
		// TODO Auto-generated constructor stub
	}
	


	private String mensaje;
	
	
	public void tokenNormal(){
		KeycloakSecurityContext keycloakSecurityContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
		String token=keycloakSecurityContext.getTokenString();
		Client client = ClientBuilder.newClient();
		Response response =client.target("http://localhost:8080/backend/resources/logs/simple").request().header("Authorization","Bearer "+token).get();
		log.info(response.getStatus()+"");
		mensaje=response.readEntity(String.class);
	}
	
	public void tokenAuth(){
		KeycloakSecurityContext keycloakSecurityContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
		String token=keycloakSecurityContext.getTokenString();
		Client client = ClientBuilder.newClient();
		Response response =client.target("http://localhost:8080/backend/resources/logs/protegido").request().header("Authorization","Bearer "+token).get();
		log.info(response.getStatus()+"");
		mensaje=response.readEntity(String.class);
	}
	
	public void sinToken(){
		Client client = ClientBuilder.newClient();
		Response response =client.target("http://localhost:8080/backend/resources/logs/simple").request().get();
		log.info(response.getStatus()+"");
		mensaje=response.readEntity(String.class);
	}

	
	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}
