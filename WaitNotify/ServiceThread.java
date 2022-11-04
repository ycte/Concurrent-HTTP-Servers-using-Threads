/*
 * 
 * XMU CNNS CLass Demo
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class ServiceThread extends Thread {

	private List<Socket> pool;

	Map<String, String> fileCache = new HashMap<>();
	int cacheSize = 8096;
	Map<String, String> cfgMap = new HashMap<>();

	public ServiceThread(List<Socket> pool, Map<String, String> cfgMap,
						 Map<String, String> fileCache, int cacheSize) {
		this.pool = pool;
		this.cfgMap = cfgMap;
		this.fileCache = fileCache;
		this.cacheSize = cacheSize;

	}
  
	public void run() {

		while (true) {
			// get a new request connection
			Socket s = null;

			synchronized (pool) {         
				while (pool.isEmpty()) {
					try {
						System.out.println("Thread " + this + " sees empty pool.");
						pool.wait();
					}
					catch (InterruptedException ex) {
						System.out.println("Waiting for pool interrupted.");
					} // end of catch
				} // end of while
				
				// remove the first request
				s = (Socket) pool.remove(0);
				System.out.println("Receive request from " + s);

				// process a request
				WebRequestHandler wrh =
						null;
				try {
					wrh =
						new WebRequestHandler( s, cfgMap, fileCache, cacheSize);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				wrh.processRequest();
				System.out.println("Thread " + this 
								   + " process request " + s);
			} // end of extract a request

//			serveARequest( s );
			
		} // end while
		
	} // end run

	private void serveARequest(Socket connSock) {
		try {
			// create read stream to get input
			BufferedReader inFromClient = 
				new BufferedReader(new InputStreamReader(connSock.getInputStream()));
			String clientSentence = inFromClient.readLine();

			// process input
			String capitalizedSentence = clientSentence.toUpperCase() + '\n';

			// send reply
			DataOutputStream outToClient = new DataOutputStream(connSock.getOutputStream());
			outToClient.writeBytes(capitalizedSentence);
		} catch (Exception e) {
		}
	} // end of serveARequest

} // end ServiceThread
