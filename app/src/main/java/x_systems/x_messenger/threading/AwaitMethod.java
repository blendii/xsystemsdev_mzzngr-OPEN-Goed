package x_systems.x_messenger.threading;

import java.util.concurrent.Semaphore;

/**
 * Created by Manasseh on 11/17/2016.
 */

public class AwaitMethod {
    private final Semaphore semaphore = new Semaphore(0);
    private MethodWrapper<String> methodWrapper;
    private String returnValue = "";

    public AwaitMethod(MethodWrapper<String> methodWrapper)
    {
        this.methodWrapper = methodWrapper;
    }

    public String run() {
        new Thread(new Runnable () {
            @Override
            public void run() {
                returnValue = methodWrapper.method();
                semaphore.release();
            }
        }).start();

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            System.out.println("An error occured");
            e.printStackTrace();
        }
        return returnValue;
    }
}
