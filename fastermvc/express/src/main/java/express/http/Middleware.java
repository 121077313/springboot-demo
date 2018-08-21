package express.http;

import express.filter.HttpRequestHandler;

public abstract class Middleware implements HttpRequestHandler {

	public boolean before(Request req, Response res) {
		return true;
	}

	@Override
	public void handle(Request req, Response res) {
		throw new RuntimeException("no need implement HttpRequestHandler");
	}

	public boolean after(Request req, Response res) {
		return true;
	}

	abstract public String getName();
}
