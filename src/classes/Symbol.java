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
public class Symbol {
    public String key;
    public ArrayList< SubSymbol > value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<SubSymbol> getValue() {
        return value;
    }

    public void setValue(ArrayList<SubSymbol> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.key + ":" + this.value;
    }
    
    
}

class SubSymbol {
    public String key;
    public ArrayList< ArrayList<String>  > value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<ArrayList<String>> getValue() {
        return value;
    }

    public void setValue(ArrayList<ArrayList<String>> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.key + ":" + this.value; //To change body of generated methods, choose Tools | Templates.
    }
    
}
