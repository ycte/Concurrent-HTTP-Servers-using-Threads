/* 
 *
 * XMU CNNS Class Demo
 */
import java.io.*;
import java.util.*;
import java.net.*;

public class Server {
  
    private ServerSocket welcomeSocket;
	
    public static int THREAD_COUNT = 10;
    private ServiceThread[] threads;

	public static int cacheSize = 8096;

	public static int reqCnt = 0;

	static HashMap<String, String> cfgMap = new HashMap<String, String>();
	static Map<String, String> fileCache = new HashMap<>();

    /* Constructor: starting all threads at once */
    public Server(int serverPort) {

	    try {
	        // create server socket
	        welcomeSocket = new ServerSocket(serverPort);
	        System.out.println("Server started; listening at " + serverPort);

	        // create thread pool
	        threads = new ServiceThread[THREAD_COUNT];

	        // start all threads
	        for (int i = 0; i < threads.length; i++) {
			    threads[i] = new ServiceThread(welcomeSocket, cfgMap, fileCache, cacheSize);
			    threads[i].start();
	        }
	    } catch (Exception e) {
	        System.out.println("Server construction failed.");
	    } // end of catch

    } // end of Server

    public static void main(String[] args) throws IOException {

	   // see if we do not use default server port
	   int serverPort = 6789;

		// see if there is .conf
		cfgMap.put("vb_default", "./");
		String confName = "httpd.conf";
		if (args.length >= 2)
			if (args[0].equals("-config")) {
				confName = args[1];
				FileTest cfg = new FileTest();
				String cfgFileContent = cfg.cfgRead(confName);
				cfgMap = cfg.generateCfgMap(cfgFileContent);
				serverPort = Integer.parseInt(cfgMap.get("Listen"));
				cacheSize = Integer.parseInt(cfgMap.get("CacheSize"));
				THREAD_COUNT = Integer.parseInt(cfgMap.get("ThreadPoolSize"));
				System.out.println(cfgMap.get("vb_yunxi.site"));
				System.out.println(cfgMap.toString());
			}
	   Server server = new Server(serverPort);
	   server.run();
	   
    } // end of main

    // Wait for all threads to finish
    public void run() {

	    try {
	        for (int i = 0; i < threads.length; i++) {
			    threads[i].join();
	        }
	        System.out.println("All threads finished. Exit");
	    } catch (Exception e) {
	        System.out.println("Join errors");
	    } // end of catch
			
    } // end of run

} // end of class
