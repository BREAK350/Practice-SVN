package break350.svn;

import java.io.File;
import java.util.List;

import break350.svn.generator.FileListGenerator;
import break350.svn.repository.SVN;
import break350.svn.wwf.FileAction;
import break350.svn.wwf.RemoveFileAction;

public class Main {

	public static void main(String[] args) {
		File root = new File("/");
		SVN svn = null;
		svn.checkout(root);

		FileListGenerator generator = null;
		List<File> files = generator.generate(root);

		FileAction fileAction = new RemoveFileAction();
		fileAction.perform(files);

		svn.commit("comment");
	}
}
