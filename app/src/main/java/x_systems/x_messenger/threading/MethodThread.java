package x_systems.x_messenger.threading;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

/**
 * Created by jeremiah8100 on 2-12-2016.
 */

public class MethodThread {

    private Semaphore locker = new Semaphore(0);
    private Object output;
    public Object Async(final Callable<Object> vm){
        new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    output = vm.call();
                    locker.release();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            locker.acquire();
            return output;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
