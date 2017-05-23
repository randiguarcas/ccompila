package classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Randi Guarcas
 */
public class ReaderFile {
   public ArrayList<String> content = new ArrayList<>();
    
    public void open(File file){
        BufferedReader br = null;
	FileReader fr = null;
        String fileLine;
        
        if(file!=null){
            try {
		fr = new FileReader(file);
		br = new BufferedReader(fr);

		br = new BufferedReader(new FileReader(file));
                Integer c = 0;
                
		while ((fileLine = br.readLine()) != null) {
                    //agrega cada una de las lineas del archivo a un arraylist
                    content.add(fileLine);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
}
