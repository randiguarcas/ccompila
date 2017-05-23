/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.ArrayList;

/**alpabethPrefix
 *
 * @author Randi Guarcas
 */
public class Robot {
    public String alpabethPrefix; //Prefijo de alfabeto
    public String prefixKeySplit; //prefijo para split de clave:valor
    private ArrayList<String> originalContent; //contenido original del archivo
    private ArrayList<Content> cleanContent = new ArrayList<Content>(); //array list de content
    private String[] alphabet; //array de alfabeto
    
    public void load(ArrayList<String> content){
        this.originalContent = (ArrayList<String>) content.clone();
        //cargamos el pojo de notation
        this.loadPojoContent();
    }
     
    /**
     * getOriginalContent original content from file
     * @return ArrayList<String>
     */
    public ArrayList<String> getOriginalContent(){
        return this.originalContent;
    }
    
    /**
     * getAlphabet from original content
     * @return String[] 
     */
    public String[] getAlphabet(){
        int index = 0;
        //recorremos el contenido original
        for (String content : originalContent) {
            //cada elemento(linea) del contenido original la volvemos un array de char
            char[] charContent = content.toCharArray();
            //buscamos en la primera posición si es el prefijo del alfabeto
            if(charContent[index] == this.alpabethPrefix.charAt(index)){
                //al encontrar la linea del alfabeto, a la linea le hacemos un split para su clave:suvalor
                String[] spContent = content.split(this.prefixKeySplit);
                //en la segunda posición se encuentra su valor
                String valueToken = spContent[(index+1)];
                //buscamos '}' y '}' para reemplazarlos por vacios
                if(valueToken.contains("{"))
                    valueToken = valueToken.replace("{","");
                if(valueToken.contains("}"))
                    valueToken = valueToken.replace("}","");
                
                //llenamos el array con cada uno de los elementos del alfabeto
                this.alphabet = valueToken.split(",");
                break;
            }
        }
        return this.alphabet;
    }
    
    private void loadPojoContent(){
        int index = 0;
        ArrayList<Content> tempContent = new ArrayList<>();
        //recorremos el arraylist original del contenido
        for (String oContent : originalContent) {
            String[] sideContent = oContent.split(this.prefixKeySplit);
            //excluimos el alfabeto
            if(!sideContent[index].equals(this.alpabethPrefix)){
                //craemos un nuevo pojo temporal
                Content temp = new Content();
                temp.setKey(sideContent[index]);
                temp.setValue(sideContent[(index+1)]);
                //agregamos el pojo a un array list de pojos content
                tempContent.add(temp);
            }   
        }
        ArrayList<Content> notationWithProduction = this.notationWithProductions(tempContent);
        this.cleanContent = (ArrayList<Content>) notationWithProduction.clone();
    }
    
    
    private ArrayList<Content> notationWithProductions(ArrayList<Content> contentNotation){
        ArrayList<Content> tempNotation =  (ArrayList<Content>) contentNotation.clone();
        for (int i = 0; i < contentNotation.size(); i++) {
            Content notation = contentNotation.get(i);
            int found = 0;
            int prefix = 0;
            //Recorrido de array de objetos de notacion temporal
            for (int j = 0; j < tempNotation.size(); j++) {
                Content temp = tempNotation.get(j);
                //Si se encuentra la variable repetida
                if(notation.getKey().equals(temp.getKey())){
                    if(found == 0){
                        prefix = j;
                    }else{
                        int prx = j;
                        //agregamos a la posicion de la primera variable encontrada
                        //lo que tiene en el valor esa variable y concatena el valor del temporal
                        contentNotation.get(prefix).setValue(contentNotation.get(prefix).getValue().concat("|"+temp.getValue()));
                        tempNotation.remove(prx);
                     }
                    found++;
                }
                else{
                    //System.out.println(notation.getKey() + " <> " + temp.getKey());
                }
            }
        }
        
        return tempNotation;
    }

    public ArrayList<Content> getCleanContent() {
        return this.cleanContent;
    }
    
}
