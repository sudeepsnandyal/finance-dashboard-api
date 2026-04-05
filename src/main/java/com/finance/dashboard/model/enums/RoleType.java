package com.finance.dashboard.model.enums;

public enum RoleType {
    VIEWER("ROLE_VIEWER"),
    ANALYST("ROLE_ANALYST"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    RoleType(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
