package com.notebook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {
    
    // Find visits by date range
    List<VisitorLog> findByVisitTimeBetweenOrderByVisitTimeDesc(LocalDateTime start, LocalDateTime end);
    
    // Find visits by page
    List<VisitorLog> findByPageVisitedOrderByVisitTimeDesc(String pageVisited);
    
    // Find visits by device type
    List<VisitorLog> findByDeviceTypeOrderByVisitTimeDesc(String deviceType);
    
    // Find visits by browser
    List<VisitorLog> findByBrowserOrderByVisitTimeDesc(String browser);
    
    // Find visits by operating system
    List<VisitorLog> findByOperatingSystemOrderByVisitTimeDesc(String operatingSystem);
    
    // Find unique visitors (by IP) in date range
    @Query("SELECT DISTINCT v.ipAddress FROM VisitorLog v WHERE v.visitTime BETWEEN :start AND :end")
    List<String> findUniqueVisitorsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Count visits by page
    @Query("SELECT v.pageVisited, COUNT(v) FROM VisitorLog v GROUP BY v.pageVisited ORDER BY COUNT(v) DESC")
    List<Object[]> countVisitsByPage();
    
    // Count visits by device type
    @Query("SELECT v.deviceType, COUNT(v) FROM VisitorLog v GROUP BY v.deviceType ORDER BY COUNT(v) DESC")
    List<Object[]> countVisitsByDeviceType();
    
    // Count visits by browser
    @Query("SELECT v.browser, COUNT(v) FROM VisitorLog v GROUP BY v.browser ORDER BY COUNT(v) DESC")
    List<Object[]> countVisitsByBrowser();
    
    // Count visits by operating system
    @Query("SELECT v.operatingSystem, COUNT(v) FROM VisitorLog v GROUP BY v.operatingSystem ORDER BY COUNT(v) DESC")
    List<Object[]> countVisitsByOperatingSystem();
    
    // Get recent visits
    List<VisitorLog> findTop100ByOrderByVisitTimeDesc();
    
    // Count total visits
    long count();
    
    // Count visits today
    @Query("SELECT COUNT(v) FROM VisitorLog v WHERE DATE(v.visitTime) = CURRENT_DATE")
    long countTodayVisits();
    
    // Count visits this week
    @Query("SELECT COUNT(v) FROM VisitorLog v WHERE v.visitTime >= :weekStart")
    long countWeekVisits(@Param("weekStart") LocalDateTime weekStart);
    
    // Count visits this month
    @Query("SELECT COUNT(v) FROM VisitorLog v WHERE v.visitTime >= :monthStart")
    long countMonthVisits(@Param("monthStart") LocalDateTime monthStart);
} 