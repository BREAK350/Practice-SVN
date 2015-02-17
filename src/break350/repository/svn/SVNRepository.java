package break350.repository.svn;

import java.io.File;

import break350.repository.Repository;

public class SVNRepository implements Repository {
	private File root;

	public SVNRepository(File root) {
		this.root = root;
	}

	@Override
	public void load() {

	}

	@Override
	public void commit(String comment) {

	}

	@Override
	public File getRoot() {
		return root;
	}

}
