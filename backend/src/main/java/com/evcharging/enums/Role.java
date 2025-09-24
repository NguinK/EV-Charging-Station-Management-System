package com.evcharging.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum Role {
    ADMIN,
    CS_STAFF,
    EV_DRIVER;

    public Collection<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        switch (this) {
            case ADMIN -> {
                // Quyền quản trị toàn cục
                auth.add(new SimpleGrantedAuthority("PERM_STATION_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_CONNECTOR_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_PRICING_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_USER_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_REPORT_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_BILLING_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_TICKET_MANAGE"));
            }
            case CS_STAFF -> {
                // Hỗ trợ khách: xem & xử lý ticket, xem phiên sạc, tra cứu thanh toán
                auth.add(new SimpleGrantedAuthority("PERM_TICKET_MANAGE"));
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_USER_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_REFUND_CREATE")); // nếu có quy trình refund
                auth.add(new SimpleGrantedAuthority("PERM_REPORT_VIEW_BASIC"));
            }
            case EV_DRIVER -> {
                // Khách hàng: sử dụng trạm, xem lịch sử, quản lý hồ sơ
                auth.add(new SimpleGrantedAuthority("PERM_STATION_BROWSE"));
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_START"));
                auth.add(new SimpleGrantedAuthority("PERM_SESSION_STOP"));
                auth.add(new SimpleGrantedAuthority("PERM_WALLET_PAY"));
                auth.add(new SimpleGrantedAuthority("PERM_HISTORY_VIEW"));
                auth.add(new SimpleGrantedAuthority("PERM_PROFILE_MANAGE"));
            }
        }
        return Collections.unmodifiableList(auth);
    }
}
