package com.example;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Personne {
    
    //Methodes statiques(peu y acceder sans instancier un objet de cette classe) 
    public static void addPersonneToJson(String nomDUtilisateur, String motDePasse, String prenom, String nom){
        JSONObject nouvellePersonne = new JSONObject();
        nouvellePersonne.put("Nom d'utilisateur", nomDUtilisateur);
        nouvellePersonne.put("Mot de passe", motDePasse);
        nouvellePersonne.put("Prenom", prenom);
        nouvellePersonne.put("Nom", nom);
        JsonManager.addObjectToJsonList(nouvellePersonne, "JsonPersonne.json");
    }


    public static JSONObject getPersonne(String nomDUtilisateur){
        String listName = "personneList";
        JSONObject file = JsonManager.translateFileToJSONObject("JsonPersonne.json"); 
        int index = JsonManager.findIndexInJsonList(file, "Nom d'utilisateur", nomDUtilisateur, listName);
        
        if(index == -1){
            return null;
        }
        JSONArray jArray = (JSONArray)file.get(listName);
        return (JSONObject)jArray.get(index);
    }
}
