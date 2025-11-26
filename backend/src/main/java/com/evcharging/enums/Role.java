package com.evcharging.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum Role {
    SUPER_ADMIN,
    ADMIN,
    CS_STAFF,
    EV_DRIVER;

    public Collection<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        switch (this) {
            case SUPER_ADMIN -> {
                // SUPER_ADMIN has ALL permissions - complete system control

                // STATION & INFRASTRUCTURE
                auth.add(new SimpleGrantedAuthority("PERM_STATION_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_STATION_CONTROL"));
                auth.add(new SimpleGrantedAuthority("PERM_CONNECTOR_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_PRICING_MANAGE"));

                // USER MANAGEMENT (including other admins)
                auth.add(new SimpleGrantedAuthority("PERM_USER_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_STAFF_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_DRIVER_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_ADMIN_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_SUPER_ADMIN_MANAGE"));

                // REPORTS & ANALYTICS
                auth.add(new SimpleGrantedAuthority("PERM_REPORT_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_REPORT_REVENUE"));
                auth.add(new SimpleGrantedAuthority("PERM_REPORT_USAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_DASHBOARD_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_AI_FORECAST"));

                // BILLING & PAYMENT
                auth.add(new SimpleGrantedAuthority("PERM_BILLING_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_TRANSACTION_MANAGE"));

                // SUBSCRIPTION
                auth.add(new SimpleGrantedAuthority("PERM_SUBSCRIPTION_MANAGE"));

                // SYSTEM (SUPER_ADMIN exclusive)
                auth.add(new SimpleGrantedAuthority("PERM_SYSTEM_CONFIG"));
                auth.add(new SimpleGrantedAuthority("PERM_TICKET_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_AUDIT_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_SECURITY_MANAGE"));

                // CHARGING POINT MANAGEMENT
                auth.add(new SimpleGrantedAuthority("PERM_CHARGER_STATUS_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_CHARGER_MAINTENANCE"));
                auth.add(new SimpleGrantedAuthority("PERM_CHARGER_VIEW"));

                // CHARGING SESSION MANAGEMENT
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_START"));
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_STOP"));
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_MANAGE"));

                // RESERVATION MANAGEMENT
                auth.add(new SimpleGrantedAuthority("PERM_RESERVATION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_RESERVATION_CHECKIN"));
                auth.add(new SimpleGrantedAuthority("PERM_RESERVATION_NOSHOW"));
                auth.add(new SimpleGrantedAuthority("PERM_RESERVATION_CREATE"));
                auth.add(new SimpleGrantedAuthority("PERM_RESERVATION_CANCEL"));

                // PAYMENT & TRANSACTION
                auth.add(new SimpleGrantedAuthority("PERM_PAYMENT_RECORD"));
                auth.add(new SimpleGrantedAuthority("PERM_TRANSACTION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_REFUND_CREATE"));
                auth.add(new SimpleGrantedAuthority("PERM_BILLING_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_WALLET_PAY"));
                auth.add(new SimpleGrantedAuthority("PERM_PAYMENT_EWALLET"));

                // STATION & BROWSE
                auth.add(new SimpleGrantedAuthority("PERM_STATION_BROWSE"));
                auth.add(new SimpleGrantedAuthority("PERM_STATION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_STATION_ACCESS"));

                // PROFILE & HISTORY
                auth.add(new SimpleGrantedAuthority("PERM_PROFILE_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_PROFILE_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_HISTORY_VIEW"));

                // USER VERIFICATION
                auth.add(new SimpleGrantedAuthority("PERM_USER_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_DRIVER_VERIFY"));

                // SUPPORT
                auth.add(new SimpleGrantedAuthority("PERM_REPORT_VIEW_BASIC"));
            }
            case ADMIN -> {
                // STATION & INFRASTRUCTURE
                auth.add(new SimpleGrantedAuthority("PERM_STATION_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_STATION_CONTROL"));
                auth.add(new SimpleGrantedAuthority("PERM_CONNECTOR_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_PRICING_MANAGE"));

                // USER MANAGEMENT
                auth.add(new SimpleGrantedAuthority("PERM_USER_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_STAFF_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_DRIVER_MANAGE"));

                // REPORTS & ANALYTICS
                auth.add(new SimpleGrantedAuthority("PERM_REPORT_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_REPORT_REVENUE"));
                auth.add(new SimpleGrantedAuthority("PERM_REPORT_USAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_DASHBOARD_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_AI_FORECAST"));

                // BILLING & PAYMENT
                auth.add(new SimpleGrantedAuthority("PERM_BILLING_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_TRANSACTION_MANAGE"));

                // SUBSCRIPTION
                auth.add(new SimpleGrantedAuthority("PERM_SUBSCRIPTION_MANAGE"));

                // SYSTEM
                auth.add(new SimpleGrantedAuthority("PERM_SYSTEM_CONFIG"));
                auth.add(new SimpleGrantedAuthority("PERM_TICKET_MANAGE"));
            }
            case CS_STAFF -> {
                // STATION MANAGEMENT
                auth.add(new SimpleGrantedAuthority("PERM_STATION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_STATION_ACCESS"));

                // CHARGING POINT MANAGEMENT
                auth.add(new SimpleGrantedAuthority("PERM_CHARGER_STATUS_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_CHARGER_MAINTENANCE"));
                auth.add(new SimpleGrantedAuthority("PERM_CHARGER_VIEW"));

                // CHARGING SESSION MANAGEMENT
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_START"));
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_STOP"));
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_MANAGE"));

                // RESERVATION MANAGEMENT
                auth.add(new SimpleGrantedAuthority("PERM_RESERVATION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_RESERVATION_CHECKIN"));
                auth.add(new SimpleGrantedAuthority("PERM_RESERVATION_NOSHOW"));

                // PAYMENT & TRANSACTION
                auth.add(new SimpleGrantedAuthority("PERM_PAYMENT_RECORD"));
                auth.add(new SimpleGrantedAuthority("PERM_TRANSACTION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_REFUND_CREATE"));
                auth.add(new SimpleGrantedAuthority("PERM_BILLING_VIEW"));

                // USER MANAGEMENT
                auth.add(new SimpleGrantedAuthority("PERM_USER_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_DRIVER_VERIFY"));

                // SUPPORT & TICKET
                auth.add(new SimpleGrantedAuthority("PERM_TICKET_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_REPORT_VIEW_BASIC"));
            }
            case EV_DRIVER -> {
                // STATION & BROWSE
                auth.add(new SimpleGrantedAuthority("PERM_STATION_BROWSE"));
                auth.add(new SimpleGrantedAuthority("PERM_CHARGER_VIEW"));

                // RESERVATION
                auth.add(new SimpleGrantedAuthority("PERM_RESERVATION_CREATE"));
                auth.add(new SimpleGrantedAuthority("PERM_RESERVATION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_RESERVATION_CANCEL"));

                // CHARGING SESSION
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_START"));
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_STOP"));
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_VIEW"));

                // PAYMENT & TRANSACTION
                auth.add(new SimpleGrantedAuthority("PERM_WALLET_PAY"));
                auth.add(new SimpleGrantedAuthority("PERM_PAYMENT_EWALLET"));
                auth.add(new SimpleGrantedAuthority("PERM_TRANSACTION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_HISTORY_VIEW"));

                // SUBSCRIPTION
                auth.add(new SimpleGrantedAuthority("PERM_SUBSCRIPTION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_SUBSCRIPTION_REGISTER"));

                // PROFILE
                auth.add(new SimpleGrantedAuthority("PERM_PROFILE_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_PROFILE_VIEW"));
            }
        }
        return Collections.unmodifiableList(auth);
    }
}
