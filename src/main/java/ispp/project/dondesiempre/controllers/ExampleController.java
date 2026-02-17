package ispp.project.dondesiempre.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ExampleController {

    @GetMapping("/health")
    public String getHealth() {
        return "Server is up!";
    }

}
