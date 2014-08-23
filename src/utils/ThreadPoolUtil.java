package utils;

import model.uploadpart.RejectedHandler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 *
 *
 */

public class ThreadPoolUtil {

	public static final int IOCORETHREADSNUMBER = 36;

	public static final int IOMAXTHREADSNUMBER = 56;

	public static final int IOTHREADSKEEPALIVETIME = 10;

	public static final TimeUnit IOTHREADSTIMEUNIT = TimeUnit.SECONDS;

	public static final int IOTHREADSQUEUELENGTH = 5;

	public static final LinkedBlockingQueue<Runnable> IOTHREADSQUEUE = new LinkedBlockingQueue<Runnable>(IOTHREADSQUEUELENGTH);


	public static final int CPUCORETHREADSNUMBER = 5;

	public static final int CPUMAXTHREADSNUMBER = 9;

	public static final int CPUTHREADSKEEPALIVETIME = 60;

	public static final TimeUnit CPUTHREADSTIMEUNIT = TimeUnit.MINUTES;

	public static final int CPUTHREADSQUEUELENGTH = 80;

	public static final LinkedBlockingQueue<Runnable> CPUTHREADSQUEUE = new LinkedBlockingQueue<Runnable>(CPUTHREADSQUEUELENGTH);

	public static final RejectedExecutionHandler HANDLER = new RejectedHandler();


	public static ThreadPoolExecutor cpuThreadPoolExecutor;

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
