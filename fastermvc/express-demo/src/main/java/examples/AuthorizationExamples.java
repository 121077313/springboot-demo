package examples;

import express.Express;
import express.utils.Status;
import middleware.Authorization;

public class AuthorizationExamples {
	public static void main(String[] args) {
		Express app = new Express();

		app.get("/", (req, res) -> {
			if (Authorization.validate(req, Authorization.validator("Basic", "123456789"))) {
				res.send("You are authorized!");
			} else {
				res.setStatus(Status._401);
				res.send();
			}
		});

		app.listen(8080, () -> System.out.println("Listening!"));
	}
}
