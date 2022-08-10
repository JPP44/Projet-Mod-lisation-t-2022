package com.example;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Proprietaire extends Personne{
    
    // Créer un propriétaire à l'ajoute au json
    public static void addProprietaireToJson(String nomDUtilisateur, String motDePasse, String prenom, String nom){
        JSONObject nouveauProprietaire = new JSONObject();
        nouveauProprietaire.put("Nom d'utilisateur", nomDUtilisateur);
        nouveauProprietaire.put("Nombre d'unite creer", 0);
        JsonManager.addObjectToJsonList(nouveauProprietaire, "JsonProprietaire.json");
        Personne.addPersonneToJson(nomDUtilisateur, motDePasse, prenom, nom);
    }

    // Permet de trouver un propriétaire dans un fichier json
    public static JSONObject getProprietaire(String nomDUtilisateur){
        String listName = "proprietaireList";
        JSONObject file = JsonManager.translateFileToJSONObject("JsonProprietaire.json"); 
        int index = JsonManager.findIndexInJsonList(file, "Nom d'utilisateur", nomDUtilisateur, listName);
        
        if(index == -1){
            return null;
        }
        JSONArray jArray = (JSONArray)file.get(listName);
        return (JSONObject)jArray.get(index);
    }
}
