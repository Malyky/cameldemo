package com.example.demo;

import com.example.demo.camelroutes.DemoRouter;
import com.example.demo.entity.Book;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;

@Controller
public class RestDemoController {

    @Autowired
    private DemoRouter demoRouter;


    @Autowired
    ProducerTemplate producerTemplate;


    @GetMapping("/camel")
    // @RequestMapping("/camel")
    //@ResponseBody
    public String welcome(Model model){
        model.addAttribute("name", "martin");

        List<Book> books = Arrays.asList(new Book("Game of Thrones", "JrrMartin"), new Book("Lord of the Rings", "Tolkin"));
        model.addAttribute("books", books);

        return "camel";
    }

    @GetMapping("/produce/{name}")
    // @RequestMapping("/camel")
    //@ResponseBody
    public String produce(@PathVariable String name, Model model){

        producerTemplate.sendBody("direct:usehttp", "");
        //producerTemplate.sendBody("direct:in", "Hello from Camel " + name);

        model.addAttribute("name", name);
        return "empty";
    }

    @GetMapping("/callHttp/{number}")
    public String callHttp(@PathVariable int number, Model model){

        producerTemplate.sendBodyAndHeader("direct:httpWithHeader", "","postId", number);

        return "empty";
    }

    public DemoRouter getDemoRouter() {
        return demoRouter;
    }

    public void setDemoRouter(DemoRouter demoRouter) {
        this.demoRouter = demoRouter;
    }
}
