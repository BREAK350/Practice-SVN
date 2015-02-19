package break350.repository.svn;

import java.util.List;

import org.tmatesoft.svn.core.SVNCommitInfo;
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
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import break350.repository.Repository;

public class SVNRepository implements Repository {
	private String url = "svn://localhost/rep";
	private String userName = "";
	private String userPassword = "";

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
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

			ISVNEditor editor = repository.getCommitEditor("directory deleted",
					null);
			for (String file : files) {
				SVNCommitInfo commitInfo = deleteDir(editor, file);
				System.out.println("The directory was deleted: " + commitInfo);
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

	private static void setupLibrary() {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}
}
