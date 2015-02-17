package break350.svn.wwf;

import java.io.File;
import java.util.List;

public class RemoveFileAction implements FileAction {

	@Override
	public void perform(List<File> files) {
		if (files != null) {
			for (File file : files) {
				remove(file, 0);
			}
		}
	}

	private String createPrefix(int level) {
		String prefix = "";
		if (level > 0) {
			for (int i = 0; i < level; i++) {
				prefix += "\t";
			}
		}
		return prefix;
	}

	private void remove(File file, int level) {
		String fname = "'" + file.getAbsolutePath() + "'";
		String prefix = createPrefix(level);
		if (file.exists()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				remove(files[i], level + 1);
			}
			if (file.delete()) {
				System.out.println(prefix + "The file " + fname
						+ " is successfully deleted");
			} else {
				System.out.println(prefix + "Error: the file " + fname
						+ " is not deleted");
			}
		} else {
			System.out.println(prefix + "File not found: " + fname);
		}
	}
}
