package webdavis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.irods.jargon.core.pub.io.IRODSFile;

public class CachedFile {
	private long length;
	private boolean isDir;
	private long lastModified;
	private boolean canWrite;
	private String canonicalPath;
	private HashMap<String, ArrayList<String>> metadata;
	private File irodsFile;
	
//	public CachedFile(RemoteFileSystem rfs, String path, String filename) throws NullPointerException {
//		super(rfs, path, filename);
//	}
//
//	public CachedFile(RemoteFileSystem rfs, String filePath) throws NullPointerException {
//		super(rfs, filePath);
//	}
	
	public CachedFile(File file){
		this.irodsFile=file;
	}

	public String getResource() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void replicate(String arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	public boolean canWrite() {
		// TODO Auto-generated method stub
		return irodsFile.canWrite();
	}

	public String getName() {
		// TODO Auto-generated method stub
		return irodsFile.getName();
	}

	public boolean isDirectory() {
		// TODO Auto-generated method stub
		return irodsFile.isDirectory();
	}

	public boolean isFile() {
		// TODO Auto-generated method stub
		return irodsFile.isFile();
	}

	public boolean isHidden() {
		// TODO Auto-generated method stub
		return irodsFile.isHidden();
	}

	public long lastModified() {
		// TODO Auto-generated method stub
		return irodsFile.lastModified();
	}

	public long length() {
		// TODO Auto-generated method stub
		return irodsFile.length();
	}

	public String getCanonicalPath() throws IOException {
		return irodsFile.getCanonicalPath();
	}

	public String getSharingValue() {
		String sharingValue = "";
		String sharingKey = Davis.getConfig().getSharingKey();
		if (metadata != null && sharingKey != null) {
			ArrayList<String> values = metadata.get(sharingKey);
			if (values != null)
				sharingValue = values.get(0);
		}
		return sharingValue;
	}
	
	public void setMetadata(HashMap<String, ArrayList<String>> metadata) {
		this.metadata = metadata;
	}
	
	public HashMap<String, ArrayList<String>> getMetadata() {
		return metadata;
	}
	
	public File getFile(){
		return this.irodsFile;
	}

	public String getParent() {
		// TODO Auto-generated method stub
		return irodsFile.getParent();
	}
}
