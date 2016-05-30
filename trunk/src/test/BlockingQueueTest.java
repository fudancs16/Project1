package test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueTest {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
     BlockingQueue<String> queue=new LinkedBlockingQueue<String>();
     
     Producer p1=new Producer(queue);
     Producer p2=new Producer(queue);
     Producer p3=new Producer(queue);
     Consumer consumer=new Consumer(queue);
     
     ExecutorService service=Executors.newCachedThreadPool();
     service.execute(p1);
     service.execute(p2);
     service.execute(p3);
     service.execute(consumer);
     Thread.sleep(10*1000);
     p1.stop();
     p2.stop();
     p3.stop();
     Thread.sleep(2000);
     // 退出Executor
     service.shutdown();
     
	}

}
