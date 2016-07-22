package locktest;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class MyWatcher implements Watcher {

	public void process(WatchedEvent event) {
		 System.out.println(event.toString());

	}

}
