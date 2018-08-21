package express.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import express.http.Middleware;
import express.http.Request;
import express.http.Response;

/**
 * @author Simon Reinisch
 *         <p>
 *         An http-filter to extract data and check if an context matches the
 *         request.
 */
public class DefaultHandler {

	public final Object handler;
	private final String req;
	private final String context;
	private final boolean reqAll;
	private final boolean contextAll;

	private String root;
	private String fullContext;

	public DefaultHandler(String requestMethod, String context, Object handler) {
		this.req = requestMethod;
		this.handler = handler;
		this.context = normalizePath(context);

		// Save some information's which don't need to be processed again
		this.reqAll = requestMethod.equals("*");
		this.contextAll = context.equals("*");

		this.root = "/";
		this.fullContext = this.context;
	}
	
	

	public void setRoot(String root) {
		if (root == null || root.isEmpty())
			return;

		if (root.charAt(0) != '/')
			root = '/' + root;

		if (root.charAt(root.length() - 1) != '/')
			root += '/';

		this.root = normalizePath(root);
		this.fullContext = normalizePath(this.root + context);
	}

	public void invoke(Request req, Response res) {
		String requestMethod = req.getMethod();
		String requestPath = req.getURI().getRawPath();
		ConcurrentHashMap<String, HttpRequestHandler> parameterListener = req.getApp().getParameterListener();

		if (!(reqAll || this.req.equals(requestMethod))) {
			return;
		} else if (contextAll) {
			req.setContext(context);

			if (handler instanceof Middleware) {
				((Middleware) handler).before(req, res);
				return;
			}

			((HttpRequestHandler) handler).handle(req, res);
			return;
		}

		// Parse params
		HashMap<String, String> params = matchURL(fullContext, requestPath);
		if (params == null)
			return;

		// Save parameter to request object
		req.setParams(params);

		// Check parameter listener
		params.forEach((s, s2) -> {
			HttpRequestHandler request = parameterListener.get(s);

			if (request != null) {

				request.handle(req, res);
			}
		});

		// Check if the response is closed
		if (res.isClosed())
			return;

		// Handle request
		req.setContext(context);

		if (handler instanceof Middleware) {
			((Middleware) handler).before(req, res);
			return;
		}
		((HttpRequestHandler) handler).handle(req, res);
	}
	
	
	

	private HashMap<String, String> matchURL(String filter, String url) {
		HashMap<String, String> params = new HashMap<>();

		if (url.equals(filter)) {
			return params;
		}
		
		if (!"/".equals(filter)&&url.startsWith(filter)) {
			return params;
		}
		

		return null;
	}

	private HashMap<String, String> matchURL2(String filter, String url) {
		HashMap<String, String> params = new HashMap<>();

		if (url.startsWith(filter)) {

			return params;
		}

		return null;
	}

	/**
	 * Extract and match the parameter from the url with an filter.
	 */
	private HashMap<String, String> matchURL3(String filter, String url) {
		HashMap<String, String> params = new HashMap<>();
		StringBuilder key = new StringBuilder();
		StringBuilder val = new StringBuilder();
		char[] uc = url.toCharArray();
		char[] fc = filter.toCharArray();
		int ui = 0, fi = 0;

		for (; fi < fc.length; fi++, ui++) {

			if (fc[fi] == ':') {
				key.setLength(0);
				val.setLength(0);

				fi++;
				while (fi < fc.length && fc[fi] != '/')
					key.append(fc[fi++]);

				while (ui < uc.length && uc[ui] != '/')
					val.append(uc[ui++]);

				try {
					String decVal = URLDecoder.decode(val.toString(), "UTF8");
					params.put(key.toString(), decVal);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

			} else if (ui == uc.length || fc[fi] != uc[ui]) {

				// Failed
				return null;
			}
		}

		if (ui < url.length() || fi < filter.length()) {
			return null;
		}

		return params;
	}

	/**
	 * Replace all double slashes from an string with an single slash
	 */
	private String normalizePath(String context) {
		if (context == null || context.length() == 1)
			return context;

		StringBuilder sb = new StringBuilder();
		char[] chars = context.toCharArray();

		sb.append(chars[0]);
		for (int i = 1; i < chars.length; i++) {
			if ((chars[i] == '/' && chars[i - 1] != '/') || chars[i] != '/')
				sb.append(chars[i]);
		}

		return sb.toString();
	}

}
