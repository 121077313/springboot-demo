package examples.app;

import javax.annotation.PostConstruct;

import middleware.CookieSession;
import middleware.FileProviderOptions;
import middleware.file.FTPFileProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import express.Express;

@SpringBootApplication
public class MyApp {

	public static void main(String[] args) {
		SpringApplication.run(MyApp.class, args);

	}

	@Autowired
	MyRouter r;

	@PostConstruct
	public void start() {

		Express app = new Express();
		r.urls(app);
		
		app.use(new CookieSession("f3v4", 9000))// 使用coki中间件
		.use("/static", new FTPFileProvider("F:\\test", new FileProviderOptions()));

		
		
		app.listen(8080, () -> System.out.println("Express is listening!"));
	}
}
