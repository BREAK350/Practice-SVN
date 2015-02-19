package break350.repository.svn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import break350.repository.Repository;

public class SVNRepository implements Repository {
	private File root;
	private String url = "http://svn.svnkit.com/repos/svnkit/trunk";
	private String login = "anonymous";
	private String password = "anonymous";
	String filePath = "www/license.html";

	public SVNRepository(File root) {
		this.root = root;
	}

	@Override
	public void load() {
		setupLibrary();
		org.tmatesoft.svn.core.io.SVNRepository repository = null;
		try {
			repository = SVNRepositoryFactory.create(SVNURL
					.parseURIEncoded(url));
		} catch (SVNException svne) {
			System.err
					.println("error while creating an SVNRepository for the location '"
							+ url + "': " + svne.getMessage());
			System.exit(1);
		}
		ISVNAuthenticationManager authManager = SVNWCUtil
				.createDefaultAuthenticationManager(login, password);
		repository.setAuthenticationManager(authManager);

		SVNProperties fileProperties = new SVNProperties();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			SVNNodeKind nodeKind = repository.checkPath(filePath, -1);

			if (nodeKind == SVNNodeKind.NONE) {
				System.err.println("There is no entry at '" + url + "'.");
				System.exit(1);
			} else if (nodeKind == SVNNodeKind.DIR) {
				System.err.println("The entry at '" + url
						+ "' is a directory while a file was expected.");
				System.exit(1);
			}
			repository.getFile(filePath, -1, fileProperties, baos);

		} catch (SVNException svne) {
			System.err
					.println("error while fetching the file contents and properties: "
							+ svne.getMessage());
			System.exit(1);
		}

		String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);

		boolean isTextType = SVNProperty.isTextMimeType(mimeType);

		Iterator<?> iterator = fileProperties.nameSet().iterator();
		while (iterator.hasNext()) {
			String propertyName = (String) iterator.next();
			String propertyValue = fileProperties.getStringValue(propertyName);
			System.out.println("File property: " + propertyName + "="
					+ propertyValue);
		}
		if (isTextType) {
			System.out.println("File contents:");
			System.out.println();
			try {
				baos.writeTo(System.out);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else {
			System.out
					.println("File contents can not be displayed in the console since the mime-type property says that it's not a kind of a text file.");
		}
		long latestRevision = -1;
		try {
			latestRevision = repository.getLatestRevision();
		} catch (SVNException svne) {
			System.err
					.println("error while fetching the latest repository revision: "
							+ svne.getMessage());
			System.exit(1);
		}
		System.out.println("");
		System.out.println("---------------------------------------------");
		System.out.println("Repository latest revision: " + latestRevision);
		System.exit(0);
	}

	public void export() throws SVNException {
		SVNURL url = SVNURL.parseURIEncoded(this.url);
		String userName = "guest";
		String userPassword = "guest";
		File exportDir = this.root;
		org.tmatesoft.svn.core.io.SVNRepository repository = org.tmatesoft.svn.core.io.SVNRepositoryFactory
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
		long latestRevision = repository.getLatestRevision();
		ISVNReporterBaton reporterBaton = new ExportReporterBaton(
				latestRevision);
		ISVNEditor exportEditor = new ExportEditor(exportDir);
		repository.update(latestRevision, null, true, reporterBaton,
				exportEditor);
	}

	@Override
	public void commit(String comment) {

	}

	@Override
	public File getRoot() {
		return root;
	}

	private void setupLibrary() {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}

	private void diff() {
		// initialize SVNKit to work through file:/// protocol
		SamplesUtility.initializeFSFSprotocol();

		File baseDirectory = new File(args[0]);
		File reposRoot = new File(baseDirectory, "exampleRepository");
		File wcRoot = new File(baseDirectory, "exampleWC");

		try {
			// 1. first create a repository and fill it with data
			SamplesUtility.createRepository(reposRoot);
			// 2. populate the repository with the greek tree in a single
			// transaction
			SVNCommitInfo info = SamplesUtility.createRepositoryTree(reposRoot);
			// pring out the commit information
			System.out.println(info);

			// 3. checkout the entire repository tree
			SVNURL reposURL = SVNURL.fromFile(reposRoot);
			SamplesUtility.checkOutWorkingCopy(reposURL, wcRoot);

			// 4. now make some changes to the working copy
			SamplesUtility.writeToFile(new File(wcRoot, "iota"),
					"New text appended to 'iota'", true);
			SamplesUtility.writeToFile(new File(wcRoot, "A/mu"),
					"New text in 'mu'", false);

			SVNClientManager clientManager = SVNClientManager.newInstance();
			SVNWCClient wcClient = SVNClientManager.newInstance().getWCClient();
			// set some property on a working copy directory
			wcClient.doSetProperty(new File(wcRoot, "A/B"), "spam",
					SVNPropertyValue.create("egg"), false, SVNDepth.EMPTY,
					null, null);

			// 5. now run diff the working copy against the repository
			SVNDiffClient diffClient = clientManager.getDiffClient();
			/*
			 * This corresponds to 'svn diff -rHEAD'.
			 */
			diffClient.doDiff(wcRoot, SVNRevision.UNDEFINED,
					SVNRevision.WORKING, SVNRevision.HEAD, SVNDepth.INFINITY,
					true, System.out, null);
		} catch (SVNException svne) {
			System.out.println(svne.getErrorMessage());
			System.exit(1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
	}

}
