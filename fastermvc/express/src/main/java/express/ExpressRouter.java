package express;

import java.util.ArrayList;

import express.filter.DefaultHandler;
import express.filter.HandlerLayers;
import express.filter.HttpRequestHandler;
import express.http.Middleware;

/**
 * @author Simon Reinisch Basic implementation of an router
 */
public class ExpressRouter implements Router {

//	private final ArrayList<FilterWorker> workers;
	private final HandlerLayers handler;

	{
		// Initialize
//		workers = new ArrayList<>();
		handler = new HandlerLayers(2);
	}

	public ExpressRouter use(Middleware middleware) {
		addMiddleware("*", "*", middleware);
		return this;
	}

	public ExpressRouter use(String context, Middleware middleware) {
		addMiddleware("*", context, middleware);
		return this;
	}

	public ExpressRouter use(String context, String requestMethod, Middleware middleware) {
		addMiddleware(requestMethod.toUpperCase(), context, middleware);
		return this;
	}

	private void addMiddleware(String requestMethod, String context, Middleware middleware) {
//		if (middleware instanceof FilterTask) {
//			workers.add(new FilterWorker((FilterTask) middleware));
//		}

		handler.add(0, new DefaultHandler(requestMethod, context, middleware));
	}

	public ExpressRouter all(HttpRequestHandler request) {
		handler.add(1, new DefaultHandler("*", "*", request));
		return this;
	}

	public ExpressRouter all(String context, HttpRequestHandler request) {
		handler.add(1, new DefaultHandler("*", context, request));
		return this;
	}

	public ExpressRouter all(String context, String requestMethod, HttpRequestHandler request) {
		handler.add(1, new DefaultHandler(requestMethod, context, request));
		return this;
	}

	public ExpressRouter get(String context, HttpRequestHandler request) {
		handler.add(1, new DefaultHandler("GET", context, request));
		return this;
	}

	public ExpressRouter post(String context, HttpRequestHandler request) {
		handler.add(1, new DefaultHandler("POST", context, request));
		return this;
	}

	public ExpressRouter put(String context, HttpRequestHandler request) {
		handler.add(1, new DefaultHandler("PUT", context, request));
		return this;
	}

	public ExpressRouter delete(String context, HttpRequestHandler request) {
		handler.add(1, new DefaultHandler("DELETE", context, request));
		return this;
	}

	public ExpressRouter patch(String context, HttpRequestHandler request) {
		handler.add(1, new DefaultHandler("PATCH", context, request));
		return this;
	}

//	ArrayList<FilterWorker> getWorker() {
//		return workers;
//	}

	HandlerLayers getHandler() {
		return handler;
	}
}
