package utils;

import model.uploadpart.RejectedHandler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * author: 康乐
 * time: 2014/8/24
 * function: 提供创建线程池所需要的参数和线程池，需要比较注意的是：当前
 * 的cpu线程池线程的数量最佳为cpu的数量加1，而IO线程池的线程数量最好是
 * 当前cpu数量的2倍，至于存活时间，那么就需要根据任务的多少等因素来决定
 *
 * 目前io线程池的线程数量是cpu数量的8倍，存活时间是10s，这个值十分需要以
 * 后的进一步测量.
 */

public class ThreadPoolUtils {

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

	/**
	 * cpu线程池getter
	 * @return 当前cpu线程池对象
	 */
	public static ThreadPoolExecutor getCpuThreadPoolExecutor() {

		return cpuThreadPoolExecutor;
	}

	/**
	 * io线程池getter
	 * @return 当前cpu线程池对象
	 */
	public static ThreadPoolExecutor getIoThreadPoolExecutor() {

		return ioThreadPoolExecutor;
	}

	/**
	 * cpu线程池setter
	 * @param cpuThreadPoolExecutor 需要设置的cpu线程池对象
	 */
	public static void setCpuThreadPoolExecutor(ThreadPoolExecutor cpuThreadPoolExecutor) {

		ThreadPoolUtils.cpuThreadPoolExecutor = cpuThreadPoolExecutor;
	}

	/**
	 * io线程池setter
	 * @param ioThreadPoolExecutor 需要设置的io线程池对象
	 */
	public static void setIoThreadPoolExecutor(ThreadPoolExecutor ioThreadPoolExecutor) {

		ThreadPoolUtils.ioThreadPoolExecutor = ioThreadPoolExecutor;
	}
}
