package webdavis;

import java.io.IOException;
import java.io.Serializable;

import edu.sdsc.grid.io.RemoteFileSystem;
import edu.sdsc.grid.io.irods.IRODSFileSystem;
import edu.sdsc.grid.io.srb.SRBFileSystem;

/**
 * A wrapper class of session information
 * @author Shunde Zhang
 */
public class DavisSession implements Serializable{
	private RemoteFileSystem remoteFileSystem;
	private String username;
	private String defaultResource;
	private String homeDirectory;
	private String account;
	private String domain;
	private String zone;
	private String serverName;
	private int serverPort;
	private String dn;
	private String sessionID;
	private String currentRoot;
	private String currentResource;
	private int sharedSessionNumber;
	public void disconnect(){
		if (remoteFileSystem!=null&&remoteFileSystem.isConnected()){
			if (remoteFileSystem instanceof SRBFileSystem){
				try {
					((SRBFileSystem)remoteFileSystem).close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (remoteFileSystem instanceof IRODSFileSystem){
				try {
					((IRODSFileSystem)remoteFileSystem).close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	public String getDn() {
		return dn;
	}
	public void setDn(String dn) {
		this.dn = dn;
	}
	public String getHomeDirectory() {
		return homeDirectory;
	}
	public String getTrashDirectory() {
		return "/"+getZone()+"/trash"+getHomeDirectory().replaceFirst("/"+getZone(), "");
	}
	public void setHomeDirectory(String homeDirectory) {
		this.homeDirectory = homeDirectory;
	}
	public DavisSession(){
		currentRoot=null;
		sharedSessionNumber=0;
	}
	public RemoteFileSystem getRemoteFileSystem() {
		if (!remoteFileSystem.isConnected()){
			Log.log(Log.DEBUG, "DavisSession: connection disconnected.");
			return null;
		}
		return remoteFileSystem;
	}
	public void setRemoteFileSystem(RemoteFileSystem remoteFileSystem) {
		this.remoteFileSystem = remoteFileSystem;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDefaultResource() {
		return defaultResource;
	}
	public void setDefaultResource(String defaultResource) {
		this.defaultResource = defaultResource;
	}
	public String toString(){
		StringBuffer buffer=new StringBuffer();
//		buffer.append("");
//		buffer.append(username);
//		buffer.append("^");
		if (remoteFileSystem instanceof SRBFileSystem) buffer.append("srb");
		if (remoteFileSystem instanceof IRODSFileSystem) buffer.append("irods");
		buffer.append("://").append(account);
		if (domain!=null) buffer.append(".").append(domain);
		buffer.append("@").append(serverName).append(":").append(serverPort);
		buffer.append("{").append(defaultResource).append("}");
		buffer.append("[").append(homeDirectory).append("]");
		buffer.append("<").append(currentRoot).append(":").append(currentResource).append(">");
		buffer.append("(shared session num:").append(sharedSessionNumber).append(")");
		return buffer.toString();
	}
	public String getCurrentRoot() {
		return currentRoot;
	}
	public void setCurrentRoot(String currentRoot) {
		this.currentRoot = currentRoot;
	}
	public String getCurrentResource() {
		return currentResource;
	}
	public void setCurrentResource(String currentResource) {
		this.currentResource = currentResource;
	}
	public void increaseSharedNumber() {
		sharedSessionNumber++;
	}
	public void descreaseSharedNumber() {
		sharedSessionNumber--;
	}
	public boolean isShared(){
		return sharedSessionNumber>0;
	}
	public boolean isConnected() {
		return remoteFileSystem.isConnected();
	}
}
