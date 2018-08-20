package examples.app;

import javax.annotation.PostConstruct;

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
		app.listen(8080);
	}
}
