package com.leo.cathay.image.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Value("${git.commit.id.abbrev}")
    private String ABBREV;

    @Value("${git.branch}")
    private String BRANCH;

    @Value("${git.commit.message.full}")
    private String MESSAGE;

    @RequestMapping("/")
    public String getCommitId() {
        return "Branch: " + BRANCH + ", Abbrev: " + ABBREV + ", MESSAGE: " + MESSAGE;
    }
}