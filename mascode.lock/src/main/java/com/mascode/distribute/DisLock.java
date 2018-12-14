package com.mascode.distribute;

public interface DisLock {

    /**
     * 阻塞的去获取锁
     */
    boolean lock();

    boolean tryLock();

    boolean tryLock(int waitTime);

    boolean unLock();
}
