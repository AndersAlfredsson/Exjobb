package com.Modelclasses.LoginServerclasses;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gustav on 2016-04-18.
 * Class for handling connections for users with the help of an ExecutorService
 * and executors. Also using a ServerSocket for starting communications with the clients
 */
public class LoginServer implements Runnable
{
    private final ExecutorService pool;
    private final ServerSocket socket;

    public LoginServer(int poolSize, int port) throws IOException
    {
        this.socket = new ServerSocket(port);
        this.pool = Executors.newFixedThreadPool(poolSize);
    }

    /**
     * Function for shutting down the executorService, it waits for all threads to complete
     * their computations for 20 seconds, if unsuccessful it waits for 20 more seconds and then
     * forcing them to quit.
     * @param pool
     */
    public void shutdownAndAwaitTermination(ExecutorService pool)
    {
        try
        {
            if(!pool.awaitTermination(20, TimeUnit.SECONDS))
            {
                pool.shutdownNow();
                if(!pool.awaitTermination(20, TimeUnit.SECONDS))
                {
                    System.err.println("Pool did not terminate correctly");
                }
            }
        }
        catch(InterruptedException ie)
        {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * The threaded ServerListener, it accepts connections and starts a thread for the communication
     */
    @Override
    public void run()
    {
        try
        {
            System.out.println("ServerListener has started...");
            while(true)
            {
                try
                {
                    pool.execute(new UserHandler(this.socket.accept()));
                }
                catch (IOException e)
                {
                    pool.shutdown();
                }
            }
        }
        catch(Exception e)
        {
            shutdownAndAwaitTermination(pool);
        }
    }
}
