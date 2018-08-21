package express.middleware;

import express.filter.Filter;
import express.http.HttpRequestHandler;
import express.http.request.Request;
import express.http.response.Response;

public abstract class AbstractMiddleware implements HttpRequestHandler, Filter {

	public void before(Request req, Response res) {
	}

	public void after(Request req, Response res) {
	}

	@Override
	public abstract String getName();
	
	@Override
	public void handle(Request req, Response res) {
		
	}

}
