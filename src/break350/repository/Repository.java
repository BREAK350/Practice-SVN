package break350.repository;

import java.io.File;

public interface Repository {
	public void load();

	public void commit(String comment);

	public File getRoot();
}
