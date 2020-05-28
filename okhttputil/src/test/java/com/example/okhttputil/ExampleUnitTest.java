package com.example.okhttputil;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void et() {
//        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 4,
//                3, TimeUnit.SECONDS,
//                new ArrayBlockingQueue<Runnable>(2),
//                new ThreadPoolExecutor.CallerRunsPolicy());

        final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
//        final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10);
//        final SynchronousQueue<Runnable> queue = new SynchronousQueue<>();
        final ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, queue, new ThreadFactory() {
            private AtomicInteger id = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "download thread #" + id.getAndIncrement());
                return thread;
            }
        });
        mThreadPool.prestartCoreThread();

        for (int i = 0; i < 24; i++) {
            final int index = i;
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(mThreadPool.toString());
//                    System.out.println("index: " + index  + ", thread id: "+Thread.currentThread().getName() + ", queue size:"+queue.size());
                }
            });
        }

        try {
            Thread.sleep(1000*60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}