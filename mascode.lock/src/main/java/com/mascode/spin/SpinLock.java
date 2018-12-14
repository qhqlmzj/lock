package com.mascode.spin;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SpinLock implements Lock {
    private AtomicReference<Thread> owner = new AtomicReference<>();

    @Override
    public void lock() {
        tryLock(0, false);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        return tryLock(0, false);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return tryLock(time, true);
    }


    @Override
    public void unlock() {
        final Thread current = Thread.currentThread();
        owner.compareAndSet(current, null);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }


    private boolean tryLock(long timeOut, boolean useTimeOut) {
        long now = System.currentTimeMillis();
        long to = now + timeOut;
        final Thread current = Thread.currentThread();
        final Thread lockOwner = owner.get();
        if (current.equals(lockOwner)) {
            return true;
        }
        while (!owner.compareAndSet(null, current)) {
            if (useTimeOut && System.currentTimeMillis() > to) {
                return false;
            }
        }
        return true;
    }

}
