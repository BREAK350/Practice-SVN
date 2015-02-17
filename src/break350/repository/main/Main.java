package break350.repository.main;

import java.io.File;
import java.util.List;

import break350.repository.Repository;
import break350.repository.RepositoryFactory;
import break350.repository.generator.FileListFactory;
import break350.repository.generator.FileListGenerator;
import break350.repository.wwf.FileAction;
import break350.repository.wwf.FileActionFactory;

public class Main {

	public static void main(String[] args) {
		Repository repository = RepositoryFactory.getRepository();
		repository.load();

		FileListGenerator generator = FileListFactory.getFileListGenerator();
		List<File> files = generator.generate(repository.getRoot());

		FileAction fileAction = FileActionFactory.getFileAction();
		fileAction.perform(files);

		repository.commit("comment");
	}
}
