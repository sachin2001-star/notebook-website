package com.notebook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@Service
public class VisitorLoggingService {
    
    @Autowired
    private VisitorLogRepository visitorLogRepository;
    
    public void logVisitor(String pageVisited) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                String ipAddress = getClientIpAddress(request);
                String userAgent = request.getHeader("User-Agent");
                String referrer = request.getHeader("Referer");
                String sessionId = getOrCreateSessionId(request);
                
                // Create visitor log entry
                VisitorLog visitorLog = new VisitorLog(
                    ipAddress, 
                    userAgent, 
                    LocalDateTime.now(), 
                    pageVisited, 
                    referrer, 
                    sessionId
                );
                
                // Try to get location info (basic implementation)
                try {
                    Map<String, String> locationInfo = getLocationInfo(ipAddress);
                    visitorLog.setCountry(locationInfo.get("country"));
                    visitorLog.setCity(locationInfo.get("city"));
                } catch (Exception e) {
                    // If geolocation fails, continue without it
                    visitorLog.setCountry("Unknown");
                    visitorLog.setCity("Unknown");
                }
                
                // Save to database
                visitorLogRepository.save(visitorLog);
            }
        } catch (Exception e) {
            // Log error but don't break the application
            System.err.println("Error logging visitor: " + e.getMessage());
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private String getOrCreateSessionId(HttpServletRequest request) {
        String sessionId = (String) request.getSession().getAttribute("visitorSessionId");
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString().substring(0, 8);
            request.getSession().setAttribute("visitorSessionId", sessionId);
        }
        return sessionId;
    }
    
    private Map<String, String> getLocationInfo(String ipAddress) {
        // This is a basic implementation
        // In a production environment, you might want to use a service like MaxMind GeoIP2
        Map<String, String> locationInfo = new HashMap<>();
        
        // For now, we'll return basic info based on IP patterns
        if (ipAddress.startsWith("127.") || ipAddress.startsWith("192.168.") || ipAddress.startsWith("10.")) {
            locationInfo.put("country", "Local");
            locationInfo.put("city", "Local Network");
        } else {
            locationInfo.put("country", "Unknown");
            locationInfo.put("city", "Unknown");
        }
        
        return locationInfo;
    }
    
    // Analytics methods
    public long getTotalVisits() {
        return visitorLogRepository.count();
    }
    
    public long getTodayVisits() {
        return visitorLogRepository.countTodayVisits();
    }
    
    public long getWeekVisits() {
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        return visitorLogRepository.countWeekVisits(weekStart);
    }
    
    public long getMonthVisits() {
        LocalDateTime monthStart = LocalDateTime.now().minusDays(30);
        return visitorLogRepository.countMonthVisits(monthStart);
    }
    
    public List<Object[]> getVisitsByPage() {
        return visitorLogRepository.countVisitsByPage();
    }
    
    public List<Object[]> getVisitsByDeviceType() {
        return visitorLogRepository.countVisitsByDeviceType();
    }
    
    public List<Object[]> getVisitsByBrowser() {
        return visitorLogRepository.countVisitsByBrowser();
    }
    
    public List<Object[]> getVisitsByOperatingSystem() {
        return visitorLogRepository.countVisitsByOperatingSystem();
    }
    
    public List<VisitorLog> getRecentVisits() {
        return visitorLogRepository.findTop100ByOrderByVisitTimeDesc();
    }
    
    public List<String> getUniqueVisitorsToday() {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return visitorLogRepository.findUniqueVisitorsByDateRange(todayStart, todayEnd);
    }
} 