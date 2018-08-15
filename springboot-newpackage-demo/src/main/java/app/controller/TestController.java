package app.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

	@RequestMapping("/")
	@ResponseBody
	public String greeting() {
		return "Hello World!";
	}

	
	@RequestMapping("/test")
	public String test() {
		return "test";
	}
	
	@RequestMapping("/now")
	@ResponseBody
	String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmssSSS");
		return sdf.format(new Date());
	}

}
