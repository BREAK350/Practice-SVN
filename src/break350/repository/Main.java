package break350.repository;

import break350.repository.files.FilesGenerator;
import break350.repository.files.FilesGeneratorFactory;

public class Main {

	public static void main(String[] args) {
		FilesGenerator filesGenerator = FilesGeneratorFactory.getGenerator();

		Repository repository = RepositoryFactory.getRepository();
		repository.removeFiles(filesGenerator.generate());
	}

}
