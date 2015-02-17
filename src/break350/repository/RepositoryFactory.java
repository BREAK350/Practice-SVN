package break350.repository;

import java.io.File;

import break350.repository.svn.SVNRepository;

public class RepositoryFactory {
	public final static String ROOT_NAME = "";

	public static Repository getRepository() {
		File root = new File(ROOT_NAME);
		return new SVNRepository(root);
	}
}
