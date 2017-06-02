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
public class Stack {
    int i;
    ArrayList<String> vars;
    ArrayList<String> input;
    ArrayList<String> production;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public ArrayList<String> getVars() {
        return vars;
    }

    public void setVars(ArrayList<String> vars) {
        this.vars = vars;
    }

    public ArrayList<String> getInput() {
        return input;
    }

    public void setInput(ArrayList<String> input) {
        this.input = input;
    }

    public ArrayList<String> getProduction() {
        return production;
    }

    public void setProduction(ArrayList<String> production) {
        this.production = production;
    }

    @Override
    public String toString() {
        return i + " " + vars + " " + input + " " + production;
    }
    
 
}
