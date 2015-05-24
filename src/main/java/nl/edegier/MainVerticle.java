package nl.edegier;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.MongoServiceVerticle;
import io.vertx.ext.sockjs.impl.RouteMatcher;

public class MainVerticle extends AbstractVerticle {

    private static final int PORT = 8080;
    private static final String PATH = "app";
    private static final String welcomePage = "index.html";
    private static final String MONGO_ADDRESS = "vertx.mongo";
    
    MongoService proxy;

    @Override
    public void start() throws Exception {
	proxy = setUpMongo();
	RouteMatcher matcher = getRouteMatcher();
	matcher.matchMethod(HttpMethod.GET, "/api/hello-world", request -> request.response().end("{\"content\" : \"Hello world!\" }"));
	vertx.createHttpServer(new HttpServerOptions().setPort(PORT)).requestHandler(req -> matcher.accept(req)).listen();
    }

    private MongoService setUpMongo() {
	DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("address", MONGO_ADDRESS));
	vertx.deployVerticle(new MongoServiceVerticle(), options, res -> System.out.println(res.result()));
	return MongoService.createEventBusProxy(vertx, MONGO_ADDRESS);
    }

    private RouteMatcher getRouteMatcher() {
	RouteMatcher matcher = RouteMatcher.routeMatcher().matchMethod(HttpMethod.GET, "/", req -> req.response().sendFile(PATH + "/" + welcomePage));
	matcher.matchMethod(HttpMethod.GET, "^\\/" + PATH + "\\/.*", req -> req.response().sendFile(req.path().substring(1)));
	return matcher;
    }
}
