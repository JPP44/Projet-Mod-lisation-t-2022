package com.example;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Unite {
    
    // Peremet de créer une unité et de l'ajouter à un fichier json
    public static void addUniteToJson(String type, String adresse, String ville, long aire, String nomProprietaire, long nbChambre, long nbSalleDeBain, String date, String condition){
        JSONObject unite = new JSONObject();
        unite.put("Type", type);
        unite.put("Adresse", adresse);
        unite.put("Ville", ville);
        unite.put("Aire", aire);
        unite.put("Nom d'utilisateur du proprietaire", nomProprietaire);
        if(nbChambre > 0){
            unite.put("Nombre de chambre", nbChambre);
        }
        unite.put("Nombre de salle de bain", nbSalleDeBain);
        unite.put("Date de contruction", date);
        unite.put("Condition", condition);
        unite.put("Etat", "Libre");
        unite.put("Nombre de proposition de bail creer", 0);
        unite.put("Nombre de bail creer", 0);
        unite.put("Possede une proposition de bail", false);

        JSONObject proprietaire = JsonManager.getJsonObjectOfAList("JsonProprietaire.json", "Nom d'utilisateur", nomProprietaire);
        String id = nomProprietaire + proprietaire.get("Nombre d'unite creer").toString();
        unite.put("Identifiant", id);
        long nouveauCompteur = (long)proprietaire.get("Nombre d'unite creer")+1;
        JsonManager.modifyIntArgumentOfList("JsonProprietaire.json", "Nom d'utilisateur", nomProprietaire, "Nombre d'unite creer", nouveauCompteur);

        JsonManager.addObjectToJsonList(unite, "JsonUnite.json");
    }
}
