package com.example.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.domain.DataSet;

@Controller
public class AjaxController {
    private static final Logger LOG = LoggerFactory.getLogger(AjaxController.class);

    // Test Case - 1
    @ResponseBody
    @GetMapping(value = "/TEST_001")
    public List<String> TEST_001(@ModelAttribute("username") String username,
                                 @RequestParam("password") String password) {
        LOG.info("username : " + username);
        LOG.info("password : " + password);
        List<String> response = new ArrayList<>();
        response.add(username);
        response.add(password);
        return response;
    }

    @ResponseBody
    @GetMapping(value = "/TEST_001_JSON", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<String> TEST_001_JSON(@ModelAttribute("username") String username,
                                      @RequestParam("password") String password) {
        LOG.info("username : " + username);
        LOG.info("password : " + password);
        List<String> response = new ArrayList<>();
        response.add(username);
        response.add(password);
        return response;
    }

    // Test Case - 2
    @ResponseBody
    @GetMapping(value = "/TEST_002")
    public List<String> TEST_002(@ModelAttribute("dataSet") DataSet dataSet) {
        List<String> response = new ArrayList<>();
        response.add(dataSet.getUsername());
        response.add(dataSet.getPassword());
        return response;
    }

    // Test Case - 3
    @GetMapping(value = "/TEST_003")
    public List<String> TEST_003(@ModelAttribute("username") String username,
                                 @RequestParam("password") String password) {
        LOG.info("username : " + username);
        LOG.info("password : " + password);
        List<String> response = new ArrayList<>();
        response.add(username);
        response.add(password);
        return response;
    }

    // Test Case - 4
    @ResponseBody
    @PutMapping(value = "/TEST_004", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> TEST_004(@RequestBody DataSet dataSet) {
        LOG.info("Request TEST_004.... - {}", dataSet);

        Map<String, Object> response = new HashMap<>();
        response.put("username", dataSet.getUsername());
        response.put("password", dataSet.getPassword());
        return response;
    }

    // Test Case - 5
    @ResponseBody
    @GetMapping(value = "/TEST_005")
    public Map<String, Object> TEST_005(@RequestBody DataSet dataSet) {
        LOG.info("Request TEST_005.... - {}", dataSet);

        Map<String, Object> response = new HashMap<>();
        response.put("username", dataSet.getUsername());
        response.put("password", dataSet.getPassword());
        return response;
    }

    // Test Case - 6
    @ResponseBody
    @PostMapping(value = "/TEST_006", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> TEST_006(@RequestBody DataSet dataSet) {
        LOG.info("Request TEST_006.... - {}", dataSet);
        Map<String, Object> body = new HashMap<>();
        body.put("username", dataSet.getUsername());
        body.put("password", dataSet.getPassword());

        //응답과 함깨 HttpStatus를 지정할 수 있습니다.
        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        return response;
    }

    // Test Case - 7
    //@ResponseBody
    @PostMapping(value = "/TEST_007", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> TEST_007(@RequestBody DataSet dataSet) {
        LOG.info("Request TEST_007.... - {}", dataSet);

        Map<String, Object> body = new HashMap<>();
        body.put("username", dataSet.getUsername());
        body.put("password", dataSet.getPassword());

        return ResponseEntity.ok(body);
    }

    // Test Case - 8
    @PostMapping(value = "/TEST_008", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> TEST_008(@RequestBody DataSet dataSet,
                                           @RequestParam String param) {
        LOG.info("Request TEST_008.... - {}", dataSet);
        LOG.info("param {}", param);

        Map<String, Object> body = new HashMap<>();
        body.put("username", dataSet.getUsername());
        body.put("password", dataSet.getPassword());

        return ResponseEntity.ok(body);
    }
}
