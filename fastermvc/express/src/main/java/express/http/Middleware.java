package express.http;

import express.filter.HttpRequestHandler;

public abstract class Middleware   {

	public void before(Request req, Response res) {
	}

//	public void handle(Request req, Response res) {
//		throw new RuntimeException("no need implement HttpRequestHandler");
//	}

	public void after(Request req, Response res) {
	}

	abstract public String getName();
}
