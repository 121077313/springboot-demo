package examples;

import express.Express;

public class MyExpress extends Express {

	public static void main(String[] args) {
		MyExpress app = new MyExpress();

		app.get("/");
		app.get("", (req, res) -> {
			res.send("sss");
		});
		app.listen(8080);
	}

}
