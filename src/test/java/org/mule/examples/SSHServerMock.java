package org.mule.examples;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import com.sshtools.daemon.SshServer;
import com.sshtools.daemon.configuration.ServerConfiguration;
import com.sshtools.daemon.configuration.XmlServerConfigurationContext;
import com.sshtools.daemon.forwarding.ForwardingServer;
import com.sshtools.daemon.session.SessionChannelFactory;
import com.sshtools.j2ssh.configuration.ConfigurationException;
import com.sshtools.j2ssh.configuration.ConfigurationLoader;
import com.sshtools.j2ssh.connection.ConnectionProtocol;

/**
 * To use this server you will need:
 * hostkey.key, server.xml, platform.xml and DummyAuthenticationProvider.java
 * 
 * @author diniz
 *
 */
public class SSHServerMock {

	private String serverXmlRelativePath = "src/test/resources/server.xml";
	
	private String platformXmlRelativePath = "src/test/resources/platform.xml";
	
	public SSHServerMock(){
		this(null, null);
	}
	
	public SSHServerMock(String serverXmlRelativePath, String platformXmlRelativePath){
		this.serverXmlRelativePath = serverXmlRelativePath==null?this.serverXmlRelativePath:serverXmlRelativePath;
		this.platformXmlRelativePath = platformXmlRelativePath==null?this.platformXmlRelativePath:platformXmlRelativePath;
	}
	
	public void start() throws IOException {
		Executors.newSingleThreadExecutor().submit(new Callable<Object>() {
	        @Override
	        public Object call () throws Exception {
	        	XmlServerConfigurationContext context = new XmlServerConfigurationContext();
	    		context.setServerConfigurationResource(ConfigurationLoader.checkAndGetProperty("sshtools.server", serverXmlRelativePath));
	    		context.setPlatformConfigurationResource(System.getProperty("sshtools.platform", platformXmlRelativePath));
	    		ConfigurationLoader.initialize(false, context);
	    		SshServer server = new SshServer() {

	    			public void configureServices(ConnectionProtocol connection) throws IOException {
	    				connection.addChannelFactory(SessionChannelFactory.SESSION_CHANNEL, new SessionChannelFactory());
	    				if (ConfigurationLoader.isConfigurationAvailable(ServerConfiguration.class)) {
	    					if (((ServerConfiguration) ConfigurationLoader.getConfiguration(ServerConfiguration.class)).getAllowTcpForwarding()) {
	    						new ForwardingServer(connection);
	    					}
	    				}
	    			}

	    			public void shutdown(String msg) {
	    				// Disconnect all sessions
	    			}
	    			
	    		};
	    		server.startServer();
	            return null;
	        }
	    });
	}

	public void stop() throws ConfigurationException, UnknownHostException, IOException {
		Socket socket = new Socket(InetAddress.getLocalHost(),
				((com.sshtools.daemon.configuration.ServerConfiguration) ConfigurationLoader
						.getConfiguration(com.sshtools.daemon.configuration.ServerConfiguration.class)).getCommandPort());

		// Write the command id
		socket.getOutputStream().write(0x3a);
		// Write the length of the message (max 255)
		String msg = "bye";
		int len = (msg.length() <= 255) ? msg.length() : 255;
		socket.getOutputStream().write(len);
		if (len > 0) {
			socket.getOutputStream().write(msg.substring(0, len).getBytes());
		}
		socket.close();
	}

}
