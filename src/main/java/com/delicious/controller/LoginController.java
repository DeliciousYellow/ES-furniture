package com.delicious.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.delicious.annotation.AddLog;
import com.delicious.pojo.Result;
import com.delicious.pojo.entity.Admin;
import com.delicious.pojo.entity.User;
import com.delicious.service.AdminService;
import com.delicious.service.LoginService;
import com.delicious.service.UserService;
import com.delicious.util.DigestUtils;
import com.delicious.util.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: ES-furniture
 * @description:
 * @author: 王炸！！
 * @create: 2023-04-10 02:41
 **/
@RestController
@Api(tags = "登录和注册相关")
@CrossOrigin
public class LoginController {

    @Resource
    private RedisTemplate<Integer,String> redisTemplate;
    @Resource
    private LoginService loginService;
    @Resource
    private UserService userService;
    @Resource
    private AdminService adminService;

    @ApiOperation("管理员登录")
    @PostMapping("/AdminLogin")
    public Result AdminLogin(@RequestBody Admin form) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getAdminCode, form.getAdminCode());
        Admin admin = adminService.getOne(wrapper);
        //根据工号和密码判断登录
        if (admin == null) {
            return Result.fail().setMessage("账号不存在");
        } else if (!form.getAdminPwd().equals(admin.getAdminPwd())) {
            //密码错误
            return Result.fail().setMessage("密码错误");
        }
        String token = JwtUtils.getToken(admin.getAdminId().toString());
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        //返回用于生成数据摘要的密钥
        String digestSecret = DigestUtils.GetDigest();
        //摘要密钥返回给前端
        map.put("digestSecret", digestSecret);
        //摘要密钥存到Redis
        redisTemplate.opsForValue().set(admin.getAdminId(),digestSecret);
        return Result.ok(map);
    }

    @ApiOperation("管理员退出登录")
    @PostMapping("/AdminLogout")
    public Result AdminLogout() {
        return Result.ok();
    }

    @ApiOperation("用户注册/登录")
    @PostMapping("/Register")
    public Result Register(@RequestParam("code") String code, @RequestParam("nickName") String nickName, @RequestParam("avatarUrl") String avatarUrl) {
        //根据code获取openid和SessionKey
        Map<String, String> map = loginService.GetOpenidAndSessionKeyByCode(code);
        //sessionKey加上前端getUserInfo获得的初始向量iv一起.解密用户手机号等关键信息
//        String sessionKey = map.get("sessionKey");
        //openid用于用户标识
        String openid = map.get("openid");
        User user = new User();
        user.setUserOpenid(openid);
        user.setNickName(nickName);
        user.setAvatarUrl(avatarUrl);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserOpenid, user.getUserOpenid());
        User one = userService.getOne(wrapper);
        if (one == null) {
            //如果数据库中没有该openid对应的数据，就注册
            userService.save(user);
            one = userService.getOne(wrapper);
        }
        //登录
        String token = JwtUtils.getToken(one.getUserId().toString());
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("userId", one.getUserId().toString());
        //返回用于生成数据摘要的密钥
        String digestSecret = DigestUtils.GetDigest();
        //摘要密钥返回给前端
        resultMap.put("digestSecret", digestSecret);
        //摘要密钥存到Redis
        redisTemplate.opsForValue().set(one.getUserId(),digestSecret);

        return Result.ok(resultMap).setMessage("登陆成功");
    }
}
