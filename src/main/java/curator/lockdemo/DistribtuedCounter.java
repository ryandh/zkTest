package curator.lockdemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;

import com.google.common.primitives.Longs;

public class DistribtuedCounter {

	public static void main(String[] args) throws Exception {

		CuratorFramework fs = CuratorFrameworkFactory.builder()
				.connectString("localhost:2181")
				.retryPolicy(new RetryForever(2000)).build();

		CreateCounterAndIncreaee(100, fs,true);

	}

	public static void CreateCounterAndIncreaee(int loop, CuratorFramework fs,
		 Boolean	withLock) throws Exception {

		fs.start();

		// Increate the counter under /counter
		for (int i = 0; i < loop; i++) {
			org.apache.curator.framework.recipes.locks.InterProcessMutex mutex = new InterProcessMutex(
					fs, "/lock");
		 
 
			try {
				if (withLock)
					mutex.acquire();
				// zero the counter
				if (fs.checkExists().forPath("/counter") == null) {
					fs.create().withMode(CreateMode.PERSISTENT)
							.forPath("/counter", Longs.toByteArray(1l));
					System.out.println("set it to zero");
				} else {
					// incrase the counter
					long ld = Longs.fromByteArray(fs.getData().forPath(
							"/counter"));
					fs.setData().forPath("/counter", Longs.toByteArray(ld + 1));
					System.out.println("Done" + ld);
				}

			} finally {
				if (withLock)
					mutex.release();
			}
		}
	}

	public static CuratorFramework getClient(int port) {
		return CuratorFrameworkFactory.builder()
				.connectString("localhost:" + port)
				.retryPolicy(new RetryForever(2000)).build();
	}
}
