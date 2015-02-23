package break350.repository;

import java.util.List;

public interface Repository {
	public void exportFromRemoteRepository();

	public void removeFiles(List<String> files);
}
