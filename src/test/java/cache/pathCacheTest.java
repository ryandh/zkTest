package cache;

import java.io.IOException;
import java.util.List;

import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.BaseClassForTests;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;

public class pathCacheTest extends BaseClassForTests {

	TestingServer server;

	@Before
	public void setUP() throws Exception {
		server = new TestingServer(1234);
		server.start();
	}

	@After
	public void tearDown() throws IOException {
		server.stop();
	}
	
	@Test
	public void TestCache() throws Exception
	{
		
		CuratorFramework fs=CuratorFrameworkFactory.newClient("localhost:2181",new RetryNTimes(10, 1000));
		fs.start();
		
		PathChildrenCache pcc=new PathChildrenCache(fs, "/montaque", true);
		pcc.start();
	 
		while(true)
		{
			List<ChildData> data=pcc.getCurrentData();
			for (ChildData childData : data) {
				System.out.println(childData.getPath());
				System.out.println(  new String( childData.getData() ));
				
			}
			Thread.sleep(2000);
		}
		
		 
	}
}
