package utils;

import model.uploadpart.RejectedHandler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * author: 康乐
 * time: 2014/8/24
 * function: 提供创建线程池所需要的参数和线程池
 */

public class ThreadPoolUtil {

	// IO密集型线程池基础线程数量
	public static final int IOCORETHREADSNUMBER = 36;

	// IO密集型线程池最大线程数量
	public static final int IOMAXTHREADSNUMBER = 56;

	// IO线程密集型线程线程保持存活时间 10s
	public static final int IOTHREADSKEEPALIVETIME = 10;

	// IO线程密集型保持存活时间的单位
	public static final TimeUnit IOTHREADSTIMEUNIT = TimeUnit.SECONDS;

	// IO密集型线程池的队列长度
	public static final int IOTHREADSQUEUELENGTH = 50;

	// IO密集型线程池队列
	public static final LinkedBlockingQueue<Runnable> IOTHREADSQUEUE = new LinkedBlockingQueue<Runnable>(IOTHREADSQUEUELENGTH);

	// CPU密集型线程池基础线程数量
	public static final int CPUCORETHREADSNUMBER = 6;

	// CPU密集型线程池最大线程数量
	public static final int CPUMAXTHREADSNUMBER = 11;

	// CPU线程密集型线程线程保持存活时间 10s
	public static final int CPUTHREADSKEEPALIVETIME = 60;

	// CPU程密集型保持存活时间的单位
	public static final TimeUnit CPUTHREADSTIMEUNIT = TimeUnit.MINUTES;

	// CPU集型线程池的队列长度
	public static final int CPUTHREADSQUEUELENGTH = 80;

	// CPU集型线程池队列
	public static final LinkedBlockingQueue<Runnable> CPUTHREADSQUEUE = new LinkedBlockingQueue<Runnable>(CPUTHREADSQUEUELENGTH);

	// 当线程池和队列都满的时候所采取的的对于策略
	public static final RejectedExecutionHandler HANDLER = new RejectedHandler();

	// cpu线程池对象
	public static ThreadPoolExecutor cpuThreadPoolExecutor;

	// io线程池对象
	public static ThreadPoolExecutor ioThreadPoolExecutor;

	public static ThreadPoolExecutor getCpuThreadPoolExecutor() {
		return cpuThreadPoolExecutor;
	}

	public static ThreadPoolExecutor getIoThreadPoolExecutor() {
		return ioThreadPoolExecutor;
	}

	public static void setCpuThreadPoolExecutor(ThreadPoolExecutor cpuThreadPoolExecutor) {
		ThreadPoolUtil.cpuThreadPoolExecutor = cpuThreadPoolExecutor;
	}

	public static void setIoThreadPoolExecutor(ThreadPoolExecutor ioThreadPoolExecutor) {
		ThreadPoolUtil.ioThreadPoolExecutor = ioThreadPoolExecutor;
	}
}
