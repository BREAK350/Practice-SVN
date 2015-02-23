package break350.repository;

import break350.repository.svn.SVNRepository;

public class RepositoryFactory {
	public static Repository getRepository() {
		String url = Main.getProperties().getProperty("url");
		String userName = Main.getProperties().getProperty("userName");
		String userPassword = Main.getProperties().getProperty("userPassword");

		System.out.println("url = " + url + " login = " + userName
				+ " password = " + userPassword);

		SVNRepository svnRepository = new SVNRepository();
		svnRepository.setUrl(url);
		svnRepository.setUserName(userName);
		svnRepository.setUserPassword(userPassword);

		return svnRepository;
	}
}
