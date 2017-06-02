/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.ArrayList;
import java.util.Arrays;
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
    public ArrayList<Content> globalNotationWP = new ArrayList<Content>();
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
    public ArrayList<Symbol> getSymbolTable(){
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
                    ArrayList< ArrayList<String>  > vhos = new ArrayList<>();
                    SubSymbol hSubSymbol = new SubSymbol();
                    hSubSymbol.setKey(term);
                    hSubSymbol.setValue(vhos);
                    inKey.add(hSubSymbol);
                    
                    
                    if(pr == (terms.size()-1)){
                        ArrayList< ArrayList<String>  > vho = new ArrayList<>();
                        SubSymbol nSubSymbol = new SubSymbol();
                        nSubSymbol.setKey("$");
                        nSubSymbol.setValue(vho);
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
                int iprod = 0;
               String firstEnv = firstStruct.getKey(); //contiene la variable de primera
               //por cada terminal de primera
               for (String fterm : firstStruct.getValue()) {
                   if(fterm.equals("e")){ //si e esta en primero de A
                      //retorna un array list con las terminales de siguiente y $ si tuviese
                      ArrayList<String> termLast =  this.getProductionFromBigLast(firstEnv, fterm, iprod);
                      //retorna la producción que se va a insertar segun un indice especifico
                      ArrayList<String> termProd = this.getProdScene(firstEnv, fterm, iprod); 
                      
                      //agregar los valores devuelos a la tabla de simbolos
                       for (Symbol symbol : tableSymbol) {
                           if(symbol.getKey().equals(firstEnv)){ //buscamos la variable en la tabla de simbolos
                               //System.out.println(symbol);
                               for (SubSymbol subSymbol : symbol.getValue()) { //obtenemos la subclase
                                   for (String lt : termLast) {// por cada terminal de last
                                       if(subSymbol.getKey().equals(lt)){ //buscamos que sean las de las terminales de siguiente
                                            subSymbol.getValue().add(termProd); //agregamos la producción
                                           break;
                                       }
                                   }
                               }
                               break;
                           }
                       }
                      
                   }else{
                       ArrayList<String> temp;
                       temp = (ArrayList<String>) this.getProductionFromBig(firstEnv, fterm).clone();
                       //System.out.println(firstEnv + " con " + fterm + " = " + temp);
                       
                       //agregar los valores devuelos a la tabla de simbolos
                       for (Symbol symbol : tableSymbol) {
                           if(symbol.getKey().equals(firstEnv)){ //buscamos la variable en la tabla de simbolos
                               for (SubSymbol subSymbol : symbol.getValue()) { 
                                   if(subSymbol.getKey().equals(fterm)){ //buscamos en que subclase está la terminal
                                        subSymbol.getValue().add(temp);
                                   }
                               }
                               break;
                           }
                       }
                   }
                   iprod++;
               }
            }
            i++;
        }
        
        System.out.println("-------------------------------------------------------");
        
        for (Symbol symbol : tableSymbol) {
            //System.out.println(symbol.toString());
        }
        
        return tableSymbol;
    }
    
    public ArrayList<Object[]> drawSymbolTable(){
        ArrayList<Object[]> t = new ArrayList<>();
        ArrayList<Symbol> temp = this.getSymbolTable();
        
        for (Symbol symbol : temp) {
            Object[] row = new Object[symbol.getValue().size()+1];
            row[0] = symbol.getKey().toString();
            t.add(row);
            int i = 1;
            
            for (SubSymbol subSymbol : symbol.getValue()) {
                String r = "";
                for (ArrayList<String> itemSub : subSymbol.getValue()) {
                    r += itemSub.toString().replace(",", "");
                }
                row[i] = r;//subSymbol.getValue().toString();
                i++;
            }
            
            
            //System.out.println(symbol.toString());
        }
        
        for (Object[] objects : t) {
            //System.out.println(Arrays.toString(objects));
        }
        
        return t;
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
                        //System.out.println(production + " contiene " + term);
                        temp = (ArrayList<String>) production.clone();
                        //System.out.println(production + " no contiene " + term);
                    }
                }
                
            }
        }
        
        return temp;
    }

    private ArrayList<String> getProductionFromBigLast(String firstEnv, String fterm, int iprod) {
        int max = this.cleanContent.size() - 1;
        int i = 0;
        ArrayList<String> temp = new ArrayList<>();
        
        for (Last lStruct : this.last) {
            if(lStruct.getKey().equals(firstEnv)){
                //buscamos si siguiente contiene $
                for (String string : lStruct.getValue()) {
                    if(!string.equals("$")){
                        temp.add(string);
                    }
                }
            }
        }
        
        //tercer regla
        for (Last lStruct : this.last) {
            if(lStruct.getKey().equals(firstEnv)){
                //buscamos si siguiente contiene $
                if(lStruct.getValue().contains("$")){
                    temp.add("$");
                }
            }
        }
        
        
        return temp;
    }
    
    //retorna una producción en un indice especifico
    private ArrayList<String> getProdScene(String firstEnv, String fterm, int iprod) {
        ArrayList<String> temp = new ArrayList<>();
        for (Struct struct : this.getBigStruct()) {
            if(struct.getKey().equals(firstEnv)){
                temp = (ArrayList<String>) struct.getValue().get(iprod).clone();
            }
        }
        return temp;
    }

    
     public void termType(String cadena, ArrayList<Content> globalNotationWithP){
       ArrayList<String> tempVars = new ArrayList<>();
        //System.out.println("analizar; "+production);
        //convierte cada produccion en un arrar de caracteres
        char[] items = cadena.toCharArray();
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
                System.out.println(item + "No empieza con ' ");
                if(found==1){
                    //concatena todo lo que tenga hasta que encuentre el de cierre y reinicie variables
                    strike++;
                    memory += String.valueOf(item);
                    System.out.println(memory);
               }else{
                    
                    P += String.valueOf(item);
                    // Recorremos el array global de variables:producciones
                    for (int j = 0; j < globalNotationWithP.size(); j++) {
                        Content global = globalNotationWithP.get(j);
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
         System.out.println(tempVars);
        //return tempVars;
    }
     
    ArrayList<String> globalTermsForStack; 
    String globalEvaluate;
    ArrayList<String> termsFromTerms = new ArrayList<>();
    public void recursiveTerms(String evaluate){
        
        //String rEvaluate;
        String memory = "";
        ArrayList<Integer> removeIndex = new ArrayList<Integer>();
        
        if(evaluate!=""){
            char[] stackEvaluate = evaluate.toCharArray();
            for (int i = stackEvaluate.length - 1; i >= 0; i--) {
                memory += String.valueOf(stackEvaluate[i]);
                String term = isTerminalFromStack(memory);
                
                if(term!=""){
                    //System.out.println("apilar " +term );
                    termsFromTerms.add(term);
                    removeIndex.add(i);
                    String newEvaluate = removeInEvaluate(removeIndex, evaluate);
                    recursiveTerms(newEvaluate);
                    break;
                }else{
                    removeIndex.add(i);
                    //System.out.println("No encontrado " + memory);
                }
            }
        }
    }
    
    public String removeInEvaluate(ArrayList<Integer> index, String cad){
        //System.out.println(index);
        String temp = "";
        char[] ncad = cad.toCharArray();
        for (int i = 0; i < ncad.length; i++) {
            //recorremos el array de indexs a remover
            if(!index.contains(i)){
                temp += ncad[i];
            }
        }
        //System.out.println(temp);
        return temp;
    }
    public String isTerminalFromStack(String value){
         //ordena los caracteres
        char[] nt = value.toCharArray();
        String memo = "";
        String temp = "";
        
        for (int i = nt.length - 1; i >= 0; i--) {
            memo += String.valueOf(nt[i]);
        }
        
        //System.out.println("buscar " + memo);
        
        for (String sTerm : globalTermsForStack) {
            if(sTerm.equals(memo)){
                //System.out.println("encontrado " + sTerm);
                temp = sTerm;
                break;
            }
        }
        return temp;
    }
    
    ArrayList<Stack> globalStack = new ArrayList<Stack>();
    ArrayList<Symbol> validateSymbolTable = new ArrayList<>();
    
    public String[] getStackLogic(String type, ArrayList<Symbol> symbolTable, ArrayList<Struct> bigStruct, ArrayList<Content> globalNotationWithP, ArrayList<String> terms) {
        String evaluate = type.trim().replace(" ","");
        evaluate = evaluate.substring(0,evaluate.length()-1);
        globalTermsForStack = terms;
        this.recursiveTerms(evaluate);
        Collections.reverse(termsFromTerms);
        termsFromTerms.add("$");
        
        Stack initialStack = new Stack();
        initialStack.setI(1);
        //agregamos la entrada
        initialStack.setInput(termsFromTerms);
        //agregamos la variable inicial
        String initialVar = bigStruct.get(0).key;
        ArrayList<String> in = new ArrayList<>();
        in.add(initialVar);
        in.add("$");
        initialStack.setVars(in);
        
        //agregamos la clase al array de clases
        globalStack.add(initialStack);
        //cargamos la tabla de simbolos
        validateSymbolTable = symbolTable;
        
        
        //enviar tope de stack
        boolean flag = true;
        int rerun = 0;
        ArrayList<String> xc = new ArrayList<>();
        String a = "";
        while(flag){
             //escogemos la ultima posición del arreglo
            int bottom = globalStack.size();
            //tomamos la ultima posición del arreglo
            Stack bottomItem = globalStack.get((bottom-1));
            //tomamos el tope de la las variables
            String topVars = bottomItem.getVars().get(0);
            //tomamos el tope de la entrada
            String topInput = bottomItem.getInput().get(0);
            //System.out.println("analizando "  + bottomItem);
            a+=bottomItem.getVars()+","+bottomItem.getInput()+"," + bottomItem.getProduction() +"|";
            //globalStack2.add(bottomItem);
            //buscamos el contenido en la tabla de simbolos
            ArrayList<String> cont = foundVarToTermSymbol(topVars, topInput);
            //agregamos el contenido anterior y lo agregamos al nuevo sin contar el tope de la fila
            for (int i = 1; i < bottomItem.getVars().size(); i++) {
                cont.add(bottomItem.getVars().get(i));
            }
            
            if(topVars.equals(topInput)){
                if(topVars=="$" && topInput =="$"){
                    flag=false;
                }else{
                    
                    Stack stack = new Stack();
                    stack.setI(1);
                    stack.setVars(this.cleanStack(cont));
                    stack.setInput(getTo());
                    ArrayList<String> get = new ArrayList<>();
                    get.add(topVars + " → " + topInput);
                    stack.setProduction(get);
                    globalStack.add(stack);
                    //flag=false;
                }
            }else{
                if(cont.contains("e")){
                    cont.remove(0);
                    Stack stack = new Stack();
                    stack.setI(1);
                    stack.setVars(this.cleanStack(cont));
                    stack.setInput(bottomItem.getInput());
                    xc = bottomItem.getInput();
                    ArrayList<String> get = new ArrayList<>();
                    get.add(topVars + " → " + "e");
                    stack.setProduction(get);
                    globalStack.add(stack);
                }else{
                    Stack stack = new Stack();
                    stack.setI(1);
                    stack.setVars(this.cleanStack(cont));
                    stack.setInput(bottomItem.getInput());
                    xc = bottomItem.getInput();
                    ArrayList<String> get = new ArrayList<>();
                    globalStack.add(stack);
                }
                
            }
            
        }
        
        //System.out.println(a);
        //pitnamos el list
        for (Stack stack : globalStack) {
            //System.out.println(stack);
            
        }
        for (Stack stack : globalStack2) {
            //System.out.println(stack);
        }
        
        String[] stackItems = a.split("\\|");
        for (String stackItem : stackItems) {
            System.out.println(stackItem);
        }
        return stackItems;
    }
    
    public ArrayList<String> getTo(){
        globalStack = (ArrayList<Stack>) globalStack.clone();
        int bottom = globalStack.size();
        //tomamos la ultima posición del arreglo
        Stack bottomItem = globalStack.get((bottom-1));
        bottomItem.getInput().remove(0);
        return bottomItem.getInput();
    }
    
    ArrayList<Stack> globalStack2 = new ArrayList<Stack>();

    
    public void recursiveStack(Stack bottomItem){
        globalStack2.add(bottomItem);
        System.out.println("analizando " +  bottomItem);
        //tomamos el tope de la las variables
        String topVars = bottomItem.getVars().get(0);
        //tomamos el tope de la entrada
        String topInput = bottomItem.getInput().get(0);
	System.out.println("comparando " + topVars + ":" + topInput);		
        if(topVars.equals(topInput)){
            if(topVars!="&" && topInput!="$"){
                System.out.println("--- iguales ---");
              
            }
        }else{
            //buscamos el contenido en la tabla de simbolos
            ArrayList<String> cont = foundVarToTermSymbol(topVars, topInput);
            //agregamos el contenido anterior y lo agregamos al nuevo sin contar el tope de la fila
            for (int i = 1; i < bottomItem.getVars().size(); i++) {
                cont.add(bottomItem.getVars().get(i));
            }
            
            Stack stack = new Stack();
            stack.setI(1);
            stack.setVars(this.cleanStack(cont));
            stack.setInput(bottomItem.getInput());
            ArrayList<String> get = new ArrayList<>();
            
            if(!stack.getVars().get(0).equals(stack.getInput().get(0))){
                System.out.println("Agregando " + stack);
                System.out.println("--------------");
                globalStack.add(stack);
               
                int bottom = globalStack.size();
                //tomamos la ultima posición del arreglo
                Stack bottomItems = globalStack.get((bottom-1));

                recursiveStack(bottomItems);
            }else{
                //globalStack.add(stack);
                int bottom = globalStack.size();
                //tomamos la ultima posición del arreglo
                Stack bottomItems = globalStack.get((bottom-1));
                recursiveStack(bottomItems);
            }
            
        }
    }
    
    public void add(){
        
    }
    
    //busca la variable y el terminal en la tabla de simbolos
    public ArrayList<String> foundVarToTermSymbol(String var, String term){
        ArrayList<String> temp = new ArrayList<>();
        for (Symbol symbol : validateSymbolTable) {
            if(symbol.getKey().equals(var)){
                //System.out.println(symbol);
                for (SubSymbol subSymbol : symbol.getValue()) {
                    if(subSymbol.getKey().equals(term)){
                        temp = subSymbol.getValue().get(0); //solo la posición del primer vector de valores
                        break;
                    }
                }
                break;
            }
        }
        return temp;
    }
    
    public Integer validateTopStack(Stack item){
        int temp = 0;
        //tomamos el tope de la las variables
        String topVars = item.getVars().get(0);
        //tomamos el tope de la entrada
        String topInput = item.getInput().get(0);
        
        if(topVars.equals(topInput)){
            //System.out.println("son iguales");
            if(topVars == "$" && topInput == "$"){
                System.out.println("son iguales $");
                temp = 2;
            }else{
                temp = 1;
            }
        }
        
        //System.out.println("tope de variables " + topVars);
        //System.out.println("tope de entrada" + topInput);
        
        return temp;
    }

  
    
    
}
