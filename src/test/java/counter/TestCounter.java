package counter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.BaseClassForTests;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class TestCounter extends BaseClassForTests {

	TestingServer server;

	@Before
	public void setUP() throws Exception {
		server = new TestingServer(12345);
		server.start();
	}

	@After
	public void tearDown() throws IOException {
		server.stop();
	}

	@Test
	public void TestIncreaseCounterIN10Threads() throws Exception {

		int thread = 50;
		final int cs = 100;
		ExecutorService pool = Executors.newFixedThreadPool(50);
		int[] sd = new int[3];
		ArrayList<Future<Integer>> fss = new ArrayList<Future<Integer>>();
		for (int i = 0; i < thread; i++)
			fss.add(pool.submit(new Callable<Integer>() {

				public Integer call() throws Exception {
					// TODO Auto-generated method stub
					CuratorFramework fs = CuratorFrameworkFactory.newClient(
							server.getConnectString(), new RetryNTimes(10, 200));
					fs.start();
					SharedCount count = new SharedCount(fs, "/count", 0);
					// System.out.println("started");
					try {
						count.start();

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.out.println("started Excetion");
					}
					Random r = new Random();
					int tryied = 0;
					for (int j = 0; j < cs; j++) {
						try {
							// Thread.sleep(400 * r.nextInt(5));
							//Thread.sleep(200 * r.nextInt(5));
							int temp = count.getCount();
							boolean goodornot = count.trySetCount(
									count.getVersionedValue(), temp + 1);

							while (goodornot == false) {
								tryied += 1;
								Thread.sleep(200);
								temp = count.getCount();
								goodornot = count.trySetCount(
										count.getVersionedValue(), temp + 1);

							}

							// count.setCount(temp + 1);
							System.out.println("Set OK" + temp);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("Set Failed xx" + e.getMessage());
							e.printStackTrace();
						}

					}
					return tryied;

				}
			}));

		int total = 0;
		for (Future<Integer> f : fss) {
			int i = f.get();
			total += i;
		}

		CuratorFramework fs = CuratorFrameworkFactory.newClient(
				server.getConnectString(), new RetryNTimes(10, 200));
		fs.start();
		SharedCount count = new SharedCount(fs, "/count", 0);
		count.start();

		Assert.assertEquals(thread * cs, count.getCount());
		System.out.println("Tried tota times " + total);

	}
}
