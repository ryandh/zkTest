package zkTest;

import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class FirstTest {

	org.apache.zookeeper.ZooKeeper keeper;

	@Before
	public void setUp() throws IOException {
		keeper=new ZooKeeper("localhost:2181", 2000, null);
	}

	@Test
	public void TestCreatePersistantNode() {
	  //	keeper.exi
	}
}
