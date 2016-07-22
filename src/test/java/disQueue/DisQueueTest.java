package disQueue;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.*;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.BaseClassForTests;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DisQueueTest extends BaseClassForTests {

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
	public void TestSendMessgesInQueueandGetThemInOrder() throws InterruptedException {
		// start 5 threads to Product message

		Callable<Void> ProductMesage = () -> {
			CuratorFramework fs = CuratorFrameworkFactory.newClient(
					server.getConnectString(), new RetryNTimes(10, 100));
			fs.start();

			final QueueSerializer<String> serializer = new QueueSerializer<String>() {
				@Override
				public byte[] serialize(String item) {
					return item.getBytes();
				}

				@Override
				public String deserialize(byte[] bytes) {
					return new String(bytes);
				}
			};

			DistributedPriorityQueue<String> queue = QueueBuilder.builder(fs, null,
					serializer, "/queue").buildPriorityQueue(10);
			
			queue.start();

			for (int i = 0; i < 100; i++) {
				queue.put("item" + i,i % 10);
			}

			return null;

		};

		
		 
		Function<String, Callable<Void> > GeneratorConsumer= new Function<String, Callable<Void>>() {
			
			@Override
			public Callable<Void> apply(String prifix) {
				// TODO Auto-generated method stub
				return 
						() -> {
							 
							CuratorFramework fs = CuratorFrameworkFactory.newClient(
									server.getConnectString(), new RetryNTimes(10, 100));
							fs.start();

							final QueueSerializer<String> serializer = new QueueSerializer<String>() {
								@Override
								public byte[] serialize(String item) {
									return item.getBytes();
								}

								@Override
								public String deserialize(byte[] bytes) {
									return new String(bytes);
								}
							};
							
							QueueConsumer<String> cons = new QueueConsumer<String>() {

								@Override
								public void stateChanged(CuratorFramework client,
										ConnectionState newState) {
									// TODO Auto-generated method stub

								}

								@Override
								public void consumeMessage(String message) throws Exception {
									System.out.println( prifix +  "I got message" + message);

								}
							};
							QueueBuilder.builder(fs, cons, serializer, "/queue").buildIdQueue()
									.start();
							return null;
						};
			}
		};
	 
		 
		 

		ExecutorService pool=Executors.newFixedThreadPool(10);
		pool.submit(ProductMesage);
		pool.submit(GeneratorConsumer.apply("c1111"));
	//	pool.submit(GeneratorConsumer.apply("c2222"));
		 
		Thread.sleep(10000);
		
	}
}
