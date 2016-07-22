package barriers;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.BaseClassForTests;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

public class testBarrier extends BaseClassForTests {

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
	public void testSetupBarrieronRedisandRemoveitWhenReady() throws Exception {
		final CuratorFramework client = CuratorFrameworkFactory.builder()
				.connectString(server.getConnectString())
				.connectionTimeoutMs(10000).retryPolicy(new RetryOneTime(1))
				.build();
		client.start();

		DistributedBarrier barrier = new DistributedBarrier(client,
				"/RedisReady");

		barrier.setBarrier();
		//

		// two threadss wating for the barrier , then they cna move to index
		ExecutorService pool = Executors.newCachedThreadPool();
		Callable<String> worker = new Callable<String>() {

			public String call() throws Exception {
				final CuratorFramework client = CuratorFrameworkFactory
						.builder().connectString(server.getConnectString())
						.connectionTimeoutMs(10000)
						.retryPolicy(new RetryOneTime(1)).build();
				client.start();
				DistributedBarrier barrier = new DistributedBarrier(client,
						"/RedisReady");
				barrier.waitOnBarrier(10, TimeUnit.SECONDS);

				return "WorkDone";
			}
		};
		
		Callable<String> worker2 = new Callable<String>() {

			public String call() throws Exception {
				final CuratorFramework client = CuratorFrameworkFactory
						.builder().connectString(server.getConnectString())
						.connectionTimeoutMs(10000)
						.retryPolicy(new RetryOneTime(1)).build();
				client.start();
				DistributedBarrier barrier = new DistributedBarrier(client,
						"/RedisReady");
				barrier.waitOnBarrier(10, TimeUnit.SECONDS);

				return "WorkDone2";
			}
		};
		Future<String> f1 = pool.submit(worker);
		Future<String> f2 = pool.submit(worker2);

		// setup redis ready and clean the barrier in after 3 seconds
		pool.execute(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				final CuratorFramework client = CuratorFrameworkFactory
						.builder().connectString(server.getConnectString())
						.connectionTimeoutMs(10000)
						.retryPolicy(new RetryOneTime(1)).build();
				client.start();
				DistributedBarrier barrier = new DistributedBarrier(client,
						"/RedisReady");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					barrier.removeBarrier();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		String resut = f1.get(5, TimeUnit.SECONDS);
		String resut2 = f2.get(5, TimeUnit.SECONDS);
		Assert.assertEquals("WorkDone", resut);
		Assert.assertEquals("WorkDone2", resut2);

	}
}
