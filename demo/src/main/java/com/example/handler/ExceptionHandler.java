package com.example.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Object> globalExceptionHandler(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(getMap(ex.getMessage()));
    }

    private Map<String, String> getMap(String mesaj) {
        Map<String, String> map = new HashMap();
        map.put("error", mesaj);
        return map;
    }
}
