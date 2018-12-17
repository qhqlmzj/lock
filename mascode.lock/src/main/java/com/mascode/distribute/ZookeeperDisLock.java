package com.mascode.distribute;

public class ZookeeperDisLock implements DisLock {
    @Override
    public boolean lock() {
        return false;
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(int waitTime) {
        return false;
    }

    @Override
    public boolean unLock() {
        return false;
    }
}
