package org.mule.examples;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

public class CopyOfDataMapperTest extends FunctionalTestCase
{
	private static final String PASSWORD = "1234";
	private static final String USER = "joe";
	private static final String HOST = "127.0.0.1";
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
    
    @Before
	public void setUp() throws IOException, InterruptedException {
		// Server will start in localhost:2022, accepting any user/password
		server = new SSHServerMock();
		server.start();
		Thread.sleep(10000);
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
    
//    @Test
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
	    

}
