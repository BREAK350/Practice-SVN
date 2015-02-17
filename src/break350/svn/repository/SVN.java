package break350.svn.repository;

import java.io.File;

public interface SVN {
	public void checkout(File root);

	public void commit(String comment);
}
