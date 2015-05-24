package nl.edegier;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.mongo.MongoServiceVerticle;
import io.vertx.ext.sockjs.BridgeOptions;
import io.vertx.ext.sockjs.SockJSServer;
import io.vertx.ext.sockjs.SockJSServerOptions;
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
	matcher.matchMethod(HttpMethod.POST, "/api/application", this::saveApplication);
	matcher.matchMethod(HttpMethod.GET, "/api/application", request -> proxy.find("application", new JsonObject(), result -> request.response().end(new JsonArray(result.result()).toString())));
	HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(PORT)).requestHandler(req -> matcher.accept(req));
	SockJSServer.sockJSServer(vertx, server).bridge(new SockJSServerOptions().setPrefix("/eventbus"),
		new BridgeOptions().addInboundPermitted(new JsonObject()).addOutboundPermitted(new JsonObject()));
	server.listen();
	
    }
    
    private void saveApplication(HttpServerRequest request){
	request.bodyHandler(buffer -> {
	    String application = buffer.getString(0, buffer.length());
	    proxy.save("application", new JsonObject(application), result -> System.out.println(result.result()));
	    vertx.eventBus().publish("application.channel", new JsonObject(application));
	});
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
