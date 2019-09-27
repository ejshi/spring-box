package com.sjz.mvc.core;

import javax.servlet.*;
import java.io.IOException;
import java.util.Optional;

/**
 * @author shijun.
 * @date 2019/9/26 14:40
 * @description 编码过滤
 */
public class CharacterEncodingFilter implements Filter {

    private String encoding;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        encoding = Optional.ofNullable(filterConfig.getServletContext().getInitParameter("encoding")).orElse("UTF-8");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        request.setCharacterEncoding(getEncoding());
        response.setCharacterEncoding(getEncoding());
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    public String getEncoding() {
        return encoding;
    }

}
