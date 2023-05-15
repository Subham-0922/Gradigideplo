package com.gradigi.sorting;

import com.gradigi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoggingFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		
		LOGGER.info("Request [{} {} {}]", req.getMethod(), req.getRequestURI(),request.getRemoteAddr());
		
		chain.doFilter(request, response);
		HttpServletResponse res = (HttpServletResponse) response;
		LOGGER.info("Response [{}]", res.getStatus());
	}

}
