package com.mycompany;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


class HelloWorld {
	String message;
	String code;
	
	public HelloWorld(String code, String message) {
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
}
@RestController
public class MapController {
	@RequestMapping("/test")
	@ResponseBody
	public String test() {
		return "Hello world";
	}
	
	@RequestMapping("/return")
	@ResponseBody
	public HelloWorld hello(){
		return new HelloWorld("1", "Hello World");
	}
}
