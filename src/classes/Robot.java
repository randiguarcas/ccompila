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
        return cleanStack(this.terms);
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

    public ArrayList<First> first = new ArrayList<>();
    public ArrayList<Last> last = new ArrayList<>();
    public ArrayList<String> globalTemp = new ArrayList<>();
    
    public ArrayList<First> getFirstLogic() {
        //recorre cada una de las variables con sus producciones
        for (Struct struct : this.getBigStruct()) {
            //limpiar el globalTemporal que apila las terminales
            globalTemp.clear();
            
            //t es un array con cada una de las terminales de la variable
            ArrayList<String> t = new ArrayList<>();
            //por cada produccion de la variable 
            for (ArrayList<String> producction : struct.getValue()) {
                //función recursiva que retorna un globalTemp de terminales apiladas
                t = (ArrayList<String>) this.recursiveFirstLogic(struct.getKey(), producction).clone();
            }
            
            // crea un nuevo objeto de first según la variable que se está analizando y el resultador de terminales 
            First first = new First();
            first.setKey(struct.getKey());
            first.setValue(t);
            this.first.add(first);
        }
        
        
        return this.first;
    }
    
    private ArrayList<String> recursiveFirstLogic(String key, ArrayList<String> production) {
        //primera posicion de cada produccion  
        String firstPosition = production.get(0);
        //buscamos si la primera posición se encuentra en el arraylist de terminales
        if(!this.getTerms().contains(firstPosition)){
            //Si el array de terminales no contiene la el dato de firstPosition
            //Buscar en la structura la variable  A = firstPosition
            for (Struct struct : this.getBigStruct()) {
                if(struct.getKey().equals(firstPosition)){
                    //encontrada la variable segun firstPosition en la estructura de variables con terminales
                    for (ArrayList<String> producction : struct.getValue()) {
                        //por cada produccion de la variable inicia la recursividad hasta encontrar las terminales
                        this.recursiveFirstLogic(struct.getKey(), producction);
                    }
                }
            }
        }else{
            //si el valor de firstPosition se encuentra en el array de terminales
            //apila cada un de los valor a globalTemp
            globalTemp.add(firstPosition);
        }
        //retorna globalTemp
        return globalTemp;
    }

    public ArrayList<Last> getLastLogic() {
        //por cada elemento en struct
        for (int i = 0; i < this.getBigStruct().size(); i++) {
            Struct struct = this.getBigStruct().get(i);
            //primer evento
            if(i==0){
                //tomamos la primer variable "S"
                String firstEnv = struct.getKey().toString();
                //buscamos en la estructura en que produccion está la primera variable
                for (Struct lastStruct : this.getBigStruct()) {
                    //buscamos en cada una de las producciones cual contiene la variable
                    int prx = 0;
                    for (ArrayList<String> production : lastStruct.getValue()) {
                        for (int j = 0; j < production.size(); j++) {
                            //buscamos en que produccion está la variable que estamos buscando
                            if(production.get(j).equals(firstEnv)){
                                boolean whoIs = this.emptyOrTerm(production, j);
                                if(whoIs){
                                    //si retorna 1 hay un valor siguiente para ser verigficado si es o no una terminal
                                    String last = production.get(j + 1);
                                    //buscamos si last es una terminal
                                    if(this.getTerms().contains(last)){
                                        //agregamos el terminal y le concatenamos $ a un nuevo objeto Last
                                        ArrayList<String> tempTerm = new ArrayList<>();
                                        tempTerm.add(last);
                                        tempTerm.add("$");
                                        Last tlast = new Last();
                                        tlast.setValue(tempTerm);
                                        tlast.setKey(firstEnv);

                                        this.last.add(tlast);
                                    }else{
                                        System.out.println(last + " no es una terminal");
                                    }
                                }else{
                                    //si no hay un  valor siguiente se saca siguiente
                                    System.out.println("No hay un valor para el primer evento de siguiente");
                                }
                            }

                        }
                    }
                }
            }else{
                if(i > 0){
                   //tomamos el resto de variableS
                    String firstEnv = struct.getKey().toString();
                    //System.out.println("Analizando : " + firstEnv);
                    //buscamos en que producciones se encuentran estas variables
                    for (Struct lStruct : this.getBigStruct()) {
                        //buscamos en cada producción cual tiene 
                        String keyOut = lStruct.getKey(); //almacena que variable está sacando la produccion
                        for (ArrayList<String> inProduccion : lStruct.getValue()) {
                            //por cada produccion buscamos si contiene la varible y en que posición
                            for (int j = 0; j < inProduccion.size(); j++) {
                                String prod = inProduccion.get(j);
                                //si la produccion contiene la variable que se está buscando
                                if(prod.equals(firstEnv)){
                                    //System.out.println(firstEnv+ " en " + keyOut + " : " + inProduccion  + " en index " +j); 
                                    //si la variable que esta sacando el valor es distinta a la que estamos analizando S1 != S
                                    if(!keyOut.equals(firstEnv)){
                                        //buscamos si puede tiene siguiente
                                        boolean inFlag = this.emptyOrTerm(inProduccion, j);
                                        if(inFlag){
                                            //si retorna 1 hay un valor siguiente para ser verigficado si es o no una terminal
                                            String last = inProduccion.get(j + 1);
                                            //System.out.println("siguiente de " + firstEnv + " : "  + last);
                                            //verificamos si siguiente es una variable o una terminal
                                            if(this.getTerms().contains(last)){
                                                //System.out.println(last+" es una terminal");
                                            }else{
                                                //si last no es igual que la variable que lo está sacando
                                                if(!keyOut.equals(last)){
                                                    //buscamos primera de last
                                                    ArrayList<String> tmpFirst = new ArrayList<>();
                                                    ArrayList<String> tmpLast = new ArrayList<>();
                                                    for (First tFirst : this.getFirstLogic()) {   
                                                        if(tFirst.getKey().equals(last)){
                                                            //System.out.println("Primera de " + last + " : " + tFirst.getValue());
                                                            tmpFirst = (ArrayList<String>) tFirst.getValue().clone();
                                                            break;
                                                        }else{
                                                            //System.out.println(last + " No encontrado en global de first");
                                                        }  
                                                     }
                                                    //buscamos siguiente de last
                                                    for (Last tLast : this.last) {
                                                        if (tLast.getKey().equals(last)) {
                                                            tmpLast = (ArrayList<String>) tLast.getValue().clone();
                                                            //System.out.println("Siguiente de " + last + " : " + tLast.getValue());
                                                            break;
                                                        }else{
                                                            //System.out.println(last  + " no encontrado en Last");
                                                        }
                                                    }
                                                    //generamos un nuevo arraylist de primera y siguiente
                                                    Last nLast = new Last();
                                                    nLast.setKey(firstEnv);
                                                    nLast.setValue(this.lastArray(tmpFirst, tmpLast));
                                                    this.last.add(nLast);
                                                }else{
                                                    //System.out.println(keyOut + "=" + last);
                                                }
                                                //System.out.println(last+" no es una terminal");
                                            }
                                        }else{
                                            //si no hay siguiente [T, S1] analizando S1
                                            //buscamos la variable que lo está sacando y la buscamos en el global de Last
                                            ArrayList<Last> tempVLast = (ArrayList<Last>) this.last.clone();
                                            for (Last globalLast : tempVLast) {
                                                //si es encontrada la variable que saco el valor dentro de la Last
                                                if(globalLast.getKey().equals(keyOut)){
                                                    //pasamos el valor de siguiente de last y lo agregamos al global de last
                                                    Last pLast = new Last();
                                                    pLast.setKey(firstEnv);
                                                    pLast.setValue(globalLast.getValue());
                                                    this.last.add(pLast);
                                                    //System.out.println("Siguiente de " + keyOut + globalLast.getValue());
                                                    //System.out.println("analizando " + firstEnv +", Siguiente de " + globalLast.getKey() + " : " + globalLast.getValue());
                                                }else{
                                                    //System.out.println(keyOut + " no encontrado en global de Last");
                                                }
                                            }
                                            //System.out.println("ya no hay variable a la par para " + inProduccion);
                                        }
                                        //System.out.println(inFlag);
                                        //System.out.println(firstEnv+ " in " + inProduccion + " out " + keyOut + " index " +j); 
                                    }
                                    break;
                                }
                            }
                         
                        }
                    } 
                }
            }
        }
        
        return this.last;
    }
    
    private void recursiveLastLogic(String key, ArrayList<String> production){
        //buscar la variable (key) en la primera producción en donde la encuentra
        
        for (Struct struct : this.getBigStruct()) {
            
        }
    }
 
    private boolean  emptyOrTerm(ArrayList<String> production, int i) {
        boolean flag = false;
        int fullSize = (production.size());
        int last = (i + 1);
        
        if(fullSize > last){
            flag = true;
            //System.out.println("aun hay elemento");
        }else{
            flag = false;
            //System.out.println("ya no hay elemento");
        }
        return flag;
    }
    
    
    //retorna un array de first y last
    private ArrayList<String>lastArray(ArrayList<String> tmpFirst, ArrayList<String> tmpLast) {
        //removemos 'e'
        ArrayList<String> tFirst = new ArrayList<>();
        
        for (String first : tmpFirst) {
            if(!first.equals("e")){
                tFirst.add(first);
            }
        }
        
        for (String last : tmpLast) {
            tFirst.add(last);
        }
        return tFirst;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //elimina repeditos
    private ArrayList<String> cleanStack(ArrayList<String> content){
        ArrayList<String> stack = new ArrayList<String>();
        for (int i = 0; i < content.size(); i++) {
            String value = content.get(i);
            if(!stack.contains(value) ){
                    stack.add(value);
            }            
        }
        return stack;
    }
    
    //retorna terminales según función primera
    public void getSymbolTable(){
        ArrayList<Symbol> tableSymbol = new ArrayList<>();
        ArrayList<String> enviroments = (ArrayList<String>) this.getEnviroment().clone();
        ArrayList<String> terms = this.getTerms();
        
        for (String enviroment : enviroments) {
            Symbol pSymbol = new Symbol();
            pSymbol.setKey(enviroment);
            
            
            
            int pr = 1;
            ArrayList<SubSymbol> inKey = new ArrayList<>();
            for (String term : terms) {
                if(!term.equals("e")){
                    SubSymbol hSubSymbol = new SubSymbol();
                    hSubSymbol.setKey(term);
                    inKey.add(hSubSymbol);
                    
                    
                    if(pr == (terms.size()-1)){
                        SubSymbol nSubSymbol = new SubSymbol();
                        nSubSymbol.setKey("$");
                        inKey.add(nSubSymbol);
                    }
                    pr++;
                }
            }
            pSymbol.setValue(inKey);
            tableSymbol.add(pSymbol);
        }
        
        //bugfix
        int max = (this.cleanContent.size()-1);
        int i = 0;
        
        //por cada terminal en Primero(A)
        for (First firstStruct : this.getFirstLogic()) {
            if(i <= max){
               String firstEnv = firstStruct.getKey(); //contiene la variable de primera
               //por cada terminal de primera
               for (String fterm : firstStruct.getValue()) {
                   if(fterm.equals("e")){ //si e esta en primero de 
                       
                   }else{
                       ArrayList<String> temp;
                       temp = (ArrayList<String>) this.getProductionFromBig(firstEnv, fterm).clone();
                       //System.out.println(firstEnv + " con " + fterm + " = " + temp);
                       
                       //agregar los valores devuelos a la tabla de simbolos
                       for (Symbol symbol : tableSymbol) {
                           if(symbol.getKey().equals(firstEnv)){ //buscamos la variable en la tabla de simbolos
                               ArrayList<SubSymbol> tSubSymbol = (ArrayList<SubSymbol>) symbol.getValue().clone();
                               for (SubSymbol subSymbol : tSubSymbol) {
                                   if(subSymbol.getKey().equals(fterm)){
                                       subSymbol.getValue().add(temp);
                                   }
                               }
                           }
                       }
                   }
               }
            }
            i++;
        }
        
        System.out.println("-------------------------------------------------------");
        
        for (Symbol symbol : tableSymbol) {
            System.out.println(symbol.toString());
        }

    }
    
    public ArrayList<String> getProductionFromBig(String env, String term){
        ArrayList<String>temp = new ArrayList<>();
        //buscamos en big struct la variable
        for (Struct struct : this.getBigStruct()) {
            if(struct.getKey().equals(env)){
                //si cada produccion contiene la terminal retornar la producción
                for (ArrayList<String> production : struct.getValue()) {
                    if(production.contains(term)){
                        temp = (ArrayList<String>) production.clone();
                        //System.out.println(production + " contiene " + term);
                        break;
                    }else{
                        temp = (ArrayList<String>) production.clone();
                        //System.out.println(production + " no contiene " + term);
                    }
                }
                
            }
        }
        
        return temp;
    }
}
