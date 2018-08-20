package app;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import app.controller.UserController;

@WebFilter(urlPatterns = "/api/*")
public class SimpleFilter implements Filter {
	private static final Logger logger = LoggerFactory
			.getLogger(SimpleFilter.class);
	@Autowired
	UserController userController;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		String url = ((HttpServletRequest) request).getRequestURI().toString();
		logger.info("filter:" + url);

		if (url.equals("/api/aa")) {
			Object result = userController.get(Integer.parseInt(request
					.getParameter("id")));
			
		}else{
			chain.doFilter(request, response);
		}
	
	}

	void url(String url) {

	}

	@Override
	public void destroy() {
	}
}