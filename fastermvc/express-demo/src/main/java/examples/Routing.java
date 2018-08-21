package examples;

import express.Express;

public class Routing {

  public static void main(String[] args) throws Throwable {
    Express app = new Express();

    // Define router for index sites
    Express indexRouter = new Express();
    indexRouter.get("/", (req, res) -> res.send("Hello World!"));
    indexRouter.get("/index", (req, res) -> res.send("Index"));
    indexRouter.get("/about", (req, res) -> res.send("About"));

    
    
    Express index=new Express();
    
    index.get("/", (req, res) -> res.send("Hello World!"));
    
    
    
    // Define router for user pages
    Express userRouter = new Express();
    userRouter.get("/", (req, res) -> res.send("User Page"));
    userRouter.get("/login", (req, res) -> res.send("User Login"));
    userRouter.get("/register", (req, res) -> res.send("User Register"));
    userRouter.get("/:username", (req, res) -> res.send("You want to see: " + req.getParam("username")));

    // Add router and set root pathsl
    app.use("/", indexRouter);
    
    app.use("/test", index);
    app.use("/user", userRouter);

    // Start server
    app.listen(8080);
  }

}
