package break350.repository.wwf;

public class FileActionFactory {
	public static FileAction getFileAction() {
		return new RemoveFileAction();
	}
}
