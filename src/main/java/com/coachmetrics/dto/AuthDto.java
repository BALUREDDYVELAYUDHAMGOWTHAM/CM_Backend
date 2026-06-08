package com.coachmetrics.dto;

public class AuthDto {

    public static class LoginRequest {
        private String email;
        private String password;
        public LoginRequest() {}
        public String getEmail()    { return email; }
        public String getPassword() { return password; }
        public void setEmail(String email)       { this.email    = email; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private String role;
        private String fullName;
        private Long   userId;
        private String email;
        private String department;
        public AuthResponse() {}
        public String getToken()      { return token; }
        public String getRole()       { return role; }
        public String getFullName()   { return fullName; }
        public Long   getUserId()     { return userId; }
        public String getEmail()      { return email; }
        public String getDepartment() { return department; }
        public void setToken(String token)           { this.token      = token; }
        public void setRole(String role)             { this.role       = role; }
        public void setFullName(String fullName)     { this.fullName   = fullName; }
        public void setUserId(Long userId)           { this.userId     = userId; }
        public void setEmail(String email)           { this.email      = email; }
        public void setDepartment(String department) { this.department = department; }
    }
}
