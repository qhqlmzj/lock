package com.mascode.spin;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

public class Test implements Runnable {
    static int sum;
    private Lock lock;
    private CountDownLatch countDownLatch;

    public Test(Lock lock, CountDownLatch countDownLatch) {
        this.lock = lock;
        this.countDownLatch = countDownLatch;
    }

    public static void main(String[] args) throws InterruptedException {
        Lock lock = new SpinLock();
        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            Test test = new Test(lock, countDownLatch);
            Thread t = new Thread(test);
            t.start();
        }

        Thread.currentThread().sleep(10000);
        System.out.println("rt:   " + sum);
    }

    @Override
    public void run() {
        countDownLatch.countDown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 10000; i++) {
            lock.lock();
            sum++;
            lock.unlock();
        }
        System.out.println(sum);
    }

}

