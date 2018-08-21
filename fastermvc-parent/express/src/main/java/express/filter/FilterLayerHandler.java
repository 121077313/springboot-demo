package express.filter;

import java.util.ListIterator;
import java.util.function.Consumer;

import com.sun.net.httpserver.HttpExchange;

import express.Express;
import express.ExpressException;
import express.http.HttpRequestHandler;
import express.http.request.Request;
import express.http.response.Response;
import express.middleware.AbstractMiddleware;

/**
 * @author Simon Reinisch
 *         <p>
 *         Handler for multiple FilterLayer.
 */
public class FilterLayerHandler {

	public final FilterLayer[] layers;

	public FilterLayerHandler(int layers) {

		// Create & initialize layers
		this.layers = new FilterLayer[layers];
		for (int i = 0; i < this.layers.length; i++)
			this.layers[i] = new FilterLayer<>();
	}

	/** 消息入口 */
	public void handle2(HttpExchange httpExchange, Express express) {
		Request request = new Request(httpExchange, express);
		Response response = new Response(httpExchange);

		// First fire all middleware's, then the normal request filter

		for (FilterLayer chain : layers) {
			chain.filter(request, response);

			if (response.isClosed()) {
				return;
			}
		}
	}

	/** 消息入口 */
	public void handle(HttpExchange httpExchange, Express express) {
		Request req = new Request(httpExchange, express);
		Response res = new Response(httpExchange);

		// First fire all middleware's, then the normal request filter

		FilterLayer middlewares = layers[0];

		ListIterator<AbstractMiddleware> iter = middlewares.filter.listIterator();

		while (!res.isClosed() && iter.hasNext()) {
			iter.next().before(req, res);
		}

		FilterLayer handers = layers[1];
		handers.filter(req, res);

		while (!res.isClosed() && iter.hasPrevious()) {
			iter.previous().after(req, res);
		}

	}

	/**
	 * Add an new handler for an specific handler layers.
	 *
	 * @param level   The layers.
	 * @param handler The handler, will be append to the top of the layers.
	 */
	public void add(int level, HttpRequestHandler handler) {

		if (level >= layers.length)
			throw new IndexOutOfBoundsException("Out of bounds: " + level + " > " + layers.length);
		if (level < 0)
			throw new IndexOutOfBoundsException("Cannot be under zero: " + level + " < 0");

		layers[level].filter.add(handler);
	}

	/**
	 * Merge two FilterLayerHandler
	 *
	 * @param filterLayerHandler The FilterLayerHandler which you want to merge with
	 *                           this
	 */
	public void combine(FilterLayerHandler filterLayerHandler) {
		if (filterLayerHandler != null) {
			FilterLayer[] chains = filterLayerHandler.layers;

			if (chains.length != layers.length)
				throw new ExpressException("Cannot add an filterLayerHandler with different layers sizes: "
						+ chains.length + " != " + layers.length);

			for (int i = 0; i < chains.length; i++)
				layers[i].filter.addAll(chains[i].filter);
		}
	}

	/**
	 * Iterate over the different FilterLayer
	 *
	 * @param layerConsumer An consumer for the layers
	 */
	public void forEach(Consumer<FilterLayer> layerConsumer) {
		if (layerConsumer == null)
			return;

		for (FilterLayer layer : layers)
			layerConsumer.accept(layer);
	}

}
