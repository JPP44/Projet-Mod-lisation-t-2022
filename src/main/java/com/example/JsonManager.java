package com.example;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class JsonManager {

    //////////////////////////////////////////////////////////////////
    ////// Foctions generales pour manipuler les fichiers JSON  //////
    //////////////////////////////////////////////////////////////////


    
    /// Met a jour un fichier JSON en fournisant un JSONObject qui va servir de nouveau de base pour JSON
    public static void updateJsonFile(String targetFile, JSONObject updatedFileInfo){
        try(FileWriter file = new FileWriter(targetFile)){
            file.write(updatedFileInfo.toJSONString());
            file.flush();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    

    /// Converti un fichier JSON en objet de type JSONObject
    public static JSONObject translateFileToJSONObject(String fileName){
        JSONParser jp = new JSONParser();
        try(FileReader reader = new FileReader(fileName)){
            Object object = jp.parse(reader);
            JSONObject jObject = (JSONObject)object;
            return jObject;

        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /// Permet de modifier l'agument d'un l'un des objet de la liste d'un des fichier JSON en entrant certaines valeurs 
    public static void modifyArgumentOfList(String targetFile, String keySearch, String keyValue, String keyChanged, String newValue){
        
        JSONObject file = translateFileToJSONObject(targetFile);
        String rawListName = file.keySet().toString();
        String listName = rawListName.replace("[", "").replace("]", "");
        int index = findIndexInJsonList(file, keySearch, keyValue, listName);

        JSONArray jArray = (JSONArray)file.get(listName);
        JSONObject object = (JSONObject)jArray.get(index);
        //object.remove(keyChanged);
        object.put(keyChanged, newValue);
        updateJsonFile(targetFile, file);

    }

    /// Permet de modifier l'agument de type bool d'un l'un des objet de la liste d'un des fichier JSON en entrant certaines valeurs 
    public static void modifyBoolArgumentOfList(String targetFile, String keySearch, String keyValue, String keyChanged, Boolean newValue){
        
        JSONObject file = translateFileToJSONObject(targetFile);
        String rawListName = file.keySet().toString();
        String listName = rawListName.replace("[", "").replace("]", "");
        int index = findIndexInJsonList(file, keySearch, keyValue, listName);

        JSONArray jArray = (JSONArray)file.get(listName);
        JSONObject object = (JSONObject)jArray.get(index);
        //object.remove(keyChanged);
        object.put(keyChanged, newValue);
        updateJsonFile(targetFile, file);

    }

    /// Permet de modifier l'agument de type long d'un l'un des objet de la liste d'un des fichier JSON en entrant certaines valeurs
    public static void modifyIntArgumentOfList(String targetFile, String keySearch, String keyValue, String keyChanged, long newValue){
        
        JSONObject file = translateFileToJSONObject(targetFile);
        String rawListName = file.keySet().toString();
        String listName = rawListName.replace("[", "").replace("]", "");
        int index = findIndexInJsonList(file, keySearch, keyValue, listName);

        JSONArray jArray = (JSONArray)file.get(listName);
        JSONObject object = (JSONObject)jArray.get(index);
        //object.remove(keyChanged);
        object.put(keyChanged, newValue);
        updateJsonFile(targetFile, file);

    }

    /// Permet de modifier l'agument de type JSONObject d'un l'un des objet de la liste d'un des fichier JSON en entrant certaines valeurs
    public static void modifyJObjectArgumentOfList(String targetFile, String keySearch, String keyValue, String keyChanged, JSONObject newValue){
        
        JSONObject file = translateFileToJSONObject(targetFile);
        String rawListName = file.keySet().toString();
        String listName = rawListName.replace("[", "").replace("]", "");
        int index = findIndexInJsonList(file, keySearch, keyValue, listName);

        JSONArray jArray = (JSONArray)file.get(listName);
        JSONObject object = (JSONObject)jArray.get(index);
        //object.remove(keyChanged);
        object.put(keyChanged, newValue);
        updateJsonFile(targetFile, file);

    }

    /// Permet de modifier l'agument de type JSONArray d'un l'un des objet de la liste d'un des fichier JSON en entrant certaines valeurs
    public static void modifyJArrayArgumentOfList(String targetFile, String keySearch, String keyValue, String keyChanged, JSONArray newValue){
        
        JSONObject file = translateFileToJSONObject(targetFile);
        String rawListName = file.keySet().toString();
        String listName = rawListName.replace("[", "").replace("]", "");
        int index = findIndexInJsonList(file, keySearch, keyValue, listName);

        JSONArray jArray = (JSONArray)file.get(listName);
        JSONObject object = (JSONObject)jArray.get(index);
        //object.remove(keyChanged);
        object.put(keyChanged, newValue);
        updateJsonFile(targetFile, file);

    }

    // Permet de modifier un objet d'un jsonobject dans une liste de json 
    public static void modifyJSONObjectOfAnObject(String targetFile, String keySearch, String keyValue, String keyChanged, String newValue) throws ParseException{
        
        JSONObject file = translateFileToJSONObject(targetFile);
        String rawListName = file.keySet().toString();
        String listName = rawListName.replace("[", "").replace("]", "");
        int index = findIndexInJsonList(file, keySearch, keyValue, listName);

        JSONArray jArray = (JSONArray)file.get(listName);
        JSONObject object = (JSONObject)jArray.get(index);
        //object.remove(keyChanged);
        JSONParser parser =new JSONParser();
        JSONObject jNewValue = (JSONObject)parser.parse(newValue);
        object.put(keyChanged, jNewValue);
        updateJsonFile(targetFile, file);

    }


    /// Trouve l'indexe d'un objet dans une liste à partir de la valeur d'une de ses clé
    public static int findIndexInJsonList(JSONObject obj, String keySearch, String keyValue,String listName){
        
        JSONArray arr = (JSONArray)obj.get(listName);

        int index = 0;
        for (Object object : arr) {
            JSONObject jobject = (JSONObject)object;
            if(keyValue.equals(jobject.get(keySearch).toString())){
                return index;
            }
            index++;
        }

        return -1;
    }


    /// Ajoute un objet a la liste d'un des fichiers JSON
    public static void addObjectToJsonList(JSONObject object, String targetFile){
        JSONObject file = translateFileToJSONObject(targetFile);
        String rawListName = file.keySet().toString();
        String listName = rawListName.replace("[", "").replace("]", "");
        JSONArray jArray = (JSONArray)file.get(listName);
        jArray.add(object);
        updateJsonFile(targetFile, file);
    } 

    // Supprime un certain objet d'une liste de json 
    public static void removeObjectToJsonList(String keySearch, String keyValue, String targetFile){
        JSONObject file = translateFileToJSONObject(targetFile);
        String rawListName = file.keySet().toString();
        String listName = rawListName.replace("[", "").replace("]", "");
        JSONArray jArray = (JSONArray)file.get(listName);
        int index = findIndexInJsonList(file, keySearch, keyValue, listName);
        jArray.remove(index);
        updateJsonFile(targetFile, file);
    }


    /// Permet de retourner un JSONObject recherché dans une liste d'un JSON
    public static JSONObject getJsonObjectOfAList(String targetFile, String keySearch, String keyValue){
        
        JSONObject file = translateFileToJSONObject(targetFile);
        String rawListName = file.keySet().toString();
        String listName = rawListName.replace("[", "").replace("]", "");
        int index = findIndexInJsonList(file, keySearch, keyValue, listName);

        JSONArray jArray = (JSONArray)file.get(listName);
        JSONObject object = (JSONObject)jArray.get(index);
        return object;
    } 

    // Retourne le JsonArray d'un des json  
    public static JSONArray getArrayOfJsonFile(String targetFile){
        JSONObject file = translateFileToJSONObject(targetFile);
        String rawListName = file.keySet().toString();
        String listName = rawListName.replace("[", "").replace("]", "");
        JSONArray jArray = (JSONArray)file.get(listName);
        return jArray;
    }

}