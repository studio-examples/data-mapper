package org.mule.examples;

import java.io.File;
import java.io.IOException;

import com.sshtools.daemon.platform.NativeAuthenticationProvider;
import com.sshtools.daemon.platform.PasswordChangeException;

/**
 * This authentication provider provides no authentication at all, and just lets anybody in so you should never use it!
 * 
 * It is really just for testing.
 */
public class DummyAuthenticationProvider extends NativeAuthenticationProvider {


	public DummyAuthenticationProvider() {
		System.out.println("DummyAuthenticationProvider is in use. This is only for testing.");
	}

	@Override
	public boolean changePassword(String username, String oldpassword, String newpassword) {
		return false;
	}

	@Override
	public String getHomeDirectory(String username) throws IOException {
		return new File("src/test/resources/ftp").getCanonicalPath();
	}

	@Override
	public void logoffUser() throws IOException {

	}

	@Override
	public boolean logonUser(String username, String password) throws PasswordChangeException, IOException {
		return true;
	}

	@Override
	public boolean logonUser(String username) throws IOException {
		return true;
	}

}
