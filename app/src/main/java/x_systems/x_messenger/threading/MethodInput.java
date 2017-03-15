package x_systems.x_messenger.threading;

import java.util.concurrent.Callable;

/**
 * Created by jeremiah8100 on 5-12-2016.
 */

public class MethodInput implements Callable<Object> {
    Object[] args;
    MethodInput(Object[] args){
        this.args = args;
    }

    @Override
    public Object call() throws Exception {

        return null;
    }
}
