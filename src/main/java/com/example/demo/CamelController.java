package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CamelController {

    @GetMapping("/camel")
   // @RequestMapping("/camel")
    //@ResponseBody
    public String welcome(Model model){
        model.addAttribute("name", "martin");

        return "camel";
    }
}
