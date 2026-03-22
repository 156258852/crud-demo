package com.example.demo.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        long startTime = System.currentTimeMillis();

        log.info("[Filter] 请求开始 - {} {} 来自 {}",
                 req.getMethod(),
                 req.getRequestURI(),
                 req.getRemoteAddr());

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("[Filter] 请求结束 - {} {} - 耗时：{}ms - 状态码：{}",
                     req.getMethod(),
                     req.getRequestURI(),
                     duration,
                     ((jakarta.servlet.http.HttpServletResponse) response).getStatus());
        }
    }
}
