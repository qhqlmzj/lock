package com.mascode.distribute;

import redis.clients.jedis.Jedis;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RedisDisLock implements DisLock {
    private static final String lockKeyBase = "redis:dis:lock:%s";
    private String callerHost;
    private String currentThread;
    private String lockKey;
    private boolean getLock = false;

    private Jedis jedis;

    public RedisDisLock(String lockId) {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            String processName = ManagementFactory.getRuntimeMXBean().getName();
            String processId = processName.substring(0, processName.indexOf('@'));
            callerHost = hostName + ":" + processId;

            lockKey = String.format(lockKeyBase, lockId);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean lock() {
        return false;
    }

    @Override
    public boolean tryLock() {
        return tryLock(false, 0);
    }

    @Override
    public boolean tryLock(long waitTime) {
        return tryLock(true, waitTime);
    }

    private boolean tryLock(boolean useTimeOut, long timeOut) {
        long start = System.currentTimeMillis();

        while (useTimeOut ? isTimeOut(start, timeOut) : true) {
            if (tryOnceLock()) {
                break;
            }
        }
        return getLock;
    }

    private boolean isTimeOut(long start, long timeOut) {
        return start + timeOut > System.currentTimeMillis();
    }

    private boolean tryOnceLock() {
        String callerThread = getCallerThread();
        if (currentThread == null) {
            currentThread = jedis.get(lockKey);
            return true;
        }
        if ("OK".equalsIgnoreCase(jedis.set(lockKey, callerThread, "nx", "px", 1000))) {
            currentThread = callerThread;
            getLock = true;
            return true;
        }
        return false;
    }

    private String getCallerThread() {
        return callerHost + ":" + Thread.currentThread().getId();
    }


    @Override
    public boolean unLock() {
        return false;
    }
}
