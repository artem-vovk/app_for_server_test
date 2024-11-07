package com.example.app_for_server_test.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.faces.view.ViewScoped;

@Component
@ViewScoped
public class ItemListBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3334389585051078274L;
	
	
	private List<String> items;

    public ItemListBean() {
        items = new ArrayList<>();
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");
    }

    public List<String> getItems() {
        return items;
    }
}
