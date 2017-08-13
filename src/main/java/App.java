import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Stanislav Rymar
 */
public class App {
    private final static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) {

        //Create fixedThreadPool and set all thread to demon
        ExecutorService executorService = Executors.newFixedThreadPool(20, new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                thread.setDaemon(true);
                return thread;
            }
        });

        List<Callable<String>> callable = new ArrayList<Callable<String>>();
        init(callable);

        try {
            logger.info("Start bruteforce!");
            String result = executorService.invokeAny(callable);

            //print correct result
            /*
                    This text will be printed in console
                    Correct link is https://en.wikipedia.org/wiki/Hot_air_balloon
                    Correct password is 1783
             */
            logger.info(result);
        } catch (InterruptedException e) {
            logger.error("InterruptedException: Something going wrong", e);
        } catch (ExecutionException e) {
            logger.error("ExecutionException: Something going wrong ", e);
        }


    }

    private static void init(List<Callable<String>> list) {
        logger.info("Initializing ...");

        for (int i = 0; i < 20; i++) {
            list.add(new BruteforceThread());
        }
    }
}
