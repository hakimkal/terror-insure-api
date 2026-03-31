package com.terron.models.user;

public enum UserRole {
    COMPANY_OWNER, NTDA, DSS, ADMIN, INSURANCE_USER;

    @Override
    public String toString() {
        return name(); // Returns the enum name as a string
    }
}