package app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import app.po.User;
import app.service.UserService;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@RequestMapping("/get")
	@ResponseBody
	public String get(int id) {
		User user = userService.getUser(id);
		if (user == null) {
			return id + " user 不存在";
		}
		return JSONObject.toJSONString(user);
	}

	@RequestMapping("/save")
	@ResponseBody
	public String save(String name, int gold) {

		User user = userService.saveUser(name, gold);

		return JSONObject.toJSONString(user);
	}

	@RequestMapping("/add")
	@ResponseBody
	String add(int id, int gold) {
		User user = userService.getUser(id);
		if (user == null) {
			return id + " user 不存在";
		}

		userService.addGold(gold, user);

		return JSONObject.toJSONString(user);
	}

}
