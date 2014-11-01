package model.status.uploadpart;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;


/**
 *  author: 康乐
 *  time: 2014/8/21
 *  function: 当线程池和队列都满后，此方法将会被注册了该方法的线程池所调用，来进行饱和情况的处理
 */


public class RejectedHandler implements RejectedExecutionHandler{
	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		System.err.println("报告管理员！！！这个是当线程池和队列君满后提供的处理操作，暂时不做操作");
		System.err.println("报告管理员！！！这个是当线程池和队列君满后提供的处理操作，暂时不做操作");
		System.err.println("报告管理员！！！这个是当线程池和队列君满后提供的处理操作，暂时不做操作");
		System.err.println("报告管理员！！！这个是当线程池和队列君满后提供的处理操作，暂时不做操作");
		throw new RejectedExecutionException("Task " + r.toString() +
				" rejected from " + executor.toString());
	}
}
