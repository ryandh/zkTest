package locktest;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class TestLock {

	static ZooKeeper zk;

	public static void main(String[] args) throws IOException {
		zk = new ZooKeeper("localhost:2181", 2000, new MyWatcher());

		// while(true)
		// {
		CreateLockIfOKandDoTask();
		// }

	}

	private static void CreateLockIfOKandDoTask() throws IOException {

		try {

			String t = zk.create("/lock", " ".getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);
			
			//zk.
			//String 
			System.out.println("doing the tasks" + t);
			Thread.sleep(300000);
			System.out.println("release the locks");

			zk.delete("/lock", 0);
		} catch (Throwable t) {
			System.out.println("got exception " + t.getMessage());
		}

	}
}
