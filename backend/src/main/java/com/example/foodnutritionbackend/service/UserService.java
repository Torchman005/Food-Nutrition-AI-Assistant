package com.example.foodnutritionbackend.service;

import com.example.foodnutritionbackend.dto.LoginRequest;
import com.example.foodnutritionbackend.model.User;
import com.example.foodnutritionbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        // Check if user exists
        if (user.getPhoneNumber() != null && userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Phone number already registered");
        }
        if (user.getWechatOpenId() != null && userRepository.findByWechatOpenId(user.getWechatOpenId()).isPresent()) {
             throw new RuntimeException("WeChat account already registered");
        }
        
        // Ensure loginType is set correctly
        if (user.getLoginTypeStr() == null) {
            if (user.getPhoneNumber() != null) user.setLoginTypeStr("PHONE");
            else if (user.getWechatOpenId() != null) user.setLoginTypeStr("WECHAT");
        }

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        if ("PHONE".equalsIgnoreCase(request.getLoginType())) {
            // Mock SMS verification: Expect password field to contain the code "123456"
            if (!"123456".equals(request.getPassword())) {
                 throw new RuntimeException("Invalid verification code");
            }
            return userRepository.findByPhoneNumber(request.getPhoneNumber())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else if ("WECHAT".equalsIgnoreCase(request.getLoginType())) {
            return userRepository.findByWechatOpenId(request.getWechatOpenId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("Invalid login type");
    }
    
    public User updateUser(String uid, User updatedUser) {
        User existingUser = userRepository.findByUserUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update fields
        if (updatedUser.getNickname() != null) existingUser.setNickname(updatedUser.getNickname());
        if (updatedUser.getGender() != null) existingUser.setGender(updatedUser.getGender());
        if (updatedUser.getBirthDate() != null) existingUser.setBirthDate(updatedUser.getBirthDate());
        if (updatedUser.getHeight() != null) existingUser.setHeight(updatedUser.getHeight());
        if (updatedUser.getWeight() != null) existingUser.setWeight(updatedUser.getWeight());
        if (updatedUser.getGroupCategory() != null) existingUser.setGroupCategory(updatedUser.getGroupCategory());
        if (updatedUser.getAvatarUrl() != null) existingUser.setAvatarUrl(updatedUser.getAvatarUrl());
        
        // Update other fields as needed
        if (updatedUser.getTargetWeight() != null) existingUser.setTargetWeight(updatedUser.getTargetWeight());
        if (updatedUser.getWaistline() != null) existingUser.setWaistline(updatedUser.getWaistline());
        
        return userRepository.save(existingUser);
    }
    
    public User getUser(String uid) {
        return userRepository.findByUserUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
