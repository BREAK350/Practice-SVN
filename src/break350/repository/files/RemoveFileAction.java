package break350.repository.files;

import java.io.File;
import java.util.List;

public class RemoveFileAction implements FileAction {

	public static boolean printSubFiles = true;

	@Override
	public void perform(List<File> files) {
		if (files != null) {
			for (File file : files) {
				remove(file, 0);
			}
		}
	}

	private void remove(File file, int level) {
		String fname = "'" + file.getAbsolutePath() + "'";
		String comment = "File not found: " + fname;
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					remove(files[i], level + 1);
				}
			}
			if (file.delete()) {
				comment = "The file " + fname + " is successfully deleted";
			} else {
				comment = "Error: the file " + fname + " is not deleted";
			}
		}
		if (level == 0 || printSubFiles) {
			System.out.println(comment);
		}
	}
}
