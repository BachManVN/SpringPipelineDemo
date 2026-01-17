package com.pipeline.demo.service;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    public String getGreeting(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Hello, Guest!";
        }
        return "Hello, " + name + "!";
    }

    public String getFormalGreeting(String name, String title) {
        if (name == null || name.trim().isEmpty()) {
            return "Hello, Guest!";
        }
        if (title == null || title.trim().isEmpty()) {
            return getGreeting(name);
        }
        return "Hello, " + title + " " + name + "!";
    }

    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 100;
    }
}
