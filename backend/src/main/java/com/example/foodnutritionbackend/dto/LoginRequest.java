package com.example.foodnutritionbackend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phoneNumber;
    private String password; // Or verifyCode
    private String loginType; // "PHONE" or "WECHAT"
    private String wechatOpenId;
}
