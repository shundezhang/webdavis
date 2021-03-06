package webdavis;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.sdsc.grid.io.FileFactory;
import edu.sdsc.grid.io.GeneralFile;
import edu.sdsc.grid.io.RemoteFile;
import edu.sdsc.grid.io.RemoteFileSystem;
import edu.sdsc.grid.io.irods.IRODSFile;
import edu.sdsc.grid.io.irods.IRODSFileSystem;
import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.srb.SRBFileSystem;


/**
 * Default implementation of a handler for requests using the WebDAV
 * COPY method.
 *
 * @author Eric Glass
 */
public class DefaultCopyHandler extends AbstractHandler {

    /**
     * Services requests which use the WebDAV COPY method.
     * This implementation copies the source file to the destination.
     * <br>
     * If the source file does not exist, a 404 (Not Found) error is sent
     * to the client.
     * <br>
     * If the destination is not specified, a 400 (Bad Request) error
     * is sent to the client.
     * <br>
     * If the source and destination specify the same resource, a 403
     * (Forbidden) error is sent to the client.
     * <br>
     * If the destination already exists, and the client has sent the
     * "Overwrite" request header with a value of "T", then the request
     * succeeds and the file is overwritten.  If the "Overwrite" header is
     * not provided, a 412 (Precondition Failed) error is sent to the client.
     *
     * @param request The request being serviced.
     * @param response The servlet response.
     * @param auth The user's authentication information.
     * @throws SerlvetException If an application error occurs.
     * @throws IOException If an IO error occurs while handling the request.
     */
    public void service(HttpServletRequest request,
            HttpServletResponse response, DavisSession davisSession)
                    throws ServletException, IOException {
        RemoteFile file = getRemoteFile(request, davisSession);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String destination = getRemoteURL(request,
                request.getHeader("Destination"));
        if (destination == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        RemoteFile destinationFile = getRemoteFile(destination, davisSession);
        if (destinationFile.equals(file)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    DavisUtilities.getResource(DefaultCopyHandler.class,
                            "sameResource", null, request.getLocale()));
            return;
        }
        int result = checkLockOwnership(request, destinationFile);
        if (result != HttpServletResponse.SC_OK) {
            response.sendError(result);
            return;
        }
        result = checkConditionalRequest(request, destinationFile);
        if (result != HttpServletResponse.SC_OK) {
            response.sendError(result);
            return;
        }
        LockManager lockManager = getLockManager();
        if (lockManager != null) {
            destinationFile =lockManager.getLockedResource(destinationFile,
            		davisSession);
        }
        boolean overwritten = false;
        if (destinationFile.exists()) {
            if ("T".equalsIgnoreCase(request.getHeader("Overwrite"))) {
                destinationFile.delete();
                overwritten = true;
            } else {
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
                return;
            }
        }
        if (file.getFileSystem() instanceof SRBFileSystem) {
        	((SRBFile)file).setResource(davisSession.getDefaultResource());
        	((SRBFile)destinationFile).setResource(davisSession.getDefaultResource());
         }else if (file.getFileSystem() instanceof IRODSFileSystem) {
        	((IRODSFile)file).setResource(davisSession.getDefaultResource());
        	((IRODSFile)destinationFile).setResource(davisSession.getDefaultResource());
        }
        copyTo(file,destinationFile,davisSession);
        response.setStatus(overwritten ? HttpServletResponse.SC_NO_CONTENT :
                HttpServletResponse.SC_CREATED);
        response.flushBuffer();
    }

	private void copyTo(RemoteFile sourceFile, RemoteFile destinationFile, DavisSession davisSession) throws IOException {
		if (sourceFile.isFile()){
	        if (destinationFile.getFileSystem() instanceof SRBFileSystem) {
	        	((SRBFile)destinationFile).setResource(davisSession.getDefaultResource());
	         }else if (destinationFile.getFileSystem() instanceof IRODSFileSystem) {
	        	((IRODSFile)destinationFile).setResource(davisSession.getDefaultResource());
	        }
			sourceFile.copyTo(destinationFile);
		}else if (sourceFile.isDirectory()){
			  //recursive copy
			String fileList[] = sourceFile.list();
			
			destinationFile.mkdir();
			if (fileList != null) {
				for (int i=0;i<fileList.length;i++) {
					if (sourceFile.getFileSystem() instanceof SRBFileSystem){
						copyTo(new SRBFile( (SRBFile)sourceFile,fileList[i]), 
								new SRBFile( (SRBFile)destinationFile,  fileList[i]),davisSession);
					}else if (sourceFile.getFileSystem() instanceof IRODSFileSystem){
						copyTo(new IRODSFile( (IRODSFile)sourceFile,fileList[i]), 
								new IRODSFile( (IRODSFile)destinationFile,  fileList[i]),davisSession);
						
					}
			    }
			}

		}
		
	}

}
