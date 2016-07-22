package curator.leaderSelector;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.RetryNTimes;

public class CompeteMaster {
	public static void main(String[] args) throws Exception {
		CuratorFramework client=  CuratorFrameworkFactory.builder().
				connectString("localhost:2181").retryPolicy(new RetryNTimes(100, 2000)).build();
		
	
		final LeaderLatch ll=new LeaderLatch(client, "/latch","ID:"+System.currentTimeMillis() % 100);
		ll.addListener(new LeaderLatchListener() {
			
			public void notLeader() {
				// TODO Auto-generated method stub
				System.out.println("I am not leader");  
			}
			
			public void isLeader() {
				System.out.println("I am the leader" + ll.getId());
				//registermylsef t the eeader /leader/myid
				
			}
		});
		client.start();
		 client.getZookeeperClient().blockUntilConnectedOrTimedOut();
		ll.start();
		while(true)
		{
		System.out.println( "Node " + ll.getId() + " Is Leader " +  ll.hasLeadership() + " Leader " + ll.getLeader().getId());
		Thread.sleep(2000);
		}
		 
	}
}
