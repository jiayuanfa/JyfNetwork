package com.example.http;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 队列管理类 线程管理类
 * 使用单例来实现
 */
public class ThreadManager {

    private static ThreadManager threadManager = new ThreadManager();

    // 请求线程的阻塞队列
    // 为什么使用Runnable而不是HttpTask是因为后面更好的扩展，比如再有一个其他的继承Runnable类
    private LinkedBlockingDeque<Runnable> mQueue = new LinkedBlockingDeque<>();
    // 线程池
    private ThreadPoolExecutor threadPoolExecutor;

    // 创建重试队列
    // 用到DelayQueue，必须要实现一个Delay的接口，有一个时间的概念
    private DelayQueue<HttpTask> failedQueue = new DelayQueue<>();

    private ThreadManager(){
        // 初始化线程池
        // 解释一下线程池各个参数的意思
        /**
         * 1: 核心线程数
         * 2：最大线程数
         * 3：空闲线程的保留时间
         * 4：单位：秒
         * 5：队列对象，它决定了缓存任务的排队策略，用来装载被拒绝的对象，以及最大数量
         * 6：被拒绝之后
         */
        threadPoolExecutor = new ThreadPoolExecutor(3, 10, 15, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(4),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        // 被拒绝后再次加入到线程池中，保证每一个请求都执行到位
                        addTask(r);
                    }
                });

        // 通过线程池，来执行我们的核心线程
        threadPoolExecutor.execute(runnable);
        // 执行重试队列的核心线程
        threadPoolExecutor.execute(failedRunnable);
    }

    public static ThreadManager getInstance() {
        return threadManager;
    }

    /**
     * 将请求线程加入到队列中的方法
     */
    public void addTask(Runnable runnable) {
        if (runnable == null) return;
        try {
            mQueue.put(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将失败的请求线程加入到重试队列中
     * @param httpTask
     */
    public void addFailedTask(HttpTask httpTask) {
        if (runnable == null) return;
        failedQueue.offer(httpTask);
    }

    /**
     * 核心线程 一直去队列中获取到请求线程，然后让线程池去执行
     */
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    // 拿出请求线程
                    Runnable take = mQueue.take();
                    // 通过请求线程去执行
                    threadPoolExecutor.execute(take);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 重试的核心线程
     */
    public Runnable failedRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    HttpTask httpTask = failedQueue.take();
                    if (httpTask.getFailNum() < 3) {
                        // 通过线程池执行
                        httpTask.setFailNum(httpTask.getFailNum() + 1);
                        threadPoolExecutor.execute(httpTask);
                        Log.e("请求重试机制==========", "我是次数" + httpTask.getFailNum());
                    } else {
                        JsonHttpRequest iHttpRequest = (JsonHttpRequest) httpTask.getiHttpRequest();
                        iHttpRequest.getCallBackListener().onFailed();
                        // 重试了三次依然失败
                        Log.e("请求重试机制=============", "失败超过3次");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
