package ru.rustam.amzscout.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rustam.amzscout.util.WebUtils;

@RestController
@RequestMapping(value = AppController.REST_URL)
public class AppController {
    static final String REST_URL = "/app";
    @Autowired
    private WebUtils webUtils;

    @GetMapping
    public ResponseEntity<?> get() {
        if (webUtils.checkRequestsNumberFromOneIp()) {
            return ResponseEntity.ok().body(HttpStatus.OK);
        }
        return ResponseEntity.internalServerError().body(HttpStatus.BAD_GATEWAY);
    }
}