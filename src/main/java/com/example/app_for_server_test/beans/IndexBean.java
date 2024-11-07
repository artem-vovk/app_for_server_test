package com.example.app_for_server_test.beans;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import jakarta.faces.view.ViewScoped;

@Component
@ViewScoped
public class IndexBean implements Serializable {

    private static final long serialVersionUID = -8828328610020691873L;
    
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}