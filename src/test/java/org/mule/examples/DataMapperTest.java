package org.mule.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class DataMapperTest extends FunctionalTestCase
{
	private static final String PASSWORD = "1234";
	private static final String USER = "joe";
	private static final String HOST = "localhost";
	private static final String PORT = "9879";
	private static final String HOME = "/data/employees";
	private SSHServerMock server;
	private final String MESSAGE = "{\n\t  \"employees\": {\n\t    \"employee\": [\n\t      {\n\t        \"name\": \"John\",\n\t        \"lastName\": \"Doe\",\n\t        \"addresses\": {\n\t          \"address\": [\n\t            {\n\t              \"street\": \"123 Main Street\",\n\t              \"zipCode\": \"111\"\n\t            },\n\t            {\n\t              \"street\": \"987 Cypress Avenue\",\n\t              \"zipCode\": \"222\"\n\t            }\n\t          ]\n\t        }\n\t      },\n\t      {\n\t        \"name\": \"Jane\",\n\t        \"lastName\": \"Doe\",\n\t        \"addresses\": {\n\t          \"address\": [\n\t            {\n\t              \"street\": \"345 Main Street\",\n\t              \"zipCode\": \"111\"\n\t            },\n\t            {\n\t              \"street\": \"654 Sunset Boulevard\",\n\t              \"zipCode\": \"333\"\n\t            }\n\t          ]\n\t        }\n\t      }\n\t    ]\n\t  }\n\t}";
	
    @Override
    protected String getConfigResources()
    {
        return "data-mapper.xml";
    }

    @BeforeClass
    public static void init() throws IOException{
    	System.setProperty("sftp.user", USER);
    	System.setProperty("sftp.password", PASSWORD);
    	System.setProperty("sftp.port", PORT);
    	System.setProperty("sftp.path", "/");
    	System.setProperty("sftp.host", HOST);
		File dataDirectory = new File(HOME);
		if (dataDirectory.exists()) {
		    FileUtils.deleteDirectory(dataDirectory);
		}
		dataDirectory.mkdirs();
    	//startServer();
    }
    
    
    private SshServer sshd;
    @Before
    public void beforeTestSetup() throws Exception {
    	sshd = SshServer.setUpDefaultServer();
	    sshd.setPort(9879);
	
	    sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
	    sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
	
		    public boolean authenticate(String username, String password, ServerSession session) {
		    	return true;
		    }
		    });
		
		    CommandFactory myCommandFactory = new CommandFactory() {
		
		    public Command createCommand(String command) {
		    	System.out.println("Command: " + command);
		    	return null;
		    }
		    
		    };
    
	    sshd.setCommandFactory(new ScpCommandFactory(myCommandFactory));

	    List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>(); 
	    namedFactoryList.add(new SftpSubsystem.Factory()); 
	    sshd.setSubsystemFactories(namedFactoryList); 
	    
	    sshd.start();
	
     }
    
    @After
    public void teardown() throws Exception { 
    	sshd.stop(); 
    }
    
    @After
    public void tearDown() {   
    	try {
			if (server != null) {
				server.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @Test
    public void testDataMapper() throws Exception
    {
    	
        MuleClient client = new MuleClient(muleContext);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("http.method", "POST");
        MuleMessage result = client.send("http://localhost:8081/", MESSAGE, props);
        
        
    }

    private static final String MAPPINGS_FOLDER_PATH = "./mappings";

	@Override
	protected Properties getStartUpProperties() {
		Properties properties = new Properties(super.getStartUpProperties());

		String pathToResource = MAPPINGS_FOLDER_PATH;
		File graphFile = new File(pathToResource);

		properties.put(MuleProperties.APP_HOME_DIRECTORY_PROPERTY,
				graphFile.getAbsolutePath());

		return properties;
	}
	    

//	@Test
	public void testPutAndGetFile() throws Exception {
		JSch jsch = new JSch();

		Hashtable<String, String> config = new Hashtable<String, String>();
		config.put("StrictHostKeyChecking", "no");
		JSch.setConfig(config);

		Session session = jsch.getSession("remote-username", "localhost", 9879);
		session.setPassword("remote-password");

		session.connect();

		Channel channel = session.openChannel("sftp");
		channel.connect();

		ChannelSftp sftpChannel = (ChannelSftp) channel;

		final String testFileContents = "some file contents";

		String uploadedFileName = "uploadFile";
//		sftpChannel.put(new ByteArrayInputStream(testFileContents.getBytes()), uploadedFileName);
//
//		String downloadedFileName = "downLoadFile";
//		sftpChannel.get(uploadedFileName, downloadedFileName);
//
//		File downloadedFile = new File(downloadedFileName);
//		assertTrue(downloadedFile.exists());
//		
//		if (sftpChannel.isConnected()) {
//		sftpChannel.exit();
//		logger.debug("Disconnected channel");
//		}
//
//		if (session.isConnected()) {
//		session.disconnect();
//		logger.debug("Disconnected session");
//		}

		}
}
