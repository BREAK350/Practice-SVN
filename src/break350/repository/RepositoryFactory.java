package break350.repository;

import break350.repository.svn.SVNRepository;

public class RepositoryFactory {
	public static Repository getRepository() {
		return new SVNRepository();
	}
}
