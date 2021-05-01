package com.example.demo;

import com.example.demo.camelroutes.DemoRouter;
import com.example.demo.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class CamelController {

    @Autowired
    private DemoRouter demoRouter;

    @GetMapping("/camel")
   // @RequestMapping("/camel")
    //@ResponseBody
    public String welcome(Model model){
        model.addAttribute("name", "martin");

        List<Book> books = Arrays.asList(new Book("Game of Thrones", "JrrMartin"), new Book("Lord of the Rings", "Tolkin"));
        model.addAttribute("books", books);

        demoRouter.runService();

        return "camel";
    }

    public DemoRouter getDemoRouter() {
        return demoRouter;
    }

    public void setDemoRouter(DemoRouter demoRouter) {
        this.demoRouter = demoRouter;
    }
}
