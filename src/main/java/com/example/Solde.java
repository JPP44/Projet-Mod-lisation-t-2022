package com.example;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Solde {
    
    // Créer un nouveau solde et l'ajoute au json
    public static void addSoldeToJson(String bailId, JSONObject propDeBail,String nomLocataire){
        JSONObject solde = new JSONObject();
        solde.put("Locataire", nomLocataire);
        solde.put("Proprietaire", propDeBail.get("Proprietaire"));
        solde.put("Identifiant du bail", bailId);
        solde.put("Fini de payer", false);
        solde.put("Payer", 0);
        solde.put("Identifiant de l'unite", propDeBail.get("Identifiant de l'unite").toString());
        long nbPeriode = (long)propDeBail.get("Nombre de periode");
        long loyer = (long)propDeBail.get("Loyer")*nbPeriode;
        solde.put("Loyer",loyer);
        JSONArray coutSupplements = new JSONArray();
        JSONArray supplements = (JSONArray)propDeBail.get("Supplements");
        for (Object object : supplements) {
            JSONObject jobject = (JSONObject)object;
            JSONObject supplement = new JSONObject();
            supplement.put("Nom", jobject.get("Nom").toString());
            long cout = (long)jobject.get("Cout")*nbPeriode;
            supplement.put("Cout", cout);
            coutSupplements.add(supplement);
        }
        solde.put("Cout supplements", coutSupplements);
        long total = loyer;
        for (Object object2 : coutSupplements) {
            JSONObject supplement = (JSONObject)object2;
            total = total + ((long)supplement.get("Cout"));
        }
        solde.put("Total", total);
        JSONArray paiements = new JSONArray();
        solde.put("Paiements", paiements);
        JsonManager.addObjectToJsonList(solde, "JsonSolde.json");
    }

    // Afficher tout l'historique de paiement d'un propriétaire
    public static void afficherSoldeEtHistoriqueDePaiementProprietaire(String nomProprietaire){
        JSONArray soldes = JsonManager.getArrayOfJsonFile("JsonSolde.json");
        System.out.println("\n////// Historique des paiements - Propriétaire //////\n");
        for (Object object : soldes) {
            JSONObject solde = (JSONObject)object;
            if(solde.get("Proprietaire").toString().equals(nomProprietaire)){
                JSONObject unite = JsonManager.getJsonObjectOfAList("JsonUnite.json", "Identifiant", solde.get("Identifiant de l'unite").toString());
                JSONObject bail = JsonManager.getJsonObjectOfAList("JsonBail.json", "Identifiant", solde.get("Identifiant du bail").toString());
                JSONObject dateDebut = (JSONObject)bail.get("Date de debut");
                JSONObject locataire = JsonManager.getJsonObjectOfAList("JsonPersonne.json", "Nom d'utilisateur", solde.get("Locataire").toString());
                System.out.println("//// "+unite.get("Adresse").toString()+" - "+
                dateDebut.get("Annee").toString()+"/"+dateDebut.get("Mois").toString()+"/"+dateDebut.get("Jour").toString()+" ////"+
                "\n/// Locataire: "+locataire.get("Prenom").toString()+" "+locataire.get("Nom").toString()+
                "\n- Loyer: "+solde.get("Loyer")+"$");
                for (Object object2 : (JSONArray)solde.get("Cout supplements")) {
                    JSONObject supplement = (JSONObject)object2;
                    System.out.println("- "+supplement.get("Nom")+": "+supplement.get("Cout")+"$");
                }
                //Affiche les differents paiements 
                System.out.println("\n- Paiements:");
                JSONArray paiements = (JSONArray)solde.get("Paiements");
                for (Object object2 : paiements) {
                    JSONObject paiement = (JSONObject)object2;
                    JSONObject date = (JSONObject)paiement.get("Date");
                    System.out.println("    * Date: "+date.get("Annee")+"/"+date.get("Mois")+"/"+date.get("Jour")+" "+
                    date.get("Heure")+":"+date.get("Minute")+":"+date.get("Seconde")+
                    ". Montant: "+paiement.get("Montant")+"$.");
                }

                //Afficher l'historique des paiements
                System.out.println("\n- Total: "+solde.get("Total")+"$"+
                "\n- Payé: "+solde.get("Payer")+"$\n------------------------------\n");
            }
        }
    }


    // Afficher tout l'historique de paiement d'un locataire
    public static void afficherSoldeEtHistoriqueDePaiementLocataire(String nomLocataire){
        JSONArray soldes = JsonManager.getArrayOfJsonFile("JsonSolde.json");
        System.out.println("\n////// Historique des paiements - Locataire //////\n");
        long soldeDuTotal = 0;
        for (Object object : soldes) {
            JSONObject solde = (JSONObject)object;
            if(solde.get("Locataire").toString().equals(nomLocataire)){
                JSONObject unite = JsonManager.getJsonObjectOfAList("JsonUnite.json", "Identifiant", solde.get("Identifiant de l'unite").toString());
                JSONObject bail = JsonManager.getJsonObjectOfAList("JsonBail.json", "Identifiant", solde.get("Identifiant du bail").toString());
                JSONObject dateDebut = (JSONObject)bail.get("Date de debut");
                JSONObject proprietaire = JsonManager.getJsonObjectOfAList("JsonPersonne.json", "Nom d'utilisateur", solde.get("Proprietaire").toString());
                System.out.println("//// "+unite.get("Adresse").toString()+" - "+
                dateDebut.get("Annee").toString()+"/"+dateDebut.get("Mois").toString()+"/"+dateDebut.get("Jour").toString()+" ////"+
                "\n/// Propriétaire: "+proprietaire.get("Prenom").toString()+" "+proprietaire.get("Nom").toString()+
                "\n- Loyer: "+solde.get("Loyer")+"$");
                for (Object object2 : (JSONArray)solde.get("Cout supplements")) {
                    JSONObject supplement = (JSONObject)object2;
                    System.out.println("- "+supplement.get("Nom")+": "+supplement.get("Cout")+"$");
                }
                //Affiche les differents paiements 
                System.out.println("\n- Paiements:");
                JSONArray paiements = (JSONArray)solde.get("Paiements");
                for (Object object2 : paiements) {
                    JSONObject paiement = (JSONObject)object2;
                    JSONObject date = (JSONObject)paiement.get("Date");
                    System.out.println("    * Date: "+date.get("Annee")+"/"+date.get("Mois")+"/"+date.get("Jour")+" "+
                    date.get("Heure")+":"+date.get("Minute")+":"+date.get("Seconde")+
                    ". Montant: "+paiement.get("Montant")+"$.");
                }

                //Afficher l'historique des paiements
                long total = (long)solde.get("Total");
                long payer = (long)solde.get("Payer");
                soldeDuTotal = soldeDuTotal + (total-payer);
                System.out.println("\n- Total: "+total+"$"+
                "\n- Payé: "+payer+"$\n------------------------------\n");
            }

            System.out.println("Total des soldes dus: "+soldeDuTotal+"$\n------------------------------\n");
        }
    }


    // Entrer un paiement fait par le locataire
    public static void saisirPaiement(String nomProprietaire){
        JSONArray soldes = JsonManager.getArrayOfJsonFile("JsonSolde.json");
        System.out.println("\nVoici vos soldes: \n");
        ArrayList<String> indexList = new ArrayList<String>();
        indexList.add("r");
        ArrayList<String> baiIdList = new ArrayList<String>();
        int compte = 0;
        for (Object object : soldes) {
            JSONObject solde = (JSONObject)object;
            if(solde.get("Proprietaire").toString().equals(nomProprietaire)&&(Boolean)solde.get("Fini de payer")){
                JSONObject unite = JsonManager.getJsonObjectOfAList("JsonUnite.json", "Identifiant", solde.get("Identifiant de l'unite").toString());
                JSONObject bail = JsonManager.getJsonObjectOfAList("JsonBail.json", "Identifiant", solde.get("Identifiant du bail").toString());
                JSONObject dateDebut = (JSONObject)bail.get("Date de debut");
                JSONObject locataire = JsonManager.getJsonObjectOfAList("JsonPersonne.json", "Nom d'utilisateur", solde.get("Locataire").toString());
                System.out.println("0 - /// "+unite.get("Adresse").toString()+" - "+
                dateDebut.get("Annee").toString()+"/"+dateDebut.get("Mois").toString()+"/"+dateDebut.get("Jour").toString()+
                "\n/// Locataire: "+locataire.get("Prenom").toString()+" "+locataire.get("Nom").toString());
                System.out.println("- Total: "+solde.get("Total")+"$"+
                "\n- Payé: "+solde.get("Payer")+"$\n--------------------\n");
                indexList.add(String.valueOf(compte));
                baiIdList.add(bail.get("Identifiant").toString());
                compte++;
            }
        }
        String[] stringArray1 = new String[indexList.size()];
        stringArray1 = indexList.toArray(stringArray1);
        System.out.println("Veuillez entrer le numéro du solde que vous voulez ajouter un paiement ou r pour ne pas faire de paiement.");
        String reponse = Interface.takeValidAnswer(stringArray1);
        String idBail = baiIdList.get(Integer.valueOf(reponse));
        JSONObject solde = JsonManager.getJsonObjectOfAList("JsonSolde.json", "Identifiant du bail", idBail);
        long total = (long)solde.get("Total");
        long payer = (long)solde.get("Payer");
        long paiement;
        while(true){
            System.out.println("Veuillez entrer le montant du paiement.");
            paiement = Interface.takePositiveInteger();
            if(paiement <= (total-payer)){
                break;
            }
            System.out.println("ERREUR: Le montant entré est trop gros.");
        }
        //modifie payer
        payer = payer+paiement;
        JsonManager.modifyIntArgumentOfList("JsonSolde.json", "Identifiant du bail", idBail, "Payer", payer);
        //regarde si fini de payer
        if(payer == total){
            JsonManager.modifyBoolArgumentOfList("JsonSolde.json", "Identifiant du bail", idBail, "Fini de payer", true);
        }
        //ajoute le paiement à la liste de paiement
        JSONArray paiements = (JSONArray)solde.get("Paiements");
        JSONObject now = TimeManager.getPresentTime();
        JSONObject newPaiement = new JSONObject();
        newPaiement.put("Montant",paiement);
        newPaiement.put("Date",now);
        paiements.add(newPaiement);
        JsonManager.modifyJArrayArgumentOfList("JsonSolde.json", "Identifiant du bail", idBail, "Paiements", paiements);
    }
}
