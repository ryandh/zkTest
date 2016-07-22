package zkTest.DisctCounterTest;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.primitives.Longs;

import curator.lockdemo.DistribtuedCounter;

public class DisTest {

	int port = 1234;
	final int loop = 1000;
	final int thread = 20;
	org.apache.curator.test.TestingServer ts;

	@Before
	public void setUup() throws Exception {
		ts = new TestingServer(port);
	}

	@After
	public void tearDown() throws IOException {
		ts.stop();
	}

	@Test
	public void TestIncrease100WithLock() throws Exception {

		for (int i = 0; i < thread; i++)
			Executors.newFixedThreadPool(loop).submit(new Runnable() {

				public void run() {
					DistribtuedCounter counter = new DistribtuedCounter();
					CuratorFramework fs = DistribtuedCounter.getClient(port);
					try {
						counter.CreateCounterAndIncreaee(loop, fs, true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

		Thread.sleep(100000);
		// we expect the count will be 10*100=1000
		DistribtuedCounter counter = new DistribtuedCounter();
		CuratorFramework fs = DistribtuedCounter.getClient(port);
		fs.start();
		long v = Longs.fromByteArray(fs.getData().forPath("/counter"));
		Assert.assertEquals(v, loop * thread);
	}

	@Test
	public void TestIncrease100WithOutLock() throws Exception {
		CuratorFramework fs = DistribtuedCounter.getClient(port);
		fs.start();
		fs.create().withMode(CreateMode.PERSISTENT)
				.forPath("/counter", Longs.toByteArray(1l));
		System.out.println("set it to zero");

		for (int i = 0; i < thread; i++)
			Executors.newFixedThreadPool(loop).submit(new Runnable() {

				public void run() {
					DistribtuedCounter counter = new DistribtuedCounter();
					CuratorFramework fs = DistribtuedCounter.getClient(port);
					try {
						counter.CreateCounterAndIncreaee(loop, fs, false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

		Thread.sleep(3000);
		// we expect the count will be 10*100=1000
		DistribtuedCounter counter = new DistribtuedCounter();
		
		//fs.start();
		long v = Longs.fromByteArray(fs.getData().forPath("/counter"));
		Assert.assertEquals(v, loop * thread);
	}

}
