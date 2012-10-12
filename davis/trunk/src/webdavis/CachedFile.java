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
	private IRODSFile irodsFile;
	
	public CachedFile(RemoteFileSystem rfs, String path, String filename) throws NullPointerException {
		super(rfs, path, filename);
	}

	public CachedFile(RemoteFileSystem rfs, String filePath) throws NullPointerException {
		super(rfs, filePath);
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
		return this.canWrite;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	public boolean isDirectory() {
		// TODO Auto-generated method stub
		return isDir;
	}

	@Override
	public boolean isFile() {
		// TODO Auto-generated method stub
		return !isDir;
	}

	@Override
	public boolean isHidden() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long lastModified() {
		// TODO Auto-generated method stub
		return this.lastModified;
	}

	@Override
	public long length() {
		// TODO Auto-generated method stub
		return length;
	}
	public void setLength(long length){
		this.length=length;
	}

	@Override
	public boolean setLastModified(long arg0) {
		// TODO Auto-generated method stub
		this.lastModified=arg0;
		return true;
	}
	
	public void setDirFlag(boolean isDir) {
		this.isDir=isDir;
	}
	public void setCanWriteFlag(boolean canWrite){
		this.canWrite=canWrite;
	}

	public String getCanonicalPath() {
		return getPath()+File.separator+getName();
	}

	public void setCanonicalPath(String canonicalPath) {
		this.canonicalPath = canonicalPath;
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
}
