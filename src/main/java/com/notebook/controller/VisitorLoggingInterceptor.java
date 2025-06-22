package com.notebook.controller;

import com.notebook.VisitorLog;
import com.notebook.VisitorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
public class VisitorLoggingInterceptor implements HandlerInterceptor {
    @Autowired
    private VisitorLogRepository visitorLogRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        VisitorLog log = new VisitorLog(ip, userAgent, LocalDateTime.now());
        visitorLogRepository.save(log);
        return true;
    }
} 