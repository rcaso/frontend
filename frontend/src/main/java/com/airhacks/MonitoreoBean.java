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
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.representation.EntitlementResponse;
import org.keycloak.authorization.client.representation.TokenIntrospectionResponse;

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
		AuthzClient authClient = AuthzClient.create();
		EntitlementResponse accessToken =authClient.entitlement(token).getAll("backend");
//		log.info("token authz "+accessToken.getToken());
//		log.info("expire in "+accessToken.getExpiresIn());
//		log.info("token id "+accessToken.getIdToken());
//		log.info("refresh expires in "+accessToken.getRefreshExpiresIn());
//		log.info("refresh token "+accessToken.getRefreshToken());
//		log.info("tipo token "+accessToken.getTokenType());
		log.info("Token de acceso simple "+token);
		log.info("Token RPT "+accessToken.getRpt());
		showToken(authClient, accessToken.getRpt());
		Response response =client.target("http://localhost:8080/backend/resources/logs/simple").request().header("Authorization","Bearer "+accessToken.getRpt()).get();
		log.info(response.getStatus()+" "+response.getHeaderString("WWW-Authenticate"));
		mensaje=response.readEntity(String.class);
	}
	
	public void tokenAuth(){
		KeycloakSecurityContext keycloakSecurityContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
		String token=keycloakSecurityContext.getTokenString();
		AuthzClient authClient = AuthzClient.create();
		EntitlementResponse accessToken =authClient.entitlement(token).getAll("backend");
		Client client = ClientBuilder.newClient();
		showToken(authClient, accessToken.getRpt());
		Response response =client.target("http://localhost:8080/backend/resources/logs/protegido").request().header("Authorization","Bearer "+accessToken.getRpt()).get();
		log.info("Token de acceso simple "+token);
		log.info("Token RPT "+accessToken.getRpt());
		log.info(response.getStatus()+" "+response.getHeaderString("WWW-Authenticate"));
		mensaje=response.readEntity(String.class);
	}
	
	public void sinToken(){
		Client client = ClientBuilder.newClient();
		Response response =client.target("http://localhost:8080/backend/resources/logs/simple").request().get();
		log.info(response.getStatus()+" "+response.getHeaderString("WWW-Authenticate"));
		mensaje=response.readEntity(String.class);
	}
	
	public void showToken(AuthzClient authClient,String rpt){
		TokenIntrospectionResponse token = authClient.protection().introspectRequestingPartyToken(rpt);
		if(token.isActive()){
			token.getPermissions().stream().forEach(permiso-> log.info(permiso.getResourceSetName()));
		} else {
			log.info("Token RPT no activo");
		}
	}

	
	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}
