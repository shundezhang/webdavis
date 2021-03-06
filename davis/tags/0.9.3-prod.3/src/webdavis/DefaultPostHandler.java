package webdavis;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.sdsc.grid.io.DirectoryMetaData;
import edu.sdsc.grid.io.FileMetaData;
import edu.sdsc.grid.io.GeneralFileSystem;
import edu.sdsc.grid.io.GeneralMetaData;
import edu.sdsc.grid.io.MetaDataCondition;
import edu.sdsc.grid.io.MetaDataField;
import edu.sdsc.grid.io.MetaDataRecordList;
import edu.sdsc.grid.io.MetaDataSelect;
import edu.sdsc.grid.io.MetaDataSet;
import edu.sdsc.grid.io.MetaDataTable;
import edu.sdsc.grid.io.RemoteFile;
import edu.sdsc.grid.io.RemoteFileOutputStream;
import edu.sdsc.grid.io.RemoteFileSystem;
import edu.sdsc.grid.io.ResourceMetaData;
import edu.sdsc.grid.io.UserMetaData;
import edu.sdsc.grid.io.irods.IRODSException;
import edu.sdsc.grid.io.irods.IRODSFile;
import edu.sdsc.grid.io.irods.IRODSFileOutputStream;
import edu.sdsc.grid.io.irods.IRODSFileSystem;
import edu.sdsc.grid.io.irods.IRODSMetaDataSet;
import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.srb.SRBFileOutputStream;
import edu.sdsc.grid.io.srb.SRBFileSystem;
import edu.sdsc.grid.io.srb.SRBMetaDataRecordList;
import edu.sdsc.grid.io.srb.SRBMetaDataSet;

//import edu.sdsc.grid.io.irods.IRODSCommandsDeleteTest;
//import edu.sdsc.jargon.testutils.AssertionHelper;
//import edu.sdsc.jargon.testutils.TestingPropertiesHelper;
//import edu.sdsc.jargon.testutils.filemanip.FileGenerator;
//import edu.sdsc.jargon.testutils.icommandinvoke.IcommandInvoker;
//import edu.sdsc.jargon.testutils.icommandinvoke.IrodsInvocationContext;
//import edu.sdsc.jargon.testutils.icommandinvoke.icommands.ImkdirCommand;
//import edu.sdsc.jargon.testutils.icommandinvoke.icommands.IputCommand;
//import static edu.sdsc.jargon.testutils.TestingPropertiesHelper.GENERATED_FILE_DIRECTORY_KEY;

/**
 * Default implementation of a handler for requests using the HTTP POST method.
 * 
 * @author Shunde Zhang
 * @author Eric Glass
 */
public class DefaultPostHandler extends AbstractHandler {

	private static Random random = new Random();
	
	/**
	 * Services requests which use the HTTP POST method. 
	 * 
	 * @param request
	 *            The request being serviced.
	 * @param response
	 *            The servlet response.
	 * @param auth
	 *            The user's authentication information.
	 * @throws SerlvetException
	 *             If an application error occurs.
	 * @throws IOException
	 *             If an IO error occurs while handling the request.
	 */
	public void service(HttpServletRequest request, HttpServletResponse response, DavisSession davisSession)
			throws ServletException, IOException {

		String method = request.getParameter("method");
		if (method == null)
			return;
		String url = getRemoteURL(request, getRequestURL(request), getRequestURICharset());
		Log.log(Log.DEBUG, "url:" + url + " method:" + method);

//		try {  //### Not needed anymore because of above test?
//			file.getPermissions(); // Test server connection
//		} catch (SocketException e) {
//			connected = false;
//			message = e.getMessage();
//		} catch (IRODSException e) {
//			message = e.getMessage();
//   			if (message.contains("IRODS error occured -816000")) // Invalid Argument seems to indicate dropped connection too
//   				connected = false;
//		}

		RemoteFile file = null;
		try {
			file = getRemoteFile(request, davisSession);
		} catch (NullPointerException e) {
			Log.log(Log.CRITICAL, "Caught a NullPointerException in DefaultGethandler.service. request=" + request.getRequestURI() + " session=" + davisSession
							+ "\nAborting request. Exception is:"+DavisUtilities.getStackTrace(e));
			internalError(response, "Jargon error in DefaultGethandler.service");
			return;
		}
		Log.log(Log.DEBUG, "POST Request for resource \"{0}\".", file.getAbsolutePath());

		if (file.getName().equals("noaccess")) { 
			Log.log(Log.WARNING, "File " + file.getAbsolutePath() + " does not exist or unknown server error - Jargon says 'noaccess'");
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "File " + file.getAbsolutePath() + " provides no access.");
				response.flushBuffer();
			} catch (IOException e) {
				if (e.getMessage().equals("Closed"))
					Log.log(Log.WARNING, file.getAbsolutePath() + ": connection to server may have been lost.");
				throw (e);
			}
			return;
		}
//		try {
//			file = getRemoteFile(request, davisSession);
//		} catch (SocketException e) {
//			lostConnection(response, e.getMessage());
//			return;
//		}
//		Log.log(Log.DEBUG, "GET Request for resource \"{0}\".", file);
		if (!file.exists()) {
			boolean connected = true;
			String message = "";
			if (davisSession.getRemoteFileSystem() instanceof IRODSFileSystem) {
				try {
					((IRODSFileSystem)davisSession.getRemoteFileSystem()).miscServerInfo();
				} catch (ProtocolException e) {
					connected = false;
					message = e.getMessage();
				} catch (SocketException e) {
					connected = false;
					message = e.getMessage();
				} catch (Exception e) {
					Log.log(Log.WARNING, "Jargon exception when testing for connection (post): "+e);					
				}
			}
			if (!connected) {
				lostConnection(response, message);
				return;
			}
			Log.log(Log.WARNING, "File " + file.getAbsolutePath() + " does not exist or unknown server error.");					
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File " + file.getAbsolutePath() + " does not exist.");
			response.flushBuffer();
			return;
		}
		String requestUrl = getRequestURL(request);
		Log.log(Log.DEBUG, "Request URL: {0}", requestUrl);
		StringBuffer json = new StringBuffer();
		
		String requestUIHandle = null;
		if (request.getParameter("uihandle") != null) {
			requestUIHandle = request.getParameter("uihandle");
			if (requestUIHandle.equals("null"))
				requestUIHandle = null;
		}
		
        response.setContentType("text/json; charset=\"utf-8\"");
		
		if (method.equalsIgnoreCase("permission")) {
			String username = request.getParameter("username");
			boolean recursive = false;
			JSONArray jsonArray = getJSONContent(request);						
			
			// Write permissions for given items
			if (jsonArray != null) {	
		    	ArrayList<RemoteFile> fileList = new ArrayList<RemoteFile>();		    						
	    		getFileList(request, davisSession, fileList, jsonArray);

				GeneralFileSystem fileSystem = file.getFileSystem();
				String domain = null;
				String permission = null;
				if (username != null) {		// Set permissions
					domain = request.getParameter("domain");
					permission = request.getParameter("permission");
				}
				try {
					recursive = Boolean.parseBoolean(request.getParameter("recursive"));
				} catch (Exception _e) {}
				Log.log(Log.DEBUG, "recursive="+recursive);
				String sticky = request.getParameter("sticky");
//				for (int j = 0; j < filesArray.size(); j++) {
				for (int j = 0; j < fileList.size(); j++) {
//					String fileName = (String)filesArray.get(j);
//					RemoteFile selectedFile = getRemoteFile(file.getAbsolutePath()+file.getPathSeparator()+fileName, davisSession);
					RemoteFile selectedFile = fileList.get(j);
//					if (j == filesArray.size()-1)		// Use the last file in the list for returning metadata below
					if (j == fileList.size()-1)		// Use the last file in the list for returning metadata below
						file = selectedFile;
					try {
						if (username != null) {
							if (fileSystem instanceof SRBFileSystem) {
								Log.log(Log.DEBUG, "change permission for "+username+"."+domain+" to "+permission+" (recursive="+recursive+")");
								((SRBFile) selectedFile).changePermissions(permission, username, domain, recursive);
							} else if (fileSystem instanceof IRODSFileSystem) {
								Log.log(Log.DEBUG, "change permission for "+username+" to "+permission+" (recursive="+recursive+")");
						/*		if (recursive) 
									iRODSSetPermission((IRODSFile)selectedFile, permission, username);
								else*/
									((IRODSFile)selectedFile).changePermissions(permission, username, recursive);
							}
						}
						if (sticky!=null) {
							Log.log(Log.DEBUG, "set "+selectedFile.getAbsolutePath()+" -- sticky:"+sticky);
							boolean flag=false;
							try {
								flag = Boolean.parseBoolean(sticky);
							} catch (Exception _e) {
							}
							this.setSticky(selectedFile, flag, recursive);
						}
					} catch (IOException e){
			        	Log.log(Log.DEBUG, "Set permissions failed: "+e);
			        	String s = e.getMessage();
			        	if (s.endsWith("-818000") || s.endsWith("-816000")) 
			        		s = "you don't have permission"; // irods error -818000 or -816000
	        			response.sendError(HttpServletResponse.SC_FORBIDDEN, s);
	        			return;
					}
				}
			}

			// Fetch permissions for item
			MetaDataRecordList[] permissions = null;
			json.append("{\n");
			if (file.getFileSystem() instanceof SRBFileSystem) {
				permissions = ((SRBFile) file).getPermissions(true);
				if (file.isDirectory()){
					json.append(escapeJSONArg("sticky")+":"+escapeJSONArg(""+isPermInherited(file))+",\n");
				}
				json.append(escapeJSONArg("items")+":[");
				if (permissions != null) {
					for (int i = 0; i < permissions.length; i++) {
						// for (MetaDataField f:p.getFields()){
						// Log.log(Log.DEBUG, f.getName()+" "+p.getValue(f));
						// }
						if (i > 0)
							json.append(",\n");
						else
							json.append("\n");
						// "user name"
						json.append("{"+escapeJSONArg("username")+":"+escapeJSONArg(""+permissions[i].getValue(SRBMetaDataSet.USER_NAME))+",");
						// "user domain"
						json.append(escapeJSONArg("domain")+":"+escapeJSONArg(""+permissions[i].getValue(SRBMetaDataSet.USER_DOMAIN))+",");
	                    if (file.isDirectory()) 	// "directory access constraint"
	    					json.append(escapeJSONArg("permission")+":"+escapeJSONArg(""+permissions[i].getValue(SRBMetaDataSet.DIRECTORY_ACCESS_CONSTRAINT))+"}");
	                    else 	// "file access constraint"
	    					json.append(escapeJSONArg("permission")+":"+escapeJSONArg(""+permissions[i].getValue(SRBMetaDataSet.ACCESS_CONSTRAINT))+"}");
					}
				}
			} else if (file.getFileSystem() instanceof IRODSFileSystem) {
				if (file.isDirectory()){
					permissions = ((IRODSFile) file).query(new String[]{DirectoryMetaData.DIRECTORY_INHERITANCE});
					boolean stickyBit = false;
					if (permissions != null && permissions.length > 0){
						String stickBitStr = (String)permissions[0].getValue(DirectoryMetaData.DIRECTORY_INHERITANCE);
						Log.log(Log.DEBUG, "stickBitStr: "+stickBitStr);
						stickyBit = stickBitStr != null && stickBitStr.equals("1");
					}
					json.append(escapeJSONArg("sticky")+":"+escapeJSONArg(""+stickyBit)+",\n");
//					permissions = ((IRODSFile) file).query(new String[]{IRODSMetaDataSet.DIRECTORY_USER_NAME, IRODSMetaDataSet.DIRECTORY_USER_ZONE,
//							IRODSMetaDataSet.DIRECTORY_ACCESS_CONSTRAINT});
					permissions = file.getFileSystem().query(
							new MetaDataCondition[] {
									MetaDataSet.newCondition(IRODSMetaDataSet.DIRECTORY_NAME, MetaDataCondition.EQUAL, file.getAbsolutePath())},
							new MetaDataSelect[]{
									MetaDataSet.newSelection(IRODSMetaDataSet.DIRECTORY_USER_NAME),
									MetaDataSet.newSelection(IRODSMetaDataSet.DIRECTORY_USER_ZONE),
									MetaDataSet.newSelection(IRODSMetaDataSet.DIRECTORY_ACCESS_CONSTRAINT),
									MetaDataSet.newSelection(IRODSMetaDataSet.OWNER)}, 
							DavisUtilities.JARGON_MAX_QUERY_NUM);
				}else {
//					permissions = ((IRODSFile) file).query(new String[]{UserMetaData.USER_NAME,	GeneralMetaData.ACCESS_CONSTRAINT});
					permissions = file.getFileSystem().query( 
							new MetaDataCondition[] {
									MetaDataSet.newCondition(GeneralMetaData.DIRECTORY_NAME, MetaDataCondition.EQUAL, file.getParent()),
//									MetaDataSet.newCondition(IRODSMetaDataSet.FILE_REPLICA_STATUS, MetaDataCondition.EQUAL, "1"),
									MetaDataSet.newCondition(IRODSMetaDataSet.FILE_NAME, MetaDataCondition.EQUAL, file.getName())},
							new MetaDataSelect[]{
//									MetaDataSet.newSelection(IRODSMetaDataSet.RESOURCE_NAME)});
									MetaDataSet.newSelection(IRODSMetaDataSet.USER_NAME),
									MetaDataSet.newSelection(IRODSMetaDataSet.ACCESS_CONSTRAINT),
									MetaDataSet.newSelection(IRODSMetaDataSet.OWNER)}, 
							DavisUtilities.JARGON_MAX_QUERY_NUM);
				}

				String owner = "unknown";
				if (permissions != null && permissions.length > 0)
					owner = (String)permissions[0].getValue(IRODSMetaDataSet.OWNER);
				json.append(escapeJSONArg("owner")+":"+escapeJSONArg(owner)+",\n");
				Log.log(Log.DEBUG, "irods permissions: "+permissions);
				json.append(escapeJSONArg("items")+":[");
				if (permissions != null) {
					for (int i = 0; i < permissions.length; i++) {
//						for (MetaDataField f:permissions[i].getFields()){
//						Log.log(Log.DEBUG, f.getName()+" "+permissions[i].getValue(f));
//						}
						if (i > 0)
							json.append(",\n");
						else
							json.append("\n");
						// "user domain"
	                    if (file.isDirectory()) {	// "user name"
							json.append("{"+escapeJSONArg("username")+":");
							String s = ""+permissions[i].getValue(IRODSMetaDataSet.DIRECTORY_USER_NAME);
							if (!((IRODSFileSystem)file.getFileSystem()).getZone().equals(permissions[i].getValue(IRODSMetaDataSet.DIRECTORY_USER_ZONE)))	
								s += "#"+permissions[i].getValue(IRODSMetaDataSet.DIRECTORY_USER_ZONE);
							json.append(escapeJSONArg(s)+",");
	    					// "directory access constraint"
	    					json.append(escapeJSONArg("permission")+":"+escapeJSONArg(DavisUtilities.iPermissionToPermission((String)permissions[i].getValue(IRODSMetaDataSet.DIRECTORY_ACCESS_CONSTRAINT)))+"}");
	                    } else {	// "user name"
							json.append("{"+escapeJSONArg("username")+":"+escapeJSONArg(""+permissions[i].getValue(UserMetaData.USER_NAME))+",");
	    					// "file access constraint"
	    					json.append(escapeJSONArg("permission")+":"+escapeJSONArg(DavisUtilities.iPermissionToPermission((String)permissions[i].getValue(GeneralMetaData.ACCESS_CONSTRAINT)))+"}");
	                    }
					}
				}
			}
			json.append("\n]}");
			
		} else if (method.equalsIgnoreCase("metadata")) {
			if (request.getContentLength() > 0) {	// write metadata if given in request
				JSONArray jsonArray = getJSONContent(request);			
//		        InputStream input = request.getInputStream();
//		        byte[] buf = new byte[request.getContentLength()];
//		        int count=input.read(buf);
//		        Log.log(Log.DEBUG, "read:"+count);
//		        Log.log(Log.DEBUG, "received metadata: " + new String(buf));
//
//				// for testing
////				String line = null;
////				StringBuffer buffer = new StringBuffer();
////				while ((line = reader.readLine()) != null) {
////					buffer.append(line);
////				}
////				Log.log(Log.DEBUG, "received metadata: " + buffer);
//
//				JSONArray jsonArray=(JSONArray)JSONValue.parse(new String(buf));
				if (jsonArray != null) {	

			    	ArrayList<RemoteFile> fileList = new ArrayList<RemoteFile>();
//			    	try {
			    		getFileList(request, davisSession, fileList, jsonArray);
//			    	} catch (ServletException e) {
//			    		if (!checkClientInSync(response, e))
//			    			return;
//			    	}
				
					JSONObject jsonObject = (JSONObject)jsonArray.get(0);
//					JSONArray filesArray = (JSONArray)jsonObject.get("files");
					JSONArray metadataArray = (JSONArray)jsonObject.get("metadata");
					GeneralFileSystem fileSystem = file.getFileSystem();
					
//					for (int j = 0; j < filesArray.size(); j++) {
					for (int j = 0; j < fileList.size(); j++) {
//						String fileName = (String)filesArray.get(j);
//						RemoteFile selectedFile = getRemoteFile(file.getAbsolutePath()+file.getPathSeparator()+fileName, davisSession);
						RemoteFile selectedFile = fileList.get(j);
						Log.log(Log.DEBUG, "changing metadata for: "+selectedFile);
//						if (j == filesArray.size()-1)		// Use the last file in the list for returning metadata below
						if (j == fileList.size()-1)		// Use the last file in the list for returning metadata below
							file = selectedFile;

						MetaDataTable metaDataTable = null;
						try {
							if (fileSystem instanceof SRBFileSystem) {
								String[][] definableMetaDataValues = new String[metadataArray.size()][2];
		
		    					for (int i = 0; i < metadataArray.size(); i++) {
		    						definableMetaDataValues[i][0] = (String) ((JSONObject) metadataArray.get(i)).get("name");
		    						definableMetaDataValues[i][1] = (String) ((JSONObject) metadataArray.get(i)).get("value");
		    					}
		
		    					int[] operators = new int[definableMetaDataValues.length];
								MetaDataRecordList rl;
								MetaDataField mdf=null;
								if (!selectedFile.isDirectory()){
									mdf=SRBMetaDataSet.getField(SRBMetaDataSet.DEFINABLE_METADATA_FOR_FILES);
								}else{
									mdf=SRBMetaDataSet.getField(SRBMetaDataSet.DEFINABLE_METADATA_FOR_DIRECTORIES);
								}
								if (mdf!=null){
									rl = new SRBMetaDataRecordList(mdf,(MetaDataTable) null);
									selectedFile.modifyMetaData(rl);
									metaDataTable = new MetaDataTable(operators, definableMetaDataValues);
									rl = new SRBMetaDataRecordList(mdf,metaDataTable);
									selectedFile.modifyMetaData(rl);
								}
		
							}else if (fileSystem instanceof IRODSFileSystem) {
								//delete all metadata, uses wildcards
		//						try{
								((IRODSFile)selectedFile).deleteMetaData(new String[]{"%","%","%"});
		//						}catch (Exception _e){}
								
								String[][] definableMetaDataValues = new String[metadataArray.size()][3];
		
		    					for (int i = 0; i < metadataArray.size(); i++) {
		    						definableMetaDataValues[i][0] = (String) ((JSONObject) metadataArray.get(i)).get("name");
		    						definableMetaDataValues[i][1] = (String) ((JSONObject) metadataArray.get(i)).get("value");
		    						definableMetaDataValues[i][2] = (String) ((JSONObject) metadataArray.get(i)).get("unit");
		    					}
		    					for (String[] metadata:definableMetaDataValues)
		    						((IRODSFile)selectedFile).modifyMetaData(metadata);
							}
						} catch (IOException e){
				        	Log.log(Log.DEBUG, "Set permissions failed: "+e);
				        	String s = e.getMessage();
				        	if (s.endsWith("-818000")) 
				        		s = "you don't have permission"; // irods error -818000 
		        			response.sendError(HttpServletResponse.SC_FORBIDDEN, s);
		        			return;
						}
					}
				}
			}

			// Get and return metadata 
			MetaDataSelect[] selects=null;
			MetaDataRecordList[] rl = null;
			json.append("{\n"+escapeJSONArg("items")+":[");
			boolean b = false;
			if (file.getFileSystem() instanceof SRBFileSystem) {
				// conditions = new MetaDataCondition[0];
				// conditions[0] = MetaDataSet.newCondition(
				// SRBMetaDataSet.DEFINABLE_METADATA_FOR_FILES, metaDataTable );

				if (!file.isDirectory()){
					selects = new MetaDataSelect[1];
					// "definable metadata for files"
					selects[0] = MetaDataSet.newSelection(SRBMetaDataSet.DEFINABLE_METADATA_FOR_FILES);
				}else{
					selects = new MetaDataSelect[1];
					// "definable metadata for files"
					selects[0] = MetaDataSet.newSelection(SRBMetaDataSet.DEFINABLE_METADATA_FOR_DIRECTORIES);
				}
				if (selects!=null){
					rl = file.query(selects);
				}
				if (rl != null) { // Nothing in the database matched the query
					for (int i = 0; i < rl.length; i++) {
//						if (i > 0)
//							json.append(",\n");
//						else
//							json.append("\n");

						int metaDataIndex;
						if (file.isDirectory())
							metaDataIndex = rl[i].getFieldIndex(SRBMetaDataSet.DEFINABLE_METADATA_FOR_DIRECTORIES);
						else
							metaDataIndex = rl[i].getFieldIndex(SRBMetaDataSet.DEFINABLE_METADATA_FOR_FILES);
						if (metaDataIndex > -1) {
							MetaDataTable t = rl[i].getTableValue(metaDataIndex);
							for (int j = 0; j < t.getRowCount(); j++) {
								if (b)
									json.append(",\n");
								else
									json.append("\n");
								json.append("{"+escapeJSONArg("name")+":");
								json.append(escapeJSONArg(t.getStringValue(j, 0))+",");
								json.append(escapeJSONArg("value")+":"+escapeJSONArg(t.getStringValue(j, 1))+"}");
								b = true;
							}
						}

						// "definable metadata for files"
						// String[]
						// lines=rl[i].getValue(SRBMetaDataSet.DEFINABLE_METADATA_FOR_FILES).toString().split("\n");
						// boolean b=false;
						// for (int j=0;j<lines.length;j++){
						// if (b)
						// str.append(",\n");
						// else
						// str.append("\n");
						// if (lines[j].length()>0){
						// str.append("{name:'");
						// str.append(lines[j].replaceAll(" = ",
						// "', value:'").trim());
						// str.append("'}");
						// b=true;
						// }
						// }
						// str.append("{name:'").append(rl[i].).append("', ");
						// str.append("value:'").append(permissions[i].getValue("file access constraint")).append("'}");
						// for (int j=0;j<rl[i].getFieldCount();j++){
						// System.out.println("field name: "+rl[i].getFieldName(j));
						// System.out.println("value: "+rl[i].getValue(j));
						// }
					}
				}

			}else if (file.getFileSystem() instanceof IRODSFileSystem) {
				selects=new MetaDataSelect[3];
				if (file.isDirectory()){
				    selects[0] = MetaDataSet.newSelection( IRODSMetaDataSet.META_COLL_ATTR_NAME );
					selects[1] = MetaDataSet.newSelection( IRODSMetaDataSet.META_COLL_ATTR_VALUE );
					selects[2] = MetaDataSet.newSelection( IRODSMetaDataSet.META_COLL_ATTR_UNITS );    
				}else{
				    selects[0] = MetaDataSet.newSelection( IRODSMetaDataSet.META_DATA_ATTR_NAME );
					selects[1] = MetaDataSet.newSelection( IRODSMetaDataSet.META_DATA_ATTR_VALUE );
					selects[2] = MetaDataSet.newSelection( IRODSMetaDataSet.META_DATA_ATTR_UNITS );    
				}
				rl = file.query( selects );
//				selects = new MetaDataSelect[1];
//				// "definable metadata for files"
//				selects[0] = MetaDataSet
//						.newSelection(IRODSMetaDataSet.GROUP_DEFINABLE_METADATA);
//				rl = file.query( selects );
				if (rl != null) { // Nothing in the database matched the query
					for (int i = 0; i < rl.length; i++) {
//						if (i > 0)
//							json.append(",\n");
//						else
//							json.append("\n");
						if (i>0) json.append(",\n");
						if (file.isDirectory()){
							json.append("{"+escapeJSONArg("name")+":");
							json.append(escapeJSONArg((String)rl[i].getValue(IRODSMetaDataSet.META_COLL_ATTR_NAME))+",");
							json.append(escapeJSONArg("value")+":"+escapeJSONArg((String)rl[i].getValue(IRODSMetaDataSet.META_COLL_ATTR_VALUE))+",");
							json.append(escapeJSONArg("unit")+":"+escapeJSONArg((String)rl[i].getValue(IRODSMetaDataSet.META_COLL_ATTR_UNITS))+"}");
						}else{
							json.append("{"+escapeJSONArg("name")+":");
							json.append(escapeJSONArg((String)rl[i].getValue(IRODSMetaDataSet.META_DATA_ATTR_NAME))+",");
							json.append(escapeJSONArg("value")+":"+escapeJSONArg((String)rl[i].getValue(IRODSMetaDataSet.META_DATA_ATTR_VALUE))+",");
							json.append(escapeJSONArg("unit")+":"+escapeJSONArg((String)rl[i].getValue(IRODSMetaDataSet.META_DATA_ATTR_UNITS))+"}");
						}
						b = true;
					}
				}
			}
			json.append("\n]}");
		} else if (method.equalsIgnoreCase("upload")) {
			response.setContentType("text/html");
			if (!ServletFileUpload.isMultipartContent(request)) // Returns json wrapped in an HTML textarea 
	            json.append(wrapJSONInHTML(escapeJSONArg("status")+":"+escapeJSONArg("failed")+","+escapeJSONArg("message")+":"+escapeJSONArg("Invalid request (not multipart)")));
	        else {
                long contentLength = request.getContentLength();
                if (contentLength == -1)
                	try {
                		contentLength = Long.parseLong(request.getHeader("x-expected-entity-length"));
                	} catch (NumberFormatException e){}
	            if (contentLength < 0) 
	            	json.append(wrapJSONInHTML(escapeJSONArg("status")+":"+escapeJSONArg("failed")+","+escapeJSONArg("message")+":"+escapeJSONArg("Your browser can't upload files larger than 2Gb")));
	            else {
	                Tracker tracker = createTracker(contentLength);
	                ClientInstance client = davisSession.getClientInstance(requestUIHandle);
	                if (client != null)
	                	client.setTracker(tracker);
			      	
			        String encoding = request.getCharacterEncoding();
			        if (encoding == null) 
			            encoding = "UTF-8";
			        
			        ServletFileUpload uploadProcessor = new ServletFileUpload();
			        uploadProcessor.setHeaderEncoding(encoding);
	//	      		upload.setSizeMax(getSizeLimit(request));	// Set maximum file size allowed for transfer
	
			        try {
			        	boolean result = false;
			            FileItemIterator iter = uploadProcessor.getItemIterator(request);
			            if (iter.hasNext()) {
			                FileItemStream fileItemStream = iter.next();
			                if (!fileItemStream.isFormField()) {
			                	InputStream inputStream = fileItemStream.openStream();
			                	String fileName = fileItemStream.getName();
			                	char c = '/';
			                	if (fileName.startsWith(":\\", 1))	// Win32 upload
			                		c = '\\';
			                	int j = fileName.lastIndexOf(c); 
			                	if (j >= 0)
			                		fileName = fileName.substring(j+1);
		                        file = getRemoteFile(file.getAbsolutePath()+file.getPathSeparator()+fileName, davisSession);
		                        boolean existsCurrently = file.exists();
		                        if (existsCurrently /*&& !file.isFile()*/) {
		                        	Log.log(Log.WARNING, file.getAbsolutePath()+" already exists on server");
		            	            json.append(wrapJSONInHTML(escapeJSONArg("status")+":"+escapeJSONArg("failed")+","+escapeJSONArg("message")+":"+escapeJSONArg("File already exists")));
		                        } else {	                        
			                		if (davisSession.getCurrentResource() == null) 
			                			davisSession.setCurrentResource(davisSession.getDefaultResource());
			                        RemoteFileOutputStream stream = null;
			                    	Log.log(Log.DEBUG, "saving file "+file.getAbsolutePath()+" into res:"+davisSession.getCurrentResource());
			                        if (file.getFileSystem() instanceof SRBFileSystem) {
			                        	((SRBFile)file).setResource(davisSession.getCurrentResource());
			                        	stream = new SRBFileOutputStream((SRBFile)file);
			                        }else if (file.getFileSystem() instanceof IRODSFileSystem) {
			                        	stream = new IRODSFileOutputStream((IRODSFile)file);
			                        }
			                        BufferedOutputStream outputStream = new BufferedOutputStream(stream, 1024*256);  //Buffersize of 256k seems to give max speed
			                        try {
			                        	copy(tracker, inputStream, outputStream);
			                        } catch (IOException e) {
			                        	try {
					                        outputStream.flush();
					                        outputStream.close();
			                        	} catch (IOException ee) {}
			                        	throw e;
			                        }
			                        outputStream.flush();
			                        outputStream.close();
				                    if (tracker.getBytesReceived() >= 0) {
				                    	tracker.setComplete();
				                        json.append(wrapJSONInHTML(escapeJSONArg("status")+":"+escapeJSONArg("success")+","+escapeJSONArg("message")+":"+escapeJSONArg(""+tracker.getBytesReceived())));
				                        result = true;
				                    } 
				                    client = davisSession.getClientInstance(requestUIHandle);
				                    if (client != null)
				                    	client.setTracker(null);
		                        }
			                }
			            }
			            if (!result) 
			            	json.append(wrapJSONInHTML(escapeJSONArg("status")+":"+escapeJSONArg("failed")+","+escapeJSONArg("message")+":"+escapeJSONArg("No file to upload")));
			        } catch (EOFException e) {
	                    json.append(wrapJSONInHTML(escapeJSONArg("status")+":"+escapeJSONArg("failed")+","+escapeJSONArg("message")+":"+escapeJSONArg("Unexpected end of file")));
			        } catch (IOException e) {
			        	Log.log(Log.DEBUG, "Upload failed: "+e);
			        	String s = e.getMessage();
			        	if (s.equals("IRODS error occured msg")) //sic
			        		s = "you don't have permission to upload here"; // Assume it's irods error -818000
	                    json.append(wrapJSONInHTML(escapeJSONArg("status")+":"+escapeJSONArg("failed")+","+escapeJSONArg("message")+":"+escapeJSONArg(s)));
			        } catch (FileUploadException e) {
			        	Log.log(Log.DEBUG, "Upload failed: "+e);
		                json.append(wrapJSONInHTML(escapeJSONArg("status")+":"+escapeJSONArg("failed")+","+escapeJSONArg("message")+":"+escapeJSONArg(e.getMessage())));
			        }
	            }
	        }
		} else if (method.equalsIgnoreCase("uploadstatus")) {	
			Tracker tracker = null;
            ClientInstance client = davisSession.getClientInstance(requestUIHandle);
            if (client == null) {
    			Log.log(Log.DEBUG, "Transfer for "+requestUIHandle+" does not exist.");
    			response.sendError(HttpServletResponse.SC_NOT_FOUND);
    			return;
            }
            tracker = client.getTracker();
			if (tracker != null) {
				long transferred = -1;
				long total = -1;
				try {
					transferred = tracker.getBytesReceived();
					total = tracker.getSize();
				} catch (Exception e) {}
				if (transferred > -1 && total > -1)
					json.append("{"+escapeJSONArg("transferred")+":"+transferred+','+escapeJSONArg("total")+":"+total+"}");
			}
			json.append("\n");
		} else if (method.equalsIgnoreCase("domains")) {
			json.append("{\n"+escapeJSONArg("items")+":[\n");
			String[] domains=FSUtilities.getDomains((SRBFileSystem)davisSession.getRemoteFileSystem());
			for (int i = 0; i < domains.length; i++) {
				if (i>0) json.append(",\n");
				json.append("{"+escapeJSONArg("name")+":"+escapeJSONArg(domains[i])+"}");
			}
			json.append("\n]}");
		} else if (method.equalsIgnoreCase("dynamicobjects")) {
			json.append("{\n"+escapeJSONArg("items")+":[\n");
			Enumeration<JSONObject> dynamicObjects = Davis.getConfig().getDynamicObjects().elements();
			int i = 0;
			while (dynamicObjects.hasMoreElements()) {
				JSONObject dynamicObject = dynamicObjects.nextElement();
				if (i++ > 0) json.append(",\n");
				json.append(dynamicObject.toString()); // No escaping required - the whole dynamic object declaration is sent verbatim
			}
			json.append("\n]}");
		} else if (method.equalsIgnoreCase("resources")) {
			json.append("{\n"+escapeJSONArg("items")+":[\n");
			String[] resources = null;
			if (davisSession.getRemoteFileSystem() instanceof SRBFileSystem)
				resources = FSUtilities.getSRBResources((SRBFileSystem)file.getFileSystem());
			else
				resources = FSUtilities.getIRODSResources((IRODSFileSystem)file.getFileSystem());
			for (int i = 0; i < resources.length; i++) { 
				if (i > 0) json.append(",\n");
				json.append("{"+escapeJSONArg("name")+":"+escapeJSONArg(resources[i])+"}");
			}
			json.append("\n]}");
		} else if (method.equalsIgnoreCase("collectionmetadata")) {
			json.append("{"+escapeJSONArg("items")+":[\n");
			HashMap<String, FileMetadata> files = null;
			if (davisSession.getRemoteFileSystem() instanceof SRBFileSystem)
				{}//###TBD not implemented yet
			else
				files = FSUtilities.getIRODSCollectionMetadata(file);
			if (file != null) {
				FileMetadata[] filesMetadata = files.values().toArray(new FileMetadata[0]);
				for (int i = 0; i < filesMetadata.length; i++) { 
					if (i > 0) json.append("  ,\n");
					json.append("  {"+escapeJSONArg("file")+":"+escapeJSONArg(filesMetadata[i].getName())+","+escapeJSONArg("metadata")+": [\n");
					HashMap<String, ArrayList<String>> metadata = filesMetadata[i].getMetadata();
					String[] names = metadata.keySet().toArray(new String[0]);
					for (int j = 0; j < names.length; j++) {
						if (j > 0) json.append(",\n");
						String name = names[j];
						ArrayList<String> values = metadata.get(name);
						for (int k = 0; k < values.size(); k++) {
							if (k > 0) json.append(",\n");
							json.append("    {"+escapeJSONArg("name")+":"+escapeJSONArg(name)+","+escapeJSONArg("value")+":"+escapeJSONArg(values.get(k))+"}");
						}
					}
					json.append("]}\n");
				}
			}
			json.append("]}");
		} else if (method.equalsIgnoreCase("execbutton")) {
			JSONArray jsonArray = getJSONContent(request);
	    	ArrayList<RemoteFile> fileList = new ArrayList<RemoteFile>();
	    	boolean batch = true;
//	    	try {
	    		batch = batch = getFileList(request, davisSession, fileList, jsonArray);
//	    	} catch (ServletException e) {
//	    		if (!checkClientInSync(response, e))
//	    			return;
//	    	}
//System.err.println("file list="+fileList);
			String buttonName = request.getParameter("button");

	    	JSONObject uiJSON = null;
	    	if (jsonArray != null) 	
	    		uiJSON = (JSONObject)jsonArray.get(0);
	    	if (uiJSON == null) {
	    		Log.log(Log.ERROR, "Internal error servicing dynamic button "+buttonName+" - can't find button name");
    			response.sendError(HttpServletResponse.SC_FORBIDDEN);
    			return;
	    	}
			Log.log(Log.DEBUG, "Executing rule '"+buttonName+"'");
			JSONObject button = Davis.getConfig().getDynamicObject(buttonName);
//System.err.println("button="+button);
			String ruleText = (String)button.get("rule");
			StringBuffer commandLine = new StringBuffer(ruleText);
			commandLine.append("\n");
			JSONArray args = (JSONArray)uiJSON.get("args");
//System.err.println("args="+args);
			JSONObject arg;
			for (int i = 0; i < args.size(); i++) {
				if (i > 0) commandLine.append("%");
				arg = (JSONObject)args.get(i);
				commandLine.append("*").append(arg.get("name")).append("=").append(arg.get("value"));
			}
			if (button.get("location").equals("selection") && fileList.size() > 0){
				if (args.size() > 0) commandLine.append("%");
				commandLine.append("*filelist=");
				for (int i = 0; i < fileList.size(); i++) {
					if (i > 0) commandLine.append(",");
					commandLine.append(fileList.get(i).getAbsolutePath()); //+"/"+fileList.get(i).getName());
				}
			}
			commandLine.append("\n*OUT");
			Log.log(Log.DEBUG, "commandLine="+commandLine);

			//execute rule
//			String rule="passwordRule||msiExecCmd(changePassword,\"*username *password\",null,null,null,*OUT)|nop\n*username="+username+"%*password="+password+"\n*OUT";
//		    System.out.println(rule);
			String notice = null;
			java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(commandLine.toString().getBytes());
		    try {
				java.util.HashMap outputParameters = ((IRODSFileSystem)file.getFileSystem()).executeRule( inputStream );
				Object out = outputParameters.get("*OUT");
				if (out instanceof String){
					notice = (String)out;
				} else if (out instanceof String[]) {
					notice = "Done.";
					String tmp;
					for (String s:(String[])out) {
						if (s!=null) {
							tmp = new String(Base64.decodeBase64(s.getBytes()), "ISO-8859-1").trim().replace("\n", "");
							Log.log(Log.DEBUG, "out="+tmp);
							notice = tmp;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				notice = "Sorry, there is an error with rule execution!";
			}
			
			if (notice != null && notice.length() > 0)
				json.append("{"+escapeJSONArg("notice")+":"+escapeJSONArg(notice)+"}");
			json.append("\n");
			
		} else if (method.equalsIgnoreCase("replicas")) {
			String deleteResource = request.getParameter("delete");
			String replicateResource = request.getParameter("replicate");
	    	ArrayList<RemoteFile> fileList = new ArrayList<RemoteFile>();
//	    	try {
	    		getFileList(request, davisSession, fileList, getJSONContent(request));
//	    	} catch (ServletException e) {
//	    		if (!checkClientInSync(response, e))
//	    			return;
//	    	}

	        Iterator<RemoteFile> iterator = fileList.iterator();
			json.append("{\n"+escapeJSONArg("items")+":[\n");
	        while (iterator.hasNext()) {
	        	file = iterator.next();
	        	if (!file.isDirectory()) {	// Can't get replica info for a directory
	        		if (deleteResource != null) {
	        			Log.log(Log.DEBUG, "Deleting replica at "+deleteResource);
                        if (file.getFileSystem() instanceof SRBFileSystem) {
    	        			Log.log(Log.DEBUG, "Not currently supported by Jargon");
                        }else if (file.getFileSystem() instanceof IRODSFileSystem) {
                        	((IRODSFile)file).deleteReplica(deleteResource);	//Currently ALWAYS returns false!
    	        			Log.log(Log.DEBUG, "Not currently working in Jargon");
                        }
	        		}
	        		if (replicateResource != null) {
	        			Log.log(Log.DEBUG, "Replicating to "+replicateResource);
                        if (file.getFileSystem() instanceof SRBFileSystem) {
 //   	        			Log.log(Log.DEBUG, "Not currently supported by Jargon");
                        	((SRBFile)file).replicate(replicateResource);	
                        } else if (file.getFileSystem() instanceof IRODSFileSystem) {
                        	try {
                        		((IRODSFile)file).replicate(replicateResource);
                        	} catch (IRODSException e) {
                    			String s = e.getMessage();
                    			if (s.contains("IRODS error occured -303"))
                    				s = "Unknown host";
                    			if (s.contains("IRODS error occured -305"))
                    				s = "Can't connect to host";
                    			if (s.contains("IRODS error occured -347"))
                    				s = "Can't connect to host";
                    			Log.log(Log.DEBUG, "Replication failed: "+e.getMessage());
                    			response.sendError(HttpServletResponse.SC_FORBIDDEN, "replication failed: "+s);
                    			return;
                        	}
                        	//   	        			Log.log(Log.DEBUG, "Not currently working in Jargon");
                        }
	        		}
	        		
	        		// Now get replica info
		    		MetaDataRecordList[] rl = null;
		    		String[] selectFieldNames = new String[] {FileMetaData.FILE_REPLICA_NUM, ResourceMetaData.RESOURCE_NAME};
		    		MetaDataSelect selects[] = MetaDataSet.newSelection(selectFieldNames);
		    		rl = file.query(selects);
		    		if (rl != null)
			    		for (int i = 0; i < rl.length; i++) {
							if (i > 0) json.append(",\n");
							json.append("{"+escapeJSONArg("resource")+":"+escapeJSONArg((String)rl[i].getValue(ResourceMetaData.RESOURCE_NAME))+",");
							json.append(escapeJSONArg("number")+":"+escapeJSONArg((String)rl[i].getValue(FileMetaData.FILE_REPLICA_NUM))+"}");
			    		}
	        	}
	        }
			json.append("\n]}");
			
		} else if (method.equalsIgnoreCase("shares")) {
			json.append("{\n"+escapeJSONArg("items")+":[\n");
			if (!(davisSession.getRemoteFileSystem() instanceof IRODSFileSystem)) 
				Log.log(Log.ERROR, "Sharing is only supported for iRODS");
			else {
//				String sharingKey = Davis.getConfig().getSharingKey();
//				if (sharingKey != null) {		
//					MetaDataSelect selectsFile[] = 
//						MetaDataSet.newSelection(new String[] {
//				//				IRODSMetaDataSet.FILE_ACCESS_NAME,
//								//IRODSMetaDataSet.FILE_ACCESS_USER_ID,
//								IRODSMetaDataSet.OWNER,							
//								IRODSMetaDataSet.FILE_NAME,
//								IRODSMetaDataSet.DIRECTORY_NAME
//						});
//					MetaDataCondition conditionsFile[];
//					conditionsFile = new MetaDataCondition[] {
//							MetaDataSet.newCondition(IRODSMetaDataSet.META_DATA_ATTR_NAME, MetaDataCondition.EQUAL, sharingKey),
//							MetaDataSet.newCondition(IRODSMetaDataSet.OWNER, MetaDataCondition.EQUAL, davisSession.getAccount()), 
//					};
//					MetaDataRecordList[] fileDetails = ((IRODSFileSystem)davisSession.getRemoteFileSystem()).query(conditionsFile, selectsFile, DavisUtilities.JARGON_MAX_QUERY_NUM);
					MetaDataRecordList[] fileDetails = getShares(davisSession);
					if (fileDetails == null) 
		    			fileDetails = new MetaDataRecordList[0];	
		 			int i = 0;
		    		for (MetaDataRecordList p:fileDetails) {
		    			String dirName = (String)p.getValue(IRODSMetaDataSet.DIRECTORY_NAME);
		    			String fileName = (String)p.getValue(IRODSMetaDataSet.FILE_NAME);
		    			String sharingURL = (String)p.getValue(IRODSMetaDataSet.META_DATA_ATTR_VALUE);
						if (i++ > 0) json.append(",\n");
						json.append("{"+escapeJSONArg("file")+":\""+FSUtilities.escape(fileName)+"\",");
						json.append(escapeJSONArg("dir")+":\""+FSUtilities.escape(dirName)+"\",");
						json.append(escapeJSONArg("url")+":"+escapeJSONArg(sharingURL)+"}");
		        	}
//				}
			}
			json.append("\n]}");
			
		} else if (method.equalsIgnoreCase("unshareall")) {
			if (!(davisSession.getRemoteFileSystem() instanceof IRODSFileSystem)) 
				Log.log(Log.ERROR, "Sharing is only supported for iRODS");
			else {
				JSONObject jsonObject = null;
				JSONArray jsonArray = getJSONContent(request);
				if (jsonArray != null) {	
					jsonObject = (JSONObject)jsonArray.get(0);
					JSONArray fileNamesArray = (JSONArray)jsonObject.get("files");
					String sharingKey = Davis.getConfig().getSharingKey();
					if (fileNamesArray != null && sharingKey != null)
						for (int i = 0; i < fileNamesArray.size(); i++) {
							String name = (String)fileNamesArray.get(i);
							name = name.replaceAll("\\+", "%2B");
							name = URLDecoder.decode(name, "UTF-8");
							if (name.trim().length() == 0)
								continue;	// If for any reason name is "", we MUST skip it because that's equivalent to home!   	 
							file = getRemoteFile(name, davisSession);
							String s = share(davisSession, file, false);
							if (s != null) {
								response.sendError(HttpServletResponse.SC_FORBIDDEN, s);
								return;
							}
						}			
				} else
					throw new ServletException("Internal error reading file list: error parsing JSON");			
			}
			
		} else if (method.equalsIgnoreCase("share")) {
			if (!(davisSession.getRemoteFileSystem() instanceof IRODSFileSystem)) 
				Log.log(Log.ERROR, "Sharing is only supported for iRODS");
			else {
				String action = request.getParameter("action");
				String sharingKey = Davis.getConfig().getSharingKey();
		    	ArrayList<RemoteFile> fileList = new ArrayList<RemoteFile>();
	//	    	try {
		    		getFileList(request, davisSession, fileList, getJSONContent(request));
	//	    	} catch (ServletException e) {
	//	    		if (!checkClientInSync(response, e))
	//	    			return;
	//	    	}
	
		        Iterator<RemoteFile> iterator = fileList.iterator();
		        while (sharingKey != null && iterator.hasNext()) {
		        	file = iterator.next();
					MetaDataSelect selectsFile[] = 
						MetaDataSet.newSelection(new String[] {
								IRODSMetaDataSet.OWNER,							
						});
					String s= null;
					try {
						MetaDataRecordList[] details = ((IRODSFile)file).query(selectsFile);
			 			if (details == null) 
			    			details = new MetaDataRecordList[0];	
			    		for (MetaDataRecordList p:details) {
	//System.err.println("##########p="+p);
			    			String owner = (String)p.getValue(IRODSMetaDataSet.OWNER);
			    			if (!owner.equals(davisSession.getAccount())) 
			    				s = "you are not the owner";
			        	}
					} catch (IOException e) {
						s = "Internal error: can't determine the owner of the resource";
						Log.log(Log.ERROR, s);
						Log.log(Log.ERROR, e);
					}
	       	
					if (s == null)
						s = share(davisSession, file, action.equals("share"));
		        	if (s != null) {
	        			response.sendError(HttpServletResponse.SC_FORBIDDEN, s);
	        			return;
		        	}
		        }
			}
			
		} else if (method.equalsIgnoreCase("userlist")) {
			json.append("{\n"+escapeJSONArg("items")+":[\n");
			if (davisSession.getRemoteFileSystem() instanceof SRBFileSystem){
				String[] users=FSUtilities.getUsernamesByDomainName((SRBFileSystem)davisSession.getRemoteFileSystem(),request.getParameter("domain"));
				for (int i = 0; i < users.length; i++) {
					if (i>0) json.append(",\n");
					json.append("{"+escapeJSONArg("name")+":"+escapeJSONArg(users[i])+"}");
				}
			}else if (davisSession.getRemoteFileSystem() instanceof IRODSFileSystem){
				String[] users=FSUtilities.getUsernames((IRODSFileSystem)davisSession.getRemoteFileSystem());
				for (int i = 0; i < users.length; i++) {
					if (i>0) json.append(",\n");
					json.append("{"+escapeJSONArg("name")+":"+escapeJSONArg(users[i])+"}");
				}
			}
			json.append("\n]}");
		} else if (method.equalsIgnoreCase("debug")) { 
			try {
//				TestingPropertiesHelper testingPropertiesHelper = new TestingPropertiesHelper();
//				Properties testingProperties = testingPropertiesHelper.getTestProperties();
//				AssertionHelper assertionHelper = new AssertionHelper();
//				String IRODS_TEST_SUBDIR_PATH = "DeleteTest";
//				// test tuning variables
//				String testFileNamePrefix = "del1200filethenquery";
//				String testFileExtension = ".txt";
//				String deleteCollectionSubdir = IRODS_TEST_SUBDIR_PATH + "/del1200noforcethenquerydir";
//				int numberOfTestFiles = 1600;
//
//				System.err.println("properties="+testingProperties.toString());
//				// create collection to zap, this is all just setup
//				String deleteCollectionAbsPath = testingPropertiesHelper.buildIRODSCollectionAbsolutePathFromTestProperties(testingProperties, deleteCollectionSubdir);
//				IrodsInvocationContext invocationContext = testingPropertiesHelper.buildIRODSInvocationContextFromTestProperties(testingProperties);
//				IcommandInvoker invoker = new IcommandInvoker(invocationContext);
//				ImkdirCommand imkdrCommand = new ImkdirCommand();
//				imkdrCommand.setCollectionName(deleteCollectionAbsPath);
//				System.err.println("user home="+System.getProperty("user.home"));
//				System.err.println("deleteCollectionAbsPath="+deleteCollectionAbsPath);
//				invoker.invokeCommandAndGetResultAsString(imkdrCommand);
//
//				IputCommand iputCommand = new IputCommand();
//				String genFileName = "";
//				String fullPathToTestFile = "";
//
//				// generate a number of files in the subdir
//				for (int i = 0; i < numberOfTestFiles; i++) {
//					genFileName = testFileNamePrefix + String.valueOf(i) + testFileExtension;
//					fullPathToTestFile = FileGenerator.generateFileOfFixedLengthGivenName(testingProperties.getProperty(GENERATED_FILE_DIRECTORY_KEY) + "/", genFileName, 1);
//
//					iputCommand.setLocalFileName(fullPathToTestFile);
//					iputCommand.setIrodsFileName(deleteCollectionAbsPath);
//					iputCommand.setForceOverride(true);
//					invoker.invokeCommandAndGetResultAsString(iputCommand);
//				}
//
//				// now try and delete the collecton **** code to replicate Rowan's issue *****
//				IRODSAccount account = testingPropertiesHelper.buildIRODSAccountFromTestProperties(testingProperties);
//				IRODSFileSystem irodsFileSystem = new IRODSFileSystem(account);
//				IRODSFile irodsFile = new IRODSFile(irodsFileSystem, testingPropertiesHelper.buildIRODSCollectionAbsolutePathFromTestProperties(testingProperties, deleteCollectionSubdir));
//
//				System.err.println("** Parent file before="+irodsFile.getParentFile());					
//				boolean deleteResult = irodsFile.delete(false);
//				System.err.println("** Parent file after="+irodsFile.getParentFile());					
//				System.err.println("** Parent file exists="+irodsFile.getParentFile().exists());					
//
//
//				//irodsFile.close();
//				// now do a query
//
//
//				String[] fields = { IRODSMetaDataSet.FILE_NAME,	IRODSMetaDataSet.DIRECTORY_NAME };
//
//
//				MetaDataSelect[] select = IRODSMetaDataSet.newSelection(fields);
//				MetaDataCondition[] condition = new MetaDataCondition[1];
//				condition[0] = IRODSMetaDataSet.newCondition(IRODSMetaDataSet.DIRECTORY_NAME,
//						MetaDataCondition.EQUAL, irodsFile.getAbsolutePath());
//				MetaDataRecordList[] fileList = irodsFileSystem.query(condition, select, 100, Namespace.FILE, false);
//
//
//				irodsFileSystem.close();
//				TestCase.assertTrue("delete was unsuccessful", deleteResult);
//				assertionHelper.assertIrodsFileOrCollectionDoesNotExist(deleteCollectionAbsPath);
				
//				IRODSCommandsDeleteTest tester = new IRODSCommandsDeleteTest();
//				tester.setUpBeforeClass();
//				tester.testDelete1200ByDeletingCollectionNoForceThenIssueGenQuery();
			} catch (Exception e) {
				System.err.println("************ Exception in debug method handler: "+e);
				e.printStackTrace();
			}
		} else if (method.equalsIgnoreCase("logout")) { 
//			Cookie[] cookies = request.getCookies();
//			if (cookies != null) // Delete shib cookies if present
//				for (Cookie cookie:cookies)
//					if (cookie.getName().startsWith("_shibstate") || cookie.getName().startsWith("_shibsession") || cookie.getName().startsWith("_saml_idp")) {
//						cookie.setPath("/");
//						cookie.setMaxAge(0);	// Browser should delete cookie
//						response.addCookie(cookie);
//					}
			HttpSession session = request.getSession(true);
			request.getSession().removeAttribute(Davis.FORMAUTHATTRIBUTENAME); // Discard auth attribute (if there is one)
			session.invalidate();
			AuthorizationProcessor.getInstance().destroy(davisSession.getSessionID());
			
			davisSession.getClientInstances().clear(); // destroy cache for the session (all browser windows) 
			
			Log.log(Log.INFORMATION, "logout from: "+request.getRemoteAddr());
			if (request.isSecure()) 
				json.append("{"+escapeJSONArg("redirect")+":"+escapeJSONArg(request.getRequestURI())+"}");	// Return to login page for original url
			else {
				String returnURL = Davis.getConfig().getLogoutReturnURL();
				if (returnURL == null || returnURL.length() == 0)
					returnURL = "";
				else
					returnURL = "?return="+returnURL;
				json.append("{"+escapeJSONArg("redirect")+":"+escapeJSONArg("https://"+request.getServerName()+"/Shibboleth.sso/Logout"+returnURL)+"}");
			}
		}
		
		ServletOutputStream op = null;
		try {
			 op = response.getOutputStream();
		} catch (EOFException e) {
			Log.log(Log.WARNING, "EOFException when preparing to send servlet response - client probably disconnected");
			return;
		}
		byte[] buf = json.toString().getBytes();
		Log.log(Log.DEBUG, "output(" + buf.length + "):\n" + json);
		op.write(buf);
		op.flush();
		op.close();
	}
	
	private MetaDataRecordList[] getShares(DavisSession davisSession) throws IOException{
		
		return getShares(davisSession, null, null);
	}

	private MetaDataRecordList[] getShares(DavisSession davisSession, String directory, String fileName) throws IOException{
		
		String sharingKey = Davis.getConfig().getSharingKey();
		if (sharingKey == null)
			return null;		

		MetaDataSelect selectsFile[] = 
		MetaDataSet.newSelection(new String[] {
//				IRODSMetaDataSet.FILE_ACCESS_NAME,
				//IRODSMetaDataSet.FILE_ACCESS_USER_ID,
				IRODSMetaDataSet.OWNER,							
				IRODSMetaDataSet.FILE_NAME,
				IRODSMetaDataSet.META_DATA_ATTR_VALUE,
				IRODSMetaDataSet.DIRECTORY_NAME
		});
		MetaDataCondition conditionsFile[];
		if (directory == null)
			conditionsFile = new MetaDataCondition[] {
					MetaDataSet.newCondition(IRODSMetaDataSet.META_DATA_ATTR_NAME, MetaDataCondition.EQUAL, sharingKey),
					MetaDataSet.newCondition(IRODSMetaDataSet.OWNER, MetaDataCondition.EQUAL, davisSession.getAccount()), 
			};
		else
			conditionsFile = new MetaDataCondition[] {
				MetaDataSet.newCondition(IRODSMetaDataSet.META_DATA_ATTR_NAME, MetaDataCondition.EQUAL, sharingKey),
				MetaDataSet.newCondition(IRODSMetaDataSet.OWNER, MetaDataCondition.EQUAL, davisSession.getAccount()), 
				MetaDataSet.newCondition(IRODSMetaDataSet.FILE_NAME, MetaDataCondition.EQUAL, fileName), 
				MetaDataSet.newCondition(IRODSMetaDataSet.DIRECTORY_NAME, MetaDataCondition.EQUAL, directory), 
			};
		return ((IRODSFileSystem)davisSession.getRemoteFileSystem()).query(conditionsFile, selectsFile, DavisUtilities.JARGON_MAX_QUERY_NUM);
	}
	
	private String share(DavisSession davisSession, RemoteFile file, boolean share) {
		
    	if (!file.isDirectory()) {	// Can't share a directory
			String sharingKey = Davis.getConfig().getSharingKey();
    		String username = Davis.getConfig().getSharingUser();
			if (username != null) {
				GeneralFileSystem fileSystem = file.getFileSystem();				
				String permission = "r";
				if (!share)
					permission = "n";
				
				try {
					// Add/remove read permission for share user
					if (fileSystem instanceof SRBFileSystem) {
						Log.log(Log.WARNING, "sharing is not implemented for SRB servers");
					} else if (fileSystem instanceof IRODSFileSystem) {
						Log.log(Log.DEBUG, "change permission for "+username+" to "+permission+" for sharing");
						((IRODSFile)file).changePermissions(permission, username, false);
					}
				} catch (IOException e){
					String s = e.toString();
					if (e.getCause() != null)
						s += " : "+e.getCause().getMessage();
		        	Log.log(Log.DEBUG, "Set permissions failed for sharing: "+s);
		        	if (e.getMessage().endsWith("-818000")) 
		        		s = "you don't have permission"; // irods error -818000 
		        	if (e.getMessage().endsWith("IRODS error occured msg")) {
		        		s = "can't unshare "+file.getName();  
		        		Log.log(Log.ERROR, s+" due to iRODS error");
		        	}
		        	if (e.getMessage().endsWith("-827000")) {
		        		s = "Internal error: sharing user account doesn't exist";
		        		Log.log(Log.ERROR, s);
		        	}
        			return s;
				}
				
				try {
					// Add/remove share metadata
					if (fileSystem instanceof SRBFileSystem) {
						Log.log(Log.ERROR,"Sharing not implemented for SRB");
					}else 
					if (fileSystem instanceof IRODSFileSystem) {
						if (share) {
							MetaDataRecordList[] fileDetails = getShares(davisSession, file.getParent(), file.getName());
							if (!(fileDetails == null || fileDetails.length == 0)) {
								Log.log(Log.DEBUG, file.getPath()+" is already shared - ignoring");
								return null;
							}
						}
						Log.log(Log.DEBUG, "removing metadata field '"+sharingKey+"' for "+username+" to end sharing");
						((IRODSFile)file).deleteMetaData(new String[]{sharingKey,"%","%"});
						if (share) {
							String randomString = Long.toHexString(random.nextLong());
							String shareURL = Davis.getConfig().getSharingURLPrefix()+'/'+randomString+'/'+DavisUtilities.encodeFileName(file.getName());
							String[] metadata = new String[] {sharingKey, shareURL, ""};		    	
							Log.log(Log.DEBUG, "adding share URL '"+shareURL+"' to metadata field '"+sharingKey+"' for "+username+" to enable sharing");
							((IRODSFile)file).modifyMetaData(metadata);
						}
					}
				} catch (IOException e){
		        	Log.log(Log.DEBUG, "Set metadata failed for sharing: "+e);
		        	String s = e.getMessage();
		        	if (s.endsWith("-818000")) 
		        		s = "you don't have permission"; // irods error -818000 
        			return s;
				}
			}
    	}
    	return null;
	}
	
	private boolean isPermInherited(RemoteFile file) throws IOException {

            String[] selectFieldNames = {
                    SRBMetaDataSet.DIRECTORY_LINK_NUMBER,
            };

            MetaDataSelect inheritanceQuery[] = MetaDataSet.newSelection( selectFieldNames );
            MetaDataRecordList[] list = file.query(inheritanceQuery);

            if(list != null)
            {
                MetaDataRecordList r = list[0];
                String result  = r.getValue(r.getFieldIndex(SRBMetaDataSet.DIRECTORY_LINK_NUMBER)).toString();
                return result.equals("1");
            }

            return false;
    }
	private void setSticky(RemoteFile file, boolean flag, boolean recursive) throws IOException{
		if (file.getFileSystem() instanceof SRBFileSystem) {
			MetaDataField mdf=SRBMetaDataSet.getField(SRBMetaDataSet.DIRECTORY_LINK_NUMBER);
			MetaDataRecordList rl = new SRBMetaDataRecordList(mdf,String.valueOf(flag));
			file.modifyMetaData(rl);
		} else if (file.getFileSystem() instanceof IRODSFileSystem) {
			((IRODSFile)file).changePermissions(flag?"inherit":"noinherit", "", recursive);
		}	
	}

    private boolean error = false;

    private boolean iRODSSetPermission(IRODSFile file, String permission, String username) {
 
    	if (file.isDirectory()) {
    		Log.log(Log.DEBUG, "(perm)entering dir "+file.getAbsolutePath());
    		String[] fileList=file.list();
    		Log.log(Log.DEBUG, "(perm)entering dir has children number: "+fileList.length);
    		if (fileList.length > 0) 
        		for (int i=0; i<fileList.length; i++){
        			Log.log(Log.DEBUG, "(perm)entering child "+fileList[i]);
    				error = error || !iRODSSetPermission(new IRODSFile(file,fileList[i]), permission, username);
        		}
    	}
		Log.log(Log.DEBUG, "changing permission of "+file.getAbsolutePath());
		try {
			file.changePermissions(permission, username, false);
		} catch (IOException e) {
			if (!error)
				Log.log(Log.DEBUG, "recursive iRODSSetPermission caught: "+e); // Only log first error
			error = true;
		}
    	
    	return !error;
    }
	
    private static final Map trackers = new HashMap();	// Set of trackers currently transferring
    
    private static void copy(Tracker tracker, InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            tracker.incrementBytesReceived(n);
//   Log.log(Log.DEBUG, "sent "+n);
//            try { 			// Simulate slow connection.
//                 Thread.sleep(3);
//            } catch (InterruptedException ex) {
//                throw new RuntimeException(ex);
//            }
        }
    }
    
    private static Tracker createTracker(long size) {
    	Tracker tracker = new Tracker(size);
    	trackers.put(tracker.getId(), tracker);
    	return tracker;
    }
    
    private static Tracker getTracker(long id, boolean verifyComplete) {
        synchronized(trackers) {
            Tracker tracker = (Tracker) trackers.get(id);
            if (verifyComplete) {
                if (tracker == null) {
                    throw new IllegalArgumentException("No file found with id: \"" + id + "\".");
                }
                if (!tracker.isComplete()) {
                    throw new IllegalArgumentException("Cannot store file with id: \"" + id 
                            + "\" because it has not yet completed uploading.");
                }
            }
            return tracker;
        }
    }
    
    public static class Tracker {
        
        public static final int OK = 0;
        public static final int ERROR_FILE_NO_FILE = 1;
        public static final int ERROR_FILE_INVALID = 2;
        public static final int ERROR_FILE_OVERSIZE = 3;
        private static long autoId = -1;
        
        private long bytesReceived;
        private long size;
        private boolean complete;
        private long id;
        private FileItem fileItem;
        private int error = OK;
        
        Tracker(long size) {
        	super();
        	this.id = getAutoId();
        	this.size = size;
        }
        
        synchronized private long getAutoId() {
        	
        	return ++autoId;
        }
        
        public long getBytesReceived() {
            return bytesReceived;
        }
        
        public int getComplete() {
            return (int) (bytesReceived * 100 / size);
        }
        
        public int getError() {
            return error;
        }
        
        public long getId() {
            return id;
        }
        
        public String getName() {
            return fileItem.getName();
        }
        
        public InputStream getInputStream() 
        throws IOException {
            return fileItem.getInputStream();
        }
        
        public long getSize() {
            return size;
        }
        
        public boolean isComplete() {
            return complete;
        }
        
        public void incrementBytesReceived(long increment) {
            this.bytesReceived += increment;
        }
        
        public void setBytesReceived(long bytesReceived) {
            this.bytesReceived = bytesReceived;
        }
        
        private void setFileItem(FileItem fileItem) {
            this.fileItem = fileItem;
        }
        
        public void setError(int error) {
            this.error = error; 
        }
        
        public void setComplete() {
            complete = true;
        }
    }
}
