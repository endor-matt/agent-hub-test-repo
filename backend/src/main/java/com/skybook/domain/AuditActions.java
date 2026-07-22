package com.skybook.domain;

public final class AuditActions {
    public static final String USER_LOGIN = "USER_LOGIN";
    public static final String USER_LOGOUT = "USER_LOGOUT";
    public static final String BOOKING_CREATED = "BOOKING_CREATED";
    public static final String BOOKING_CANCELLED = "BOOKING_CANCELLED";
    public static final String AI_QUERY = "AI_QUERY";
    public static final String PROFILE_UPDATE = "PROFILE_UPDATE";
    public static final String PASSWORD_CHANGE = "PASSWORD_CHANGE";
    public static final String EXPORT_REQUEST = "EXPORT_REQUEST";
    public static final String USER_REGISTER = "USER_REGISTER";

    private AuditActions() {
    }
}
