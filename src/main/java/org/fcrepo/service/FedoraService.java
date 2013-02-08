package org.fcrepo.service;

import java.util.concurrent.Callable;

public interface FedoraService extends Callable<Object>{
    public void stopService();
    public boolean isRunning();
}
