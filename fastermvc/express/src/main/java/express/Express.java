package express;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import express.filter.DefaultHandler;
import express.filter.HandlerLayers;
import express.filter.HttpRequestHandler;
import express.http.Middleware;

/**
 * @author Simon Reinisch Core class of java-express
 */
public class Express implements Router {

	public ConcurrentHashMap<String, HttpRequestHandler> parameterListener;
	public ConcurrentHashMap<Object, Object> locals;

//	public  ArrayList<FilterWorker> worker;
	public HandlerLayers layers;

	public Executor executor;
	public String hostname;
	public HttpServer httpServer;
	public HttpsConfigurator httpsConfigurator;

	{
		// Initialize
		parameterListener = new ConcurrentHashMap<>();
		locals = new ConcurrentHashMap<>();

//		worker = new ArrayList<>();
		layers = new HandlerLayers(2);

		executor = Executors.newCachedThreadPool();
	}

	/**
	 * Create an express instance and bind the server to an hostname. Default is
	 * "Localhost"
	 *
	 * @param hostname The host name
	 */
	public Express(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Default, will bind the server to "localhost"
	 *
	 * @param httpsConfigurator The HttpsConfigurator for https
	 */
	public Express(HttpsConfigurator httpsConfigurator) {
		this.httpsConfigurator = httpsConfigurator;
	}

	/**
	 * Create an express instance and bind the server to an hostname. Default is
	 * "Localhost"
	 *
	 * @param hostname          The host name
	 * @param httpsConfigurator The HttpsConfigurator for https
	 */
	public Express(String hostname, HttpsConfigurator httpsConfigurator) {
		this.hostname = hostname;
		this.httpsConfigurator = httpsConfigurator;
	}

	/**
	 * Default, will bind the server to "localhost"
	 */
	public Express() {
	}

	/**
	 * @return True if the server uses https.
	 */
	public boolean isSecure() {
		return httpsConfigurator != null;
	}

	/**
	 * Add a listener which will be called when an url with this parameter is
	 * called.
	 *
	 * @param param   The parameter name.
	 * @param request An request handler.
	 */
	public Express onParam(String param, HttpRequestHandler request) {
		parameterListener.put(param, request);
		return this;
	}

	public ConcurrentHashMap<String, HttpRequestHandler> getParameterListener() {
		return parameterListener;
	}

	/**
	 * Add an key-val pair to the express app, can be used to store data. Uses
	 * ConcurrentHashMap so it's thread save.
	 *
	 * @param key The key
	 * @param val The value
	 * @return The last value which was attached by this key, can be null.
	 */
	public Object set(String key, String val) {
		return locals.put(key, val);
	}

	/**
	 * Returns the value which was allocated by this key.
	 *
	 * @param key The key.
	 * @return The value.
	 */
	public Object get(String key) {
		return locals.get(key);
	}

	/**
	 * Set an executor service. Default is CachedThreadPool Can only changed if the
	 * server isn't already stardet.
	 *
	 * @param executor The new executor.
	 * @throws IOException If the server is currently running
	 */
	public void setExecutor(Executor executor) throws IOException {
		if (httpServer != null) {
			throw new IOException("Cannot set the executor after the server has starderd!");
		} else {
			this.executor = executor;
		}
	}

	/**
	 * Add an routing object.
	 *
	 * @param router The router.
	 */
	public Express use(ExpressRouter router) {
		this.layers.combine(router.getHandler());
//		this.worker.addAll(router.getWorker());
		return this;
	}

	/**
	 * Add an routing object with an specific root root.
	 *
	 * @param root   The root path for all request to this router.
	 * @param router The router.
	 */
	public Express use(String root, ExpressRouter router) {

		router.getHandler().forEach(fl -> fl.filters.forEach(layer -> {
			((DefaultHandler) layer).setRoot(root);
		}));

		this.layers.combine(router.getHandler());
//		this.worker.addAll(router.getWorker());

		return this;
	}

	public Express use(Middleware middleware) {
		addMiddleware("*", "*", middleware);
		return this;
	}

	public Express use(String context, Middleware middleware) {
		addMiddleware("*", context, middleware);
		return this;
	}

	public Express use(String context, String requestMethod, Middleware middleware) {
		addMiddleware(requestMethod.toUpperCase(), context, middleware);
		return this;
	}

	// Internal service to handle middleware
	private void addMiddleware(String requestMethod, String context, Middleware middleware) {
//		if (middleware instanceof FilterTask) {
//			worker.add(new FilterWorker((FilterTask) middleware));
//		}

		layers.add(0, new DefaultHandler(requestMethod, context, middleware));

	}

	public Express all(HttpRequestHandler request) {
		layers.add(1, new DefaultHandler("*", "*", request));
		return this;
	}

	public Express all(String context, HttpRequestHandler request) {
		layers.add(1, new DefaultHandler("*", context, request));
		return this;
	}

	public Express all(String context, String requestMethod, HttpRequestHandler request) {
		layers.add(1, new DefaultHandler(requestMethod, context, request));
		return this;
	}

	public Express get(String context, HttpRequestHandler request) {
		layers.add(1, new DefaultHandler("GET", context, request));
		return this;
	}

	public Express post(String context, HttpRequestHandler request) {
		layers.add(1, new DefaultHandler("POST", context, request));
		return this;
	}

	public Express put(String context, HttpRequestHandler request) {
		layers.add(1, new DefaultHandler("PUT", context, request));
		return this;
	}

	public Express delete(String context, HttpRequestHandler request) {
		layers.add(1, new DefaultHandler("DELETE", context, request));
		return this;
	}

	public Express patch(String context, HttpRequestHandler request) {
		layers.add(1, new DefaultHandler("PATCH", context, request));
		return this;
	}

	/**
	 * Start the HTTP-Server on a specific port This method is asynchronous so be
	 * sure to add an listener or keep it in mind!
	 *
	 * @param port The port.
	 */
	public void listen(int port) {
		listen(port, null);
	}

	/**
	 * Start the HTTP-Server on a specific port. This method is asynchronous so be
	 * sure to add an listener or keep it in mind.
	 *
	 * @param onStart An listener which will be fired after the server is stardet.
	 * @param port    The port.
	 */
	public void listen(int port, Runnable onStart) {
		new Thread(() -> {
			try {

				// Fire worker threads
//				worker.forEach(FilterWorker::start);

				InetSocketAddress socketAddress = this.hostname == null ? new InetSocketAddress(port)
						: new InetSocketAddress(this.hostname, port);
				if (httpsConfigurator != null) {

					// Create https server
					httpServer = HttpsServer.create(socketAddress, 0);
					((HttpsServer) httpServer).setHttpsConfigurator(httpsConfigurator);
				} else {

					// Create http server
					httpServer = HttpServer.create(socketAddress, 0);
				}

				// Set thread executor
				httpServer.setExecutor(executor);

				// Create handler for all contexts
				httpServer.createContext("/", exchange -> layers.handle(exchange, this));

				// Start server
				httpServer.start();

				// Fire listener
				if (onStart != null)
					onStart.run();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Stop express
	 */
	public void stop() {
		if (httpServer != null) {

			// Stop http-server
			httpServer.stop(0);

			// Stop worker threads
//			worker.forEach(FilterWorker::stop);
		}
	}

}
