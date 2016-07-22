package curator;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;

public class HelloCurator {
	public static void main(String[] args) throws Exception {
		//ConnectTOZKandCreateNodeHelloUsingZkStyle();
		
		ConnectToZkandUsingCuratorStyle();
	}

	private static void ConnectToZkandUsingCuratorStyle() throws Exception {
		CuratorFramework client=CuratorFrameworkFactory.newClient("localhost:2181",new RetryOneTime(1));
		client.start();
		 client.blockUntilConnected();
		 client.create().withMode(CreateMode.PERSISTENT).forPath("/test");
		
	}

	private static void ConnectTOZKandCreateNodeHelloUsingZkStyle() throws Exception {
		CuratorZookeeperClient client = new CuratorZookeeperClient(
				"localhost:2181", 14000, 15000, null, new RetryUntilElapsed(
						14000, 0));
		client.start();
		try {
			client.blockUntilConnectedOrTimedOut();
			if (client.getZooKeeper().exists("/Curator", null) == null)
				client.getZooKeeper().create("/Curator", "".getBytes(),
						Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} finally {
			client.close();
		}
	}

}
