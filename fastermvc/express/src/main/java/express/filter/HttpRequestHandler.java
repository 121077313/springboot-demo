package express.filter;

import express.http.Request;
import express.http.Response;

/**
 * @author Simon Reinisch Interface to handle an http-request
 */
@FunctionalInterface
public interface HttpRequestHandler {

	/**
	 * Handle an http-request
	 *
	 * @param req - The request object
	 * @param res - The response object
	 */
	public void handle(Request req, Response res);
	
}
