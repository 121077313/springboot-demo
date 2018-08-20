package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@ServletComponentScan
//@EnableAspectJAutoProxy(proxyTargetClass = true)
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
