package com.snava.cubanews;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.stream.Stream;

public class TestHttpServer {
  HttpServer server;
  Crawler crawler;
  public void start() throws IOException {
    int port = 4001; // Change this to the desired port number
    server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/", new MyHandler());
    server.setExecutor(null); // Use the default executor
    server.start();
    System.out.println("Server is listening on port " + port);
  }
  public void stop() {
    server.stop(0);
  }
  static class MyHandler implements HttpHandler {
    String currentDir = System.getProperty("user.dir");
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      String requestPath = exchange.getRequestURI().getPath();
      String filePath = currentDir + "/sites" + requestPath; // Modify this to the actual path of your HTML directory

      File file = new File(filePath);
      if (file.exists() && file.isFile()) {
        sendFileResponse(file, exchange);
      } else {
        send404Response(exchange);
      }
    }

    private void sendFileResponse(File file, HttpExchange exchange) throws IOException {
      exchange.sendResponseHeaders(200, file.length());
      exchange.getResponseHeaders().put("Content-Type", Stream.of("text/html").toList());
      OutputStream outputStream = exchange.getResponseBody();
      Files.copy(file.toPath(), outputStream);
      outputStream.close();
    }

    private void send404Response(HttpExchange exchange) throws IOException {
      String response = "File not found";
      exchange.sendResponseHeaders(404, response.length());
      OutputStream outputStream = exchange.getResponseBody();
      outputStream.write(response.getBytes());
      outputStream.close();
    }
  }

}
