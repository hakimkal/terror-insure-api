package com.terron.models.user;

public enum UserRole {
    COMPANY_OWNER, NTDC, DSS, ADMIN, INSURANCE_USER, INTERPOL, NSA;

    @Override
    public String toString() {
        return name(); // Returns the enum name as a string
    }
}
