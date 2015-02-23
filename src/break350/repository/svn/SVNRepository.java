package break350.repository.svn;

import java.io.File;
import java.util.List;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import break350.repository.Repository;

public class SVNRepository implements Repository {
	private String url = "svn://localhost/rep";
	private String userName = "";
	private String userPassword = "";
	private String root = "test/checkout/";

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public SVNClientManager getSVNClientManager() throws Exception {
		SVNURL url = SVNURL.parseURIEncoded(this.url);

		org.tmatesoft.svn.core.io.SVNRepository repository = SVNRepositoryFactory
				.create(url);
		ISVNOptions myOptions = SVNWCUtil.createDefaultOptions(true);
		// provide svn username and password
		// username = name used to connect to svn
		// password = password used to connect to svn
		ISVNAuthenticationManager myAuthManager = SVNWCUtil
				.createDefaultAuthenticationManager(this.userName,
						this.userPassword);
		repository.setAuthenticationManager(myAuthManager);
		// clientManager will be used to get different kind of svn clients
		// instances to do different activities
		// like update, commit, view diff etc.
		SVNClientManager clientManager = SVNClientManager.newInstance(
				myOptions, myAuthManager);

		return clientManager;
	}

	private void commitToSvn(SVNClientManager clientManager)
			throws SVNException {
		SVNCommitClient commitClient = clientManager.getCommitClient();
		File fileToCheckin = new File("LocalDir/SampleFileFolder/SampleFile1");
		boolean recursive = true;
		SVNCommitInfo importInfo = commitClient
				.doImport(
						fileToCheckin,
						SVNURL.parseURIDecoded("<path at which we want to check-in the file>"),
						"testing svn kit integration", recursive);
		System.out.println(importInfo.getNewRevision());
	}

	private void exportFromSvn(SVNClientManager clientManager)
			throws SVNException {
		SVNUpdateClient updateClient = clientManager.getUpdateClient();
		SVNURL url = SVNURL.parseURIDecoded(this.url);
		// destination path
		File dstPath = new File(root);
		// the revision number which should be looked upon for the file path
		SVNRevision pegRevision = SVNRevision.create(-1);
		// the revision number which is required to be exported.
		SVNRevision revision = SVNRevision.create(-1);
		// if there is any special character for end of line (in the file) then
		// it is required. For our use case, //it can be null, assuming there
		// are no special characters. In this case the OS specific EoF style
		// will //be assumed
		String eolStyle = null;
		// this would force the operation
		boolean force = true;
		// Till what extent under a directory, export is required, is determined
		// by depth. INFINITY means the whole subtree of that directory will be
		// exported
		SVNDepth recursive = SVNDepth.INFINITY;
		updateClient.doExport(url, dstPath, pegRevision, revision, eolStyle,
				force, recursive);
	}

	private org.tmatesoft.svn.core.io.SVNRepository getRepository()
			throws Exception {
		SVNURL url = SVNURL.parseURIEncoded(this.url);

		org.tmatesoft.svn.core.io.SVNRepository repository = SVNRepositoryFactory
				.create(url);

		ISVNAuthenticationManager authManager = SVNWCUtil
				.createDefaultAuthenticationManager(userName, userPassword);
		repository.setAuthenticationManager(authManager);

		SVNNodeKind nodeKind = repository.checkPath("", -1);

		if (nodeKind == SVNNodeKind.NONE) {
			SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN,
					"No entry at URL ''{0}''", url);
			throw new SVNException(err);
		} else if (nodeKind == SVNNodeKind.FILE) {
			SVNErrorMessage err = SVNErrorMessage
					.create(SVNErrorCode.UNKNOWN,
							"Entry at URL ''{0}'' is a file while directory was expected",
							url);
			throw new SVNException(err);
		}
		// repository.checkout(-1, "", true, new
		// SVNReplicationEditor(repository,
		// repository.getCommitEditor("sdafsdaf", new ISVNWorkspaceMediator)));
		return repository;
	}

	@Override
	public void removeFiles(List<String> files) {
		setupLibrary();
		try {
			org.tmatesoft.svn.core.io.SVNRepository repository = getRepository();

			long latestRevision = repository.getLatestRevision();
			System.out
					.println("Repository latest revision (before committing): "
							+ latestRevision);
			ISVNEditor editor = repository.getCommitEditor("directory deleted:"
					+ files, null);
			for (String file : files) {
				SVNCommitInfo commitInfo = deleteDir(editor, file);
				System.out.println("The entry was deleted: " + commitInfo);
			}

			latestRevision = repository.getLatestRevision();
			System.out
					.println("Repository latest revision (after committing): "
							+ latestRevision);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static SVNCommitInfo deleteDir(ISVNEditor editor, String dirPath)
			throws SVNException {
		editor.openRoot(-1);
		editor.deleteEntry(dirPath, -1);
		editor.closeDir();
		return editor.closeEdit();
	}

	public static void setupLibrary() {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}

	@Override
	public void exportFromRemoteRepository() {
		try {
			exportFromSvn(getSVNClientManager());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
