package com.dongyin.ecommerce.dl.gateway.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DlGwFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(DlGwFilter.class);

    private String dlDomain = "http://wxtest1.yyzws.com";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse resp = (HttpServletResponse)servletResponse;
        String reqUrl = req.getRequestURL().toString();
        log.info("[{}] reqUrl:{}",reqUrl);
        this.crossDomain(resp, reqUrl);

        filterChain.doFilter(req, resp);
    }

    private void crossDomain(HttpServletResponse response, String url) {
        log.info("[{}] dlDomain:{}",dlDomain);
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", this.dlDomain);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With");
    }

    @Override
    public void destroy() {
    }
}
