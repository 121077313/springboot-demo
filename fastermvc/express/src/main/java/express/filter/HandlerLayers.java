package express.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

import com.sun.net.httpserver.HttpExchange;

import express.Express;
import express.http.Middleware;
import express.http.Request;
import express.http.Response;

/**
 * @author Simon Reinisch
 *         <p>
 *         Handler for multiple FilterLayer.
 */
public class HandlerLayers {

//	public final HandlerLayer[] layers;
	
	public final List<DefaultHandler> middlewares = Collections.synchronizedList(new ArrayList<>());
	
	public final List<DefaultHandler> handlers = Collections.synchronizedList(new ArrayList<>());
	
	

	public HandlerLayers() {

		// Create & initialize layers
//		this.layers = new HandlerLayer[layers];
//		for (int i = 0; i < this.layers.length; i++)
//			this.layers[i] = new HandlerLayer<>();
	}

	/** 消息入口 */
	public void handle2(HttpExchange httpExchange, Express express) {
		Request request = new Request(httpExchange, express);
		Response response = new Response(httpExchange);

		// First fire all middleware's, then the normal request filter

//		for (HandlerLayer chain : layers) {
//			// chain.filter(request, response);
//
//			if (response.isClosed()) {
//				return;
//			}
//		}
	}

	/** 消息入口 */
	public void handle(HttpExchange httpExchange, Express express) {

		try {

			Request req = new Request(httpExchange, express);
			Response res = new Response(httpExchange);

			// First fire all middleware's, then the normal request filter

//			HandlerLayer middlewares = layers[0];

			ListIterator<DefaultHandler> middlewareIter = middlewares
					.listIterator();

			while (!res.isClosed() && middlewareIter.hasNext()) {
				// (((DefaultHandler) iter.next())).invoke(req, res);

				middlewareIter.next().invoke(req, res);
			}

//			HandlerLayer handers = layers[1];
			// handers.filter(req, res);

			ListIterator<DefaultHandler> iter2 = handlers.listIterator();

			while (!res.isClosed() && iter2.hasNext()) {
				iter2.next().invoke(req, res);
			}

			while (!res.isClosed() && middlewareIter.hasPrevious()) {
				((Middleware) middlewareIter.previous().handler).after(req, res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add an new handler for an specific handler layers.
	 *
	 * @param level
	 *            The layers.
	 * @param handler
	 *            The handler, will be append to the top of the layers.
	 */
	public void add(int level, DefaultHandler handler) {

		
		if(level==0){
			middlewares.add(handler);
		}else if(level==1){
			handlers.add(handler);
		}

//		layers[level].filters.add(handler);
	}

	/**
	 * Merge two FilterLayerHandler
	 *
	 * @param filterLayerHandler
	 *            The FilterLayerHandler which you want to merge with this
	 */
	public void combine(HandlerLayers filterLayerHandler) {
		if (filterLayerHandler != null) {
			middlewares.addAll(filterLayerHandler.middlewares);
			handlers.addAll(filterLayerHandler.handlers);
			
			
//			HandlerLayer[] chains = filterLayerHandler.layers;
//
//			if (chains.length != layers.length)
//				throw new RuntimeException(
//						"Cannot add an filterLayerHandler with different layers sizes: "
//								+ chains.length + " != " + layers.length);
//
//			for (int i = 0; i < chains.length; i++)
//				layers[i].filters.addAll(chains[i].filters);
		}
	}

	/**
	 * Iterate over the different FilterLayer
	 *
	 * @param layerConsumer
	 *            An consumer for the layers
	 */
//	public void forEach(Consumer<HandlerLayer> layerConsumer) {
//		if (layerConsumer == null)
//			return;
//
//		for (HandlerLayer layer : layers)
//			layerConsumer.accept(layer);
//	}

	public void setRoot(String root) {
		
		for(DefaultHandler d:middlewares){
			d.setRoot(root);
		}
		for(DefaultHandler d:handlers){
			d.setRoot(root);
		}
		
		
	}

}
