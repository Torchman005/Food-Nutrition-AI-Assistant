package com.example.foodnutritionaiassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodnutritionaiassistant.ui.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    viewModel: UserViewModel,
    onLoginSuccess: (isFirstLogin: Boolean) -> Unit,
    onSkip: () -> Unit
) {
    var isPhoneLoginMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF1F8E9), // Very Light Green
                        Color(0xFFDCEDC8)  // Light Green
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Title
        Text(
            text = "你好，\n欢迎登录食鉴",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF33691E), // Dark Green
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.weight(0.5f))
        
        if (isPhoneLoginMode) {
            PhoneLoginForm(viewModel, onLoginSuccess)
        } else {
            // Illustration Placeholder
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                 // Using an Icon as placeholder for the illustration
                 Icon(
                     imageVector = Icons.Default.Person,
                     contentDescription = "Login Illustration",
                     modifier = Modifier.size(120.dp),
                     tint = Color(0xFF558B2F)
                 )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (!isPhoneLoginMode) {
            // WeChat Login Button
            Button(
                onClick = { viewModel.loginWithWeChat(onLoginSuccess) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAED581)), // Light Green
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Email, // Placeholder for WeChat icon
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("微信登陆", fontSize = 16.sp, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Phone Login Button (Switch Mode)
            Button(
                onClick = { isPhoneLoginMode = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, Color.Black, RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("手机号登陆", fontSize = 16.sp, color = Color.Black)
            }
        } else {
             // Back to main login options
             TextButton(onClick = { isPhoneLoginMode = false }) {
                 Text("返回其他登录方式", color = Color.Gray)
             }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Skip Option
        TextButton(onClick = onSkip) {
            Text("暂不登录", color = Color.Gray, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Terms
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF7CB342),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = buildAnnotatedString {
                    append("已阅读并同意 ")
                    withStyle(style = SpanStyle(color = Color(0xFF7CB342))) {
                        append("《服务条款》")
                    }
                    append(" 与 ")
                    withStyle(style = SpanStyle(color = Color(0xFF7CB342))) {
                        append("《隐私权政策》")
                    }
                    append("。")
                },
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PhoneLoginForm(
    viewModel: UserViewModel,
    onLoginSuccess: (isFirstLogin: Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Phone Input
        OutlinedTextField(
            value = viewModel.phoneNumber,
            onValueChange = { viewModel.phoneNumber = it },
            label = { Text("手机号") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF66BB6A),
                unfocusedBorderColor = Color(0xFFA5D6A7)
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Code Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.verificationCode,
                onValueChange = { viewModel.verificationCode = it },
                label = { Text("验证码") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF66BB6A),
                    unfocusedBorderColor = Color(0xFFA5D6A7)
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = { viewModel.sendVerificationCode() },
                enabled = !viewModel.isCodeSent && viewModel.phoneNumber.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Text(
                    text = if (viewModel.isCodeSent) "${viewModel.countdown}s" else "发送验证码",
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { viewModel.loginWithPhone(onLoginSuccess) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("登录", fontSize = 18.sp, color = Color.White)
        }
    }
}
