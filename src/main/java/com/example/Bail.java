package com.example;

import org.json.JSONArray;
import org.json.simple.JSONObject;

public class Bail {

    //Methodes statiques(peu y acceder sans instancier un objet de cette classe) 
    public static void addBailToJson(JSONObject proposition, String nomAssureur, String numeroDAssurance, String nomLocataire){
        JSONObject bail = new JSONObject();
        bail.put("Loyer", (long)proposition.get("Loyer"));
        bail.put("Date de debut", (JSONObject)proposition.get("Date de debut"));
        bail.put("Date de fin", (JSONObject)proposition.get("Date de fin"));
        bail.put("Periode", proposition.get("Periode").toString());
        bail.put("Nombre de periode", (long)proposition.get("Nombre de periode"));
        bail.put("Identifiant de l'unite", proposition.get("Identifiant de l'unite").toString());
        bail.put("Proprietaire", proposition.get("Proprietaire").toString());
        bail.put("Loactaire", nomLocataire);
        bail.put("Renouvelable", (Boolean)proposition.get("Renouvelable"));
        bail.put("Supplements", proposition.get("Supplements"));
        bail.put("Date de creation", (JSONObject)TimeManager.getPresentTime());
        bail.put("Termine", false);
        JSONObject assurance = new JSONObject();
        assurance.put("Nom assureur", nomAssureur);
        assurance.put("Numero d'assurance", numeroDAssurance);
        bail.put("Assurance", assurance);
        JSONObject unite = JsonManager.getJsonObjectOfAList("JsonUnite.json", "Identifiant", proposition.get("Identifiant de l'unite").toString());
        String id = proposition.get("Proprietaire").toString() + unite.get("Nombre de proposition de bail creer").toString();
        bail.put("Identifiant", id);
        long nouveauCompteur = (long)unite.get("Nombre de bail creer")+1;
        JsonManager.modifyIntArgumentOfList("JsonUnite.json", "Identifiant", unite.get("Identifiant").toString(), "Nombre de bail creer", nouveauCompteur);
        Solde.addSoldeToJson(id, proposition, nomLocataire);
        JsonManager.removeObjectToJsonList("Identifiant", proposition.get("Identifiant").toString(), "JsonPropositionDeBail.json");
        String etat = unite.get("Etat").toString();
        if(etat.equals("Libre")){etat = "Loue";}
        else{etat = "Reserve";}
        JsonManager.modifyBoolArgumentOfList("JsonUnite.json", "Identifiant", unite.get("Identifiant").toString(), "Possede une proposition de bail", false);
        JsonManager.modifyArgumentOfList("JsonUnite.json", "Identifiant", unite.get("Identifiant").toString(), "Etat", etat);
        JsonManager.modifyBoolArgumentOfList("JsonLocataire.json", "Nom d'utilisateur", nomLocataire, "Cherche location", false);
        JsonManager.modifyArgumentOfList("JsonLocataire.json", "Nom d'utilisateur", nomLocataire, "Proprietaire actuel", unite.get("Nom d'utilisateur du proprietaire").toString());
        JsonManager.addObjectToJsonList(bail, "JsonBail.json");
    }
}
