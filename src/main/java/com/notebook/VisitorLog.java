package com.notebook;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
// import jakarta.persistence.*; // Uncomment if using Jakarta EE
import java.time.LocalDateTime;

@Entity
@jakarta.persistence.Table(name = "visitor_logs")
public class VisitorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ipAddress;
    
    @Column(length = 1000)
    private String userAgent;
    
    @Column(nullable = false)
    private LocalDateTime visitTime;
    
    @Column(length = 200)
    private String pageVisited;
    
    @Column(length = 500)
    private String referrer;
    
    @Column(length = 100)
    private String country;
    
    @Column(length = 100)
    private String city;
    
    @Column(length = 50)
    private String deviceType; // mobile, desktop, tablet
    
    @Column(length = 100)
    private String browser;
    
    @Column(length = 100)
    private String operatingSystem;
    
    @Column(length = 20)
    private String sessionId;

    public VisitorLog() {}

    public VisitorLog(String ipAddress, String userAgent, LocalDateTime visitTime) {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.visitTime = visitTime;
    }

    // Enhanced constructor
    public VisitorLog(String ipAddress, String userAgent, LocalDateTime visitTime, 
                     String pageVisited, String referrer, String sessionId) {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.visitTime = visitTime;
        this.pageVisited = pageVisited;
        this.referrer = referrer;
        this.sessionId = sessionId;
        this.deviceType = parseDeviceType(userAgent);
        this.browser = parseBrowser(userAgent);
        this.operatingSystem = parseOperatingSystem(userAgent);
    }

    // Helper methods to parse user agent
    private String parseDeviceType(String userAgent) {
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "Mobile";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "Tablet";
        } else {
            return "Desktop";
        }
    }

    private String parseBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("chrome")) return "Chrome";
        if (userAgent.contains("firefox")) return "Firefox";
        if (userAgent.contains("safari")) return "Safari";
        if (userAgent.contains("edge")) return "Edge";
        if (userAgent.contains("opera")) return "Opera";
        return "Unknown";
    }

    private String parseOperatingSystem(String userAgent) {
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows")) return "Windows";
        if (userAgent.contains("mac")) return "macOS";
        if (userAgent.contains("linux")) return "Linux";
        if (userAgent.contains("android")) return "Android";
        if (userAgent.contains("ios")) return "iOS";
        return "Unknown";
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public LocalDateTime getVisitTime() { return visitTime; }
    public void setVisitTime(LocalDateTime visitTime) { this.visitTime = visitTime; }
    
    public String getPageVisited() { return pageVisited; }
    public void setPageVisited(String pageVisited) { this.pageVisited = pageVisited; }
    
    public String getReferrer() { return referrer; }
    public void setReferrer(String referrer) { this.referrer = referrer; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    
    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }
    
    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
} 