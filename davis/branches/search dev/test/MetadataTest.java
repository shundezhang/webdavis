import java.io.FileNotFoundException;
import java.io.IOException;

import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

import webdavis.FSUtilities;

import edu.sdsc.grid.io.FileFactory;
import edu.sdsc.grid.io.irods.IRODSAccount;
import edu.sdsc.grid.io.irods.IRODSFile;
import edu.sdsc.grid.io.irods.IRODSFileSystem;
import edu.sdsc.grid.io.srb.SRBAccount;
import edu.sdsc.grid.io.srb.SRBFileSystem;


public class MetadataTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
//		SRBFileSystem fileSystem = (SRBFileSystem) FileFactory.newFileSystem( new SRBAccount( ) );
//		System.out.println(fileSystem);
//		
////		Object[] domains=FSUtilities.getDomains(fileSystem);
////		for (Object s:domains) System.out.println("domain:"+s);
////		System.out.println("================");
////		Object[] usernames=FSUtilities.getUsernamesByDomainName(fileSystem, (String) domains[domains.length-1]);
////		for (Object s:usernames) System.out.println("user:"+s);
////		fileSystem.close();
//		System.out.println("fileSystem zone:"+fileSystem.getMcatZone());
////		String[] res=FSUtilities.getSRBResources(fileSystem, "srb.sapac.edu.au");
//		String[] res=FSUtilities.getAvailableResource(fileSystem);
//		for (String s:res) System.out.println("res:"+s);
		
		IRODSAccount account=new IRODSAccount("ngspare.sapac.edu.au", 5544, "shunde", null, "/ngspare.sapac.edu.au/home/shunde", "ngspare.sapac.edu.au", "datares" );
		GlobusCredential gCert;
		try {
			gCert = GlobusCredential.getDefaultCredential();
			GSSCredential cert = new GlobusGSSCredentialImpl(gCert, GSSCredential.INITIATE_AND_ACCEPT);
			account.setGSSCredential(cert);
		} catch (GlobusCredentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GSSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IRODSFileSystem fileSystem = new IRODSFileSystem( account );
//		IRODSFileSystem fileSystem = (IRODSFileSystem) FileFactory.newFileSystem( account );
		
		System.out.println(fileSystem);
		System.out.println(new IRODSFile(fileSystem,"/"));
		String[] dirs=new IRODSFile(fileSystem,"/").list();
		System.out.println(dirs);
		for (String s:dirs) System.out.println("dirs:"+s);
		System.out.println("fileSystem zone:"+((IRODSAccount)fileSystem.getAccount()).getZone());
		String[] res=FSUtilities.getIRODSResources(fileSystem, "ngspare.sapac.edu.au");
		for (String s:res) System.out.println("res:"+s);

	}

}
