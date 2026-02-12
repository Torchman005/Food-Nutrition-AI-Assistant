package com.example.foodnutritionbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "user_uid", nullable = false, unique = true, length = 64)
    @JsonProperty("id") // Map this to "id" in JSON for frontend compatibility
    private String userUid;

    @Column(length = 64)
    private String nickname;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private Integer gender; // 0: Unknown, 1: Male, 2: Female

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private Double height;
    private Double weight;
    
    @Column(name = "target_weight")
    private Double targetWeight;
    
    private Double waistline;
    private Double bmi;
    private Integer bmr;

    @Column(name = "login_type")
    @JsonIgnore
    private Integer loginTypeDb; // 1: WECHAT, 2: PHONE

    @Column(name = "phone_number", length = 20, unique = true)
    private String phoneNumber;

    @Transient
    private String password; // Used for passing verification code in request

    @Column(name = "wechat_open_id", length = 64, unique = true)
    private String wechatOpenId;

    @Column(name = "group_category", length = 20)
    private String groupCategory;

    private Integer status = 1;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (userUid == null) userUid = UUID.randomUUID().toString();
        if (nickname == null) nickname = "新用户";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods for JSON <-> DB mapping for loginType
    @Transient
    @JsonProperty("loginType")
    public String getLoginTypeStr() {
        if (loginTypeDb == null) return null;
        return loginTypeDb == 2 ? "PHONE" : (loginTypeDb == 1 ? "WECHAT" : "UNKNOWN");
    }

    @JsonProperty("loginType")
    public void setLoginTypeStr(String type) {
        if ("PHONE".equalsIgnoreCase(type)) this.loginTypeDb = 2;
        else if ("WECHAT".equalsIgnoreCase(type)) this.loginTypeDb = 1;
        else this.loginTypeDb = 0;
    }
}
