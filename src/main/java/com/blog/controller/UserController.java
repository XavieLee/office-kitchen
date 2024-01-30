package com.blog.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.Result;
import com.blog.entity.User;
import com.blog.service.EmailService;
import com.blog.service.UserService;
import com.blog.utils.CodeUtils;
import com.blog.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if (RegexUtils.checkEmail(phone)) {
            //随机生成一个验证码
            String code = CodeUtils.dynamicCode();
            log.info(code);
            //这里的phone其实就是邮箱，code是我们生成的验证码
            emailService.sendHtmlMail(phone, "您本次的验证码是: " + code,
                    "尊敬的用户,您好:<br/>"
                            + "<br/>本次请求的邮件验证码为: " + code + " ,本验证码 5 分钟内效，请及时输入。（请勿泄露此验证码）<br/>"
                            + "<br/>如非本人操作，请忽略该邮件。<br/>(这是一封系统自动发送的邮件，请不要直接回复)"
            );
            //验证码存session，方便后面拿出来比对
            session.setAttribute(phone, code);
            return Result.success("验证码发送成功");
        }
        return Result.error("邮箱不合法, 请检查后重试");
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //把刚刚存进去的验证码拿出来
        Object sessionCode = session.getAttribute(phone);
        if (sessionCode != null) {
            //看看接收到用户输入的验证码是否和session中的验证码相同
            if (code != null && code.equals(sessionCode.toString())) {
                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(User::getPhone, phone);
                User user = userService.getOne(queryWrapper);
                if (user == null) {
                    user = new User();
                    user.setPhone(phone);
                    userService.save(user);
                }
                session.setAttribute("user", user.getId());
                return Result.success(user);
            }
        }
        return Result.error("登录失败");
    }

    @PostMapping("/loginout")
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return Result.success("退出成功");
    }
}
