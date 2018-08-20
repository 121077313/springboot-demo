package app;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;

import app.controller.UserController;

public class Router {
	static Map<String, Method> urls = new HashMap<String, Method>();
	@Autowired
	UserController userController;

	public void init() {
		try {
//			url("/get", userController.get(1));
			url("/get/?", UserController.class, "get");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Method comple(String url) {

		Method m = urls.get(url);
		if (m != null) {
			return m;
		}
		for (Entry<String, Method> entry : urls.entrySet()) {
			String str = entry.getKey();
			if (url.matches(str)) {
				return entry.getValue();
			}
		}
		return null;

	}

	public static void url(String url, Class clazz, String methodName) {
		try {
			for (Method m : clazz.getMethods()) {
				if (m.getName().equals(methodName)) {
					urls.put(url, m);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		init();
		Method m = comple("/get/1");

		System.out.println();
	}
}
