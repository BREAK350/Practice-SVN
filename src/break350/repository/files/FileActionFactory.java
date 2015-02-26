package break350.repository.files;

public class FileActionFactory {
	public static FileAction getFileAction() {
		return new RemoveFileAction();
	}
}
