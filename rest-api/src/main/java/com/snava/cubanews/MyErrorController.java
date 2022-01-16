package com.snava.cubanews;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class MyErrorController implements ErrorController {

  @RequestMapping("/error")
  public String handleError() {
    //do something like logging
    return "forward:/";
  }
}