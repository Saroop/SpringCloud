package com.example.demo.filter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class ZuulPostFilter extends ZuulFilter {
	
	@Value("${log.file.location:c:/temp/log.txt}")
    private String logFile;

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext requestContext = RequestContext.getCurrentContext();
		HttpServletRequest httpServletRequest = requestContext.getRequest();
		try {
			String requestBody = StreamUtils.copyToString(httpServletRequest.getInputStream(),
					Charset.forName("UTF-8"));
			String responsBody = StreamUtils.copyToString(requestContext.getResponseDataStream(),
					Charset.forName("UTF-8"));
			requestContext.setResponseBody(responsBody);
			Path path = Paths.get(logFile);
			if (!Files.exists(path)) {
				Files.createFile(path);
			}
			BufferedWriter bw = Files.newBufferedWriter(path,
	                Charset.forName("UTF-8"), StandardOpenOption.APPEND);
	        bw.write("Request:" + requestBody + "\nResponse:" + responsBody);
	        bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String filterType() {
		return FilterConstants.POST_TYPE;
	}

	@Override
	public int filterOrder() {
		return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
	}

}
