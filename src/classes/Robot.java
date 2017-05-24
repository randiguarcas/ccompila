/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private ArrayList<Content> globalNotationWP = new ArrayList<Content>();
    public ArrayList<String> terms = new ArrayList<>();
    
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
    
    /**
     * Retorna un array limpio de contenido
     * @return ArrayList
     */
    public ArrayList<Content> getCleanContent() {
        return this.cleanContent;
    }
    
    public ArrayList<Struct> getBigStruct(){
        ArrayList<Struct> bigStruct = new ArrayList<>();
        //Extraemos el array de objetos de notacion con las producciones en una sola variable
        ArrayList<Content> notationWithProduction = this.notationWithProductions(this.cleanContent);
        //creamos un global de array de objetos
        globalNotationWP = (ArrayList<Content>) notationWithProduction.clone();
        //recorre cada uno de los objetos del array de notacion con las producciones en una sola variable
        int find = 0;
        int lastScene = 0;
        //por cada produccion 
        for (Content content : notationWithProduction) {
            Struct struct = new Struct();
            struct.setKey(content.getKey());
            //extraemos las producciones
            String[] tempProduction = content.getValue().split("\\|");
            //retorna un array de producciones si producciones repetidas
            String[] productions = this.cleanProduction(tempProduction);
            //temporal
            ArrayList< ArrayList<String> > tmp = new ArrayList<>();
            for (int j = 0; j < productions.length; j++) {
                String production = productions[j];
                ArrayList<String> elements = this.ProductionWithEnviroment(production);
                tmp.add(elements);
            }
            //agrega el valor al objeto struct
            struct.setValue(tmp);
            bigStruct.add(struct);
        }
        
        //for (Struct struct : bigStruct) {
         //System.out.println(struct.getKey() + " => " + struct.getValue().toString());   
        //}
        
        return bigStruct;
    }
    
    //Retorna un array de producciones sin repetidos
    private String[] cleanProduction(String[] productions){
        String[] temp = productions;
        return temp;
    }
    
    private String reverseEnviromentToTerm(String value){
        //Crea una lista de caracteres vacia
        List<Character> chars = new ArrayList<Character>();
        //Crea cada caracter de la cadena a la lista de caracteres
        for (char c : value.toCharArray()) {
            chars.add(c);
        }
        //invierte el array de caracteres
        Collections.reverse(chars);
        //Crea una nueva cadena en base al array inverso de caracteres
        String valueFromReverse = "";
        for (int i = 0; i < chars.size(); i++) {
            valueFromReverse += chars.get(i).toString();
        }
        //Retorna la cadena ordenada
        return valueFromReverse;
    }
    
    //Retorna un array con terminales y variables
    private ArrayList<String> ProductionWithEnviroment(String production){
        ArrayList<String> tempVars = new ArrayList<>();
        //System.out.println("analizar; "+production);
        //convierte cada produccion en un arrar de caracteres
        char[] items = production.toCharArray();
        //variables de uso
        String memory="";
        int found = 0;
        int strike = 0;
        String P = "";
        //por cada caracter analizar si es terminal o variable
        for (int i = items.length - 1; i >= 0; i--) {
            char item = items[i];
            //Si el item empieza con ' probablemente sea una terminal
            if(item=='\''){
                //si encuentra el ' de inicio
                if(found == 1){
                    //Obtiene la terminal
                    String real = this.reverseEnviromentToTerm(memory);
                    tempVars.add(real);
                    //reinicio de variables
                    memory="";
                    found=0;
                    strike=0;    
                }
                //sigue el contador buscando el  ' de cierre
                found++;
            }else{
                //Si encontró el ' de inicio 
                if(found==1){
                    //concatena todo lo que tenga hasta que encuentre el de cierre y reinicie variables
                    strike++;
                    memory += String.valueOf(item);
               }else{
                    
                    P += String.valueOf(item);
                    // Recorremos el array global de variables:producciones
                    for (int j = 0; j < globalNotationWP.size(); j++) {
                        Content global = globalNotationWP.get(j);
                        // Verificamos si el elemento que viene es una variable
                        String real = this.reverseEnviromentToTerm(P);
                        if(global.getKey().equals(real)){
                            //Agregamos la variable al array de elementos
                            tempVars.add(real);
                            P = "";
                            break;
                        }
                        else{
                            
                        }
                    }
                }
            }
            
        }
       
        //reverse de array que contiene elementos
        Collections.reverse(tempVars);
        return tempVars;
    }
    
    //metodo que devuelve las variables
    public ArrayList<String> getEnviroment() {
        ArrayList<String> temp = new ArrayList<>();
        for (Struct struct : this.getBigStruct()) {
            temp.add(struct.getKey());
        }
        return temp;
    }
    
    //funcion que retorna un arraylist con las producciones
    public ArrayList<String> getTerms(){
        ArrayList<String> temp = new ArrayList<>();
        //cleanContent arrayrrayList con producciones en una sola variable
        for (Content content : this.cleanContent) {
            String[] stackProd = content.getValue().split("\\|");
            for (String string : stackProd) {
                temp.add(string);
            }
        }
        
        for (String string : temp) {
            this.termStack(string);
        }
        //System.out.println(temp);
        return this.terms;
    }
    
    
    public void termStack(String cadena){
       String element = cadena.replace(" ","");
       char[] stack = element.toCharArray();
       String memory="";
       int found = 0;
       int strike = 0;
       
       for (int i = 0; i < stack.length; i++) {
           if(stack[i]=='\''){
               if(found==1){
                this.terms.add(memory);
                memory="";
                found=0;
                strike=0;
               }else{
                found++;
               }
           }else{
               if(found==1){
                strike++;
                memory += String.valueOf(stack[i]);
               }
           }
       }
    }

    public void getFirstLogic() {
        ArrayList<First> tempFirst = new ArrayList<First>();
        ArrayList<Struct> bigStruct = (ArrayList<Struct>) this.getBigStruct().clone();
        
        for (Struct struct : bigStruct) {
            ArrayList<ArrayList<String>> productionList = struct.getValue();
            //System.out.println(struct.getKey() + "=>" + struct.getValue());
            //nuevo First
            First first = new First();
            //cada produccion es un arrayList [+, T, S1], [-, T, S1], [e]
            ArrayList<String> tempOut = new ArrayList<>(); //arraylist con las terminales de firstLogic
            for (ArrayList<String> prList : productionList) {
                //solo la primera posicion de cada produccion
                String firstPosition = prList.get(0); 
                //verificamos si es una terminal
                ArrayList<String> terms = (ArrayList<String>) this.getTerms().clone();
                
                //ArrayList<String> tempOut = new ArrayList<>();
                
                for (String term : terms) {
                    
                    if(term.equals(firstPosition)){
                        //System.out.println(firstPosition + " == " + term);
                        first.setKey(struct.getKey()); //agregar la variable a first
                        tempOut.add(term);
                        break;
                    }else{
                        //si la primera posición no es un terminal
                        //verificamos que la posición que viene sea 
                        System.out.println(firstPosition);
                        //break;
                        //System.out.println(firstPosition + " <> " + term);
                    }
                }
                
            }
            first.setValue(tempOut);
            tempFirst.add(first);
            
        }
        
        for (First first : tempFirst) {
            System.out.println(first.getKey() +" => "+ first.getValue());
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void recursiveFirstLogic(String key){
        
        ArrayList<Struct> bigStruct = (ArrayList<Struct>) this.getBigStruct().clone();
        for (Struct struct : bigStruct) {
            if(struct.getKey() != key){
                recursiveFirstLogic(struct.getKey());
            }else{
                System.out.println(struct.getValue() + "=>" + struct.getValue());
                break;
            }
            //System.out.println(struct.getValue());
        }

    }
}
