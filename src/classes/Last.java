/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.ArrayList;

/**
 *
 * @author Randi Guarcas
 */
public class Last {
    public String key;
    public ArrayList<String> value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<String> getValue() {
        return value;
    }

    public void setValue(ArrayList<String> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.key + " : " + this.value.toString().replace("[", "{ ").replace("]", " }");
    }
    
    
}
