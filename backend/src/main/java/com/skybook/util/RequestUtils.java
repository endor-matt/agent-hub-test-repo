package com.skybook.util;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestUtils {

    private RequestUtils() {
    }

    public static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public static String browser(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("edg/")) {
            return "Edge";
        }
        if (ua.contains("chrome/")) {
            return "Chrome";
        }
        if (ua.contains("firefox/")) {
            return "Firefox";
        }
        if (ua.contains("safari/") && !ua.contains("chrome")) {
            return "Safari";
        }
        return "Other";
    }

    public static String operatingSystem(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) {
            return "Windows";
        }
        if (ua.contains("mac os") || ua.contains("macintosh")) {
            return "macOS";
        }
        if (ua.contains("android")) {
            return "Android";
        }
        if (ua.contains("iphone") || ua.contains("ipad")) {
            return "iOS";
        }
        if (ua.contains("linux")) {
            return "Linux";
        }
        return "Other";
    }
}
