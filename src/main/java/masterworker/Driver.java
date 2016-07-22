package masterworker;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Driver {

	interface IDoWork {
		void LetsWork(String args);
	}

	public static void main(String[] args) throws KeeperException,
			InterruptedException {
		// TODO Auto-generated method stub
		// Start 2 Masters

		// Register 2 works

		// task input /task/worker2/task1 content

		// Start 1 Client to Submit Tasks

		RegisterMaster("LA", "http://0.0.0.1:11111");
		// RegisterMaster("LA","http://0.0.0.1:22222");
	}

	private static void RegisterMaster(String masterName, String url)
			throws KeeperException, InterruptedException {

		try {
			ZooKeeper zk = new ZooKeeper("localhost:2181", 2000, null);
			zk.create("/master", masterName.getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);
			while (true) {
				// get task from tasks and send to workers
				List<String> tasks = zk.getChildren("/tasks", false);

				List<String> works = zk.getChildren("/workers", false);
				if (works.size() == 0) {
					// no worker;
					System.out.println("no worker");
					continue;
				}

				if (tasks.size() == 0) {
					// no worker;
					System.out.println("no task");
				}

				for (int i = 0; i < tasks.size(); i++) {
					for (int j = 0; j < works.size(); j++) {
						if (null == zk.exists(
								"/Assignment/Woker" + works.get(j), false))
						{
							zk.create("/Assignment/Woker" + works.get(j),
									"".getBytes(), Ids.OPEN_ACL_UNSAFE,
									CreateMode.PERSISTENT);
							System.out.println("I created worker" + "/Assignment/Woker" + works.get(j));
						}
						
						Thread.sleep(2000);
						zk.create("/Assignment/Woker" + works.get(j) + "/Task"
								+ tasks.get(i), "".getBytes(),
								Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
					}

				}
				Thread.sleep(3000);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
