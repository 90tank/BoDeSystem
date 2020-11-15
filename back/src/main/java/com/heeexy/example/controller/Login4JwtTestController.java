package com.heeexy.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.heeexy.example.config.jwt.JwtUtil;
import com.heeexy.example.service.LoginService;
import com.heeexy.example.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: hxy
 * @description: 登录相关Controller
 * @date: 2017/10/24 10:33
 */
@Slf4j
@RestController
public class Login4JwtTestController {

	@Autowired
	private LoginService loginService;




	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody JSONObject requestJson) {
		String username = requestJson.getString("username");
		String password = requestJson.getString("password");
		log.info("username:{},password:{}",username,password);
		Map<String, String> map = new HashMap<>();

		JSONObject user = loginService.getUser(username, password);

		if (user==null) {
			map.put("msg", "用户名密码错误");
			return ResponseEntity.ok(map);
		}
		log.info(user.toString());

		JwtUtil jwtUtil = new JwtUtil();
		Map<String, Object> chaim = new HashMap<>();
		chaim.put("username", username);
		String jwtToken = jwtUtil.encode(username, 5 * 60 * 1000, chaim);
		map.put("msg", "登录成功");
		map.put("token", jwtToken);
		return ResponseEntity.ok(map);
	}


	@RequestMapping("/testdemo")
	public ResponseEntity<String> testdemo() {
		return ResponseEntity.ok("我爱蛋炒饭");
	}

	/**
	 * 查询当前登录用户的信息
	 */
	@PostMapping("/getInfo")
	public JSONObject getInfo() {
		return loginService.getInfo(); // 包含了用户权限list的查询
	}

	/**
	 * 登出
	 */
	@PostMapping("/logout")
	public JSONObject logout() {
		return loginService.logout();
	}
}
