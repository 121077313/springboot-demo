package examples.app;

import java.io.FilePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import express.Express;
import middleware.Cookie;
import middleware.CookieSession;
import middleware.FileProvider;
import middleware.FileProviderOptions;
import middleware.SessionCookie;
import middleware.file.FTPFileProvider;

@Component
public class MyRouter {

	@Autowired
	UserService userService;

	public void urls(Express app) {

		app.get("/", (req, res) -> {
			res.send("Hello World");
		})

				.get("/getuser", (req, res) -> {
					Integer id = req.intValue("id");
					Object result = userService.getUser(id);

					res.send(result); // Send: "Page: 12, from: John"
				})

				.get("/posts/:user/:type", (req, res) -> {
					String user = req.getParam("user"); // Contains 'john'
					String type = req.getParam("type"); // Contains 'all'
					res.send("User: " + user + ", type: " + type); // Send: "User: john, type: all"
				})

				.get("/setcookie", (req, res) -> {
					Cookie cookie = new Cookie("username", "john");
//					res.setCookie(cookie);
					res.send("Cookie has been set!");
				})

				.get("/showcookie", (req, res) -> {
//					Cookie cookie = req.getCookie("username");
//					String username = cookie.getValue();
//					res.send("The username is: " + username); // Prints "The username is: john"
				})

				.post("/register", (req, res) -> {
					String email = req.getFormQuery("email");
					String username = req.getFormQuery("username");
					// Process data

					// Prints "E-Mail: john@gmail.com, Username: john"
					res.send("E-Mail: " + email + ", Username: " + username);
				})

				.get("/res", (req, res) -> {
					// res.send(); // Send empty response
					// res.send("Hello World"); // Send an string
					// res.send("chart.pdf"); // Send an file
					// res.setStatus(200); // Set the response status
					// res.getStatus(); // Returns the current response status
					// res.setCookie(new Cookie(...)); // Send an cookie
					// res.isClosed(); // Check if already something has been send to the client
				})

				.get("/req/", (req, res) -> {
					// req.getURI(); // Request URI
					// req.getHost(); // Request host (mostly localhost)
					// req.getMethod(); // Request method (here GET)
					// req.getContentType(); // Request content type, is here null because it's an
					// GET request
					// req.getBody(); // Request body inputstream
					// req.getUserAgent(); // Request user-agent
					// req.getParam("parameter"); // Returns an url parameter
					// req.getQuery("queryname"); // Returns an url query by key
					// req.getFormQuery("formqueryname"); // Returns an form input value
					// req.getFormQuerys(); // Returns all form querys
					// req.getCookie("user"); // Returns an cookie by name
					// req.getCookies(); // Returns all cookies
					// req.hasAuthorization(); // Check if the request contains an authorization
					// header
					// req.getAuthorization(); // Returns the authorization header
					// req.getMiddlewareContent("name"); // Returns data from middleware
					// req.pipe(new OutputStream() {...}); // Pipe the body to an outputstream
				})

//				.use(Middleware.cookieSession("f3v4", 9000))
				.use(new CookieSession("f3v4", 9000))// 使用coki中间件

				.use("/static", new FTPFileProvider("F:\\test", new FileProviderOptions()))

				.get("/session", (req, res) -> {

					/*
					 * CookieSession named its data "SessionCookie" which is an SessionCookie so we
					 * can Cast it.
					 */
					SessionCookie sessionCookie = (SessionCookie) req.getMiddlewareContent("SessionCookie");
					int count;

					// Check if the data is null, we want to implement an simple counter
					if (sessionCookie.getData() == null) {

						// Set the default data to 1 (first request with this session cookie)
						count = (int) sessionCookie.setData(1);
					} else {

						// Now we know that the cookie has an integer as data property, increase it
						count = (int) sessionCookie.setData((int) sessionCookie.getData() + 1);
					}

					// Send an info message
					res.send("You take use of your session cookie " + count + " times.");
				});
	}

}
