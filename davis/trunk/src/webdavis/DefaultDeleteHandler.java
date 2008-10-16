package webdavis;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.sdsc.grid.io.RemoteFile;
import edu.sdsc.grid.io.RemoteFileSystem;

/**
 * Default implementation of a handler for requests using the HTTP DELETE
 * method.
 *
 * @author Eric Glass
 */
public class DefaultDeleteHandler extends AbstractHandler {

    /**
     * Services requests which use the HTTP DELETE method.
     * This implementation deletes the specified file
     * <br>
     * If the specified file does not exist, a 404 (Not Found) error is
     * sent to the client.
     *
     * @param request The request being serviced.
     * @param response The servlet response.
     * @param auth The user's authentication information.
     * @throws ServletException If an application error occurs.
     * @throws IOException If an IO error occurs while handling the request.
     */
    public void service(HttpServletRequest request,
            HttpServletResponse response, RemoteFileSystem rfs)
                    throws ServletException, IOException {
    	RemoteFile file = getRemoteFile(request, rfs);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
//        int result = checkLockOwnership(request, file);
//        if (result != HttpServletResponse.SC_OK) {
//            response.sendError(result);
//            return;
//        }
        int result = checkConditionalRequest(request, file);
        if (result != HttpServletResponse.SC_OK) {
            response.sendError(result);
            return;
        }
//        LockManager lockManager = getLockManager();
//        if (lockManager != null) {
//            file = lockManager.getLockedResource(file, auth);
//        }
        file.delete();
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        response.flushBuffer();
    }

}
