package app;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

public class DispatcherServlet2 extends AbstractHandlerMapping{

	@Override
	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {
		
		return null;
	}


}
