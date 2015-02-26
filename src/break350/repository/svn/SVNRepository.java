package break350.repository.svn;

import java.io.File;
import java.util.List;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
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
import org.tmatesoft.svn.core.wc.SVNCommitPacket;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnCommit;

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

	public SVNClientManager getSVNClientManager() throws SVNException {
		org.tmatesoft.svn.core.io.SVNRepository repository = getRepository();
		ISVNOptions myOptions = getISVNOptions();
		// provide svn username and password
		// username = name used to connect to svn
		// password = password used to connect to svn
		ISVNAuthenticationManager myAuthManager = getISVNAuthenticationManager();
		repository.setAuthenticationManager(myAuthManager);
		// clientManager will be used to get different kind of svn clients
		// instances to do different activities
		// like update, commit, view diff etc.
		return SVNClientManager.newInstance(myOptions, myAuthManager);
	}

	private SVNURL getSVNURL() throws SVNException {
		return SVNURL.parseURIEncoded(url);
	}

	private org.tmatesoft.svn.core.io.SVNRepository getRepository()
			throws SVNException {
		org.tmatesoft.svn.core.io.SVNRepository repository = SVNRepositoryFactory
				.create(getSVNURL());
		repository.setAuthenticationManager(getISVNAuthenticationManager());
		return repository;
	}

	private ISVNOptions getISVNOptions() {
		return SVNWCUtil.createDefaultOptions(true);
	}

	private ISVNAuthenticationManager getISVNAuthenticationManager() {
		return SVNWCUtil.createDefaultAuthenticationManager(this.userName,
				this.userPassword);
	}

	private void exportFromSvn(SVNClientManager clientManager)
			throws SVNException {
		SVNUpdateClient updateClient = clientManager.getUpdateClient();
		SVNURL url = getSVNURL();
		// destination path
		File dstPath = new File(root);
		// the revision number which should be looked upon for the file path
		SVNRevision pegRevision = SVNRevision.create(-1);
		// the revision number which is required to be exported.
		SVNRevision revision = SVNRevision.create(-1);
		// this would force the operation
		boolean force = true;
		// Till what extent under a directory, export is required, is determined
		// by depth. INFINITY means the whole subtree of that directory will be
		// exported
		SVNDepth recursive = getSVNDepth();
		updateClient.doCheckout(url, dstPath, pegRevision, revision, recursive,
				force);
	}

	private SVNDepth getSVNDepth() {
		return SVNDepth.INFINITY;
	}

	public void removeFiles(List<String> files) {
		System.out.println(files);
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

	@SuppressWarnings("deprecation")
	private void commitToSvn(SVNClientManager clientManager, File[] files)
			throws SVNException {
		SvnCommit commit =  clientManager.getOperationFactory().createCommit();
		commit.setCommitMessage("");
		
		SVNCommitClient commitClient = clientManager.getCommitClient();
		SVNCommitPacket packets = commitClient.doCollectCommitItems(files,
				false, true, getSVNDepth(), null);
		System.out.println(packets);
		boolean recursive = true;
		// for (File file : files) {
		// SVNCommitInfo importInfo = commitClient.doImport(file, getSVNURL(),
		// "testing svn kit integration", true);
		// System.out.println(importInfo);
		// }
		SVNCommitInfo importInfo = commitClient.doCommit(packets, false,
				"testing svn kit integration");
		// SVNCommitInfo importInfo = commitClient.doCommit(files, false,
		// "testing svn kit integration", true, recursive);
		System.out.println(importInfo);

		// SVNCommitInfo importInfo = commitClient.doCommit(files, false,
		// "testing svn kit integration", null, null, false, false,
		// getSVNDepth());
	}

	@Override
	public void synchronize(List<File> files) {
		try {
			commitToSvn(getSVNClientManager(), files.toArray(new File[0]));
		} catch (SVNException e) {
			e.printStackTrace();
		}
	}

	@Override
	public File getRoot() {
		return new File(root);
	}
}
