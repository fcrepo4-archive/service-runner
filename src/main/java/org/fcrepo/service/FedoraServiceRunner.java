package org.fcrepo.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FedoraServiceRunner implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(FedoraServiceRunner.class);
    
    public static final int SERVICE_STATE_STARTING = 1;

    public static final int SERVICE_STATE_RUNNING = 2;

    public static final int SERRVICE_STATE_ERROR = 3;

    private List<FedoraService> services;

    private boolean shutdown = false;

    public void setServices(List<FedoraService> services) {
        this.services = services;
    }

    @Override
    public void run() {
        final Map<String, Integer> serviceStates = new ConcurrentHashMap<String, Integer>();
        for (final FedoraService s : services) {
            Executors.newFixedThreadPool(1).submit(s);
            LOG.info("waiting for service " + s.getClass().getName());
            while (!s.isRunning()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void shutdown() {
        this.shutdown = true;
    }

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml");
        FedoraServiceRunner serviceRunner = (FedoraServiceRunner) ctx.getBean("fedoraServiceRunner");
        Thread t = new Thread(serviceRunner);
        t.start();
    }
}
