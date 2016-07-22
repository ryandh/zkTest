package curator.leaderSelector;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.Participant;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class LeaderLatchExample {

    private CuratorFramework client;
    private String latchPath;
    private String id;
    private LeaderLatch leaderLatch;

    public LeaderLatchExample(String connString, String latchPath, String id) {
        client = CuratorFrameworkFactory.newClient(connString, new ExponentialBackoffRetry(1000, Integer.MAX_VALUE));
        this.id = id;
        this.latchPath = latchPath;
    }

    public void start() throws Exception {
        client.start();
        client.blockUntilConnected();
        client.getZookeeperClient().blockUntilConnectedOrTimedOut();
        leaderLatch = new LeaderLatch(client, latchPath, id);
        leaderLatch.addListener(new LeaderLatchListener() {
			
			public void notLeader() {
				// TODO Auto-generated method stub
				System.out.println("i am the not leader");
			}
			
			public void isLeader() {
				// TODO Auto-generated method stub
				System.out.println("i am the leader");
			}
		});
        leaderLatch.start();
    }
    
    public void Stop()
    {
    	client.close();
    }

    public boolean isLeader() {
        return leaderLatch.hasLeadership();
    }

    public Participant currentLeader() throws Exception {
    	
        return leaderLatch.getLeader();
    }

    public void close() throws IOException {
        leaderLatch.close();
        client.close();
    }


    public static void main(String[] args) throws Exception {
        String latchPath = "/latch";
        String connStr = "127.0.0.1:2181";
        LeaderLatchExample node1 = new LeaderLatchExample(connStr, latchPath, "node-1");
        LeaderLatchExample node2 = new LeaderLatchExample(connStr, latchPath, "node-2");
        LeaderLatchExample node3 = new LeaderLatchExample(connStr, latchPath, "node-3");
        
        LeaderLatchExample node4 = new LeaderLatchExample(connStr, latchPath, "node-4");
        
        
        node1.start();
        node2.start();
        node3.start();
        node4.start();
        Thread.sleep(1000);

        for (int i = 0; i < 10; i++) {
        	 
        	int s=System.in.read();
        	System.out.println(s);
        	if(s==49)
        	{
        		node1.close();
        	}
        	if(s==50)
        	{
        		node2.close();
        	}
        		
        	if(s==51)
        	{
        		node3.close();
        	}
        		
        	if(s==52)
        	{
        		node4.close();
        	}
        		
        			
        	try
        	{
            System.out.println("node-1 think the leader is " + node1.currentLeader());
        	}catch(Exception t){}
        	try
        	{
            System.out.println("node-2 think the leader is " + node2.currentLeader());
        	}catch(Exception t){}
        	try
        	{
            System.out.println("node-3 think the leader is " + node3.currentLeader());
        	}catch(Exception t){}
        	try
        	{
            System.out.println("node-4 think the leader is " + node4.currentLeader());
        	}catch(Exception t){}
        	
            
            Thread.sleep(2000);
        }

        node1.close();

        System.out.println("now node-2 think the leader is " + node2.currentLeader());

        node2.close();

    }

}
