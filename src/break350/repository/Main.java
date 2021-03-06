package break350.repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import break350.repository.files.FilesGenerator;
import break350.repository.files.FilesGeneratorFactory;

public class Main {
	public final static String configFileName = "config.properties";
	private static Properties properties = new Properties();

	private static void loadProperties() {
		InputStream input = null;
		try {
			input = new FileInputStream(configFileName);
			properties.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Properties getProperties() {
		return properties;
	}

	public static void main(String[] args) {
		loadProperties();

		FilesGenerator filesGenerator = FilesGeneratorFactory.getGenerator();

		Repository repository = RepositoryFactory.getRepository();
		repository.exportFromRemoteRepository();
		repository.removeFiles(filesGenerator.generate());
		repository.exportFromRemoteRepository();
	}

}
