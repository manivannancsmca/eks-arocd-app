package com.eks_arocd_app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello, Mr. Manivannan K. Your application is running in AWS EKS Server with Argocd";
    }
}
