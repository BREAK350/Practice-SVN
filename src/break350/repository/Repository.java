package break350.repository;

import java.io.File;
import java.util.List;

public interface Repository {
	public void exportFromRemoteRepository();

	public void synchronize(List<File> files);

	public File getRoot();
}
