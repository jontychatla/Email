package com.uber.email.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  public @ResponseBody String hello(
      @RequestParam(value = "name", defaultValue = "Foo") String input) {
    return new String("Hello " + input);
  }

}
