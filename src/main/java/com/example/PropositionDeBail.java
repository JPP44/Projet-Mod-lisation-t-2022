package com.example;

import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PropositionDeBail {
    public static void createPropositionDeBail(String idUnite, String periode, long nbPeriode, long loyer, JSONObject dateDeDebut, JSONObject dateDeFin, Boolean renouvelable, String nomProprietaire, JSONArray suplements){
        JSONObject propDeBail = new JSONObject();
        propDeBail.put("Identifiant de l'unite", idUnite);
        propDeBail.put("Periode", periode);
        propDeBail.put("Nombre de periode", nbPeriode);
        propDeBail.put("Loyer", loyer);
        propDeBail.put("Date de debut", dateDeDebut);
        propDeBail.put("Date de fin", dateDeFin);
        propDeBail.put("Renouvelable", renouvelable);
        propDeBail.put("Proprietaire", nomProprietaire);
        propDeBail.put("Supplements", suplements);
        propDeBail.put("Visible", true);
        propDeBail.put("Est pour un renouvlement", false);

        JSONObject unite = JsonManager.getJsonObjectOfAList("JsonUnite.json", "Identifiant", idUnite);
        String id = nomProprietaire + unite.get("Nombre de proposition de bail creer").toString();
        propDeBail.put("Identifiant", id);
        long nouveauCompteur = (long)unite.get("Nombre de proposition de bail creer")+1;
        JsonManager.modifyIntArgumentOfList("JsonUnite.json", "Identifiant", idUnite, "Nombre de proposition de bail creer", nouveauCompteur);
        JsonManager.modifyBoolArgumentOfList("JsonUnite.json", "Identifiant", idUnite, "Possede une proposition de bail", true);

        JsonManager.addObjectToJsonList(propDeBail, "JsonPropositionDeBail.json");
    }


    public static void modifierPropositionDeBail(String nomProprietaire){
        System.out.println("Voici les propositions que vous pouvez modiffier:");
        JSONArray propositions = JsonManager.getArrayOfJsonFile("JsonPropositionDeBail.json");
        ArrayList<String> listeIdentifiant = new ArrayList<String>();
        ArrayList<String> listeIndexe = new ArrayList<String>(); 
        listeIndexe.add("r");

        int compte = 0;
        System.out.println("X - ///// Adresse /// Date de début /// Durée d'une période  /// Nombre de période /// Loyer /////");
        for (Object object : propositions) {
            JSONObject proposition = (JSONObject)object;
            JSONObject date = (JSONObject)proposition.get("Date de debut");
            JSONObject unite = JsonManager.getJsonObjectOfAList("JsonUnite.json", "Identifiant", proposition.get("Identifiant de l'unite").toString());
            if(proposition.get("Proprietaire").equals(nomProprietaire)&&!(Boolean)proposition.get("Est pour un renouvlement")){
                listeIndexe.add(String.valueOf(compte));
                listeIdentifiant.add(proposition.get("Identifiant").toString());
                System.out.println(compte + " - ///// "+unite.get("Adresse")+
                " /// Date: "+date.get("Annee")+"/"+date.get("Mois")+"/"+date.get("Jour")+" "+
                date.get("Heure")+":"+date.get("Minute")+":"+date.get("Seconde")+
                " /// "+proposition.get("Periode").toString()+
                " /// "+proposition.get("Nombre de periode").toString()+
                " /// "+proposition.get("Loyer").toString()+"$ /////");
                compte++;
            }
        }
        //2 - Demande quelle proposition il veut modifier ou s'il veut retourner au menu unité
        String[] stringArray1 = new String[listeIndexe.size()];
        stringArray1 = listeIndexe.toArray(stringArray1);
        System.out.println("\nVeuillez entrer le numéro de la poposition que vous désirez modiffier ou entrer r pour retourner au menu des unités.");
        String reponse1 = Interface.takeValidAnswer(stringArray1);
        if(reponse1.equals("r")){
            return;
        }
        String nomProposition = listeIdentifiant.get(Integer.valueOf(reponse1));
        JSONObject proposition = JsonManager.getJsonObjectOfAList("JsonPropositionDeBail.json", "Identifiant", nomProposition);
        ArrayList<String> stringArrayList1 = new ArrayList<String>();
        //3 - Demande quel élément de l'unité il veut changer et s'il veut retourner au menu unité
        System.out.println("Quel élément de la proposition voulez vous changer?");
        System.out.println("- La période = p");
        stringArrayList1.add("p");
        System.out.println("- Le nombre de périodes = n");
        stringArrayList1.add("n");
        System.out.println("- Si le bail est renouvelable = r");
        stringArrayList1.add("r");
        System.out.println("- Ajouter un supplément = a");
        stringArrayList1.add("a");
        System.out.println("- Supprimer un supplément = s");
        stringArrayList1.add("s");
        System.out.println("- Changer la visibilité = v");
        stringArrayList1.add("v");
        String[] stringArray2 = new String[stringArrayList1.size()];
        stringArray1 = stringArrayList1.toArray(stringArray2);
        String reponse3 = Interface.takeValidAnswer(stringArray2);
        //4 - Input la nouvelle valeur et met dans une boucle jusqu'à avoir une valeur satisfaisante
        

        System.out.println();

        if(reponse3.equals("p")){
            System.out.println("Entrez la nouvelle valeur de la période (o = Mois, j = Jour, h = Heure, m = Minute, s = Secondes)");
            String[] stringArray3 = {"o","j","h","m","s"};
            reponse3 = Interface.takeValidAnswer(stringArray3);
            String periode;
            if(reponse3.equals("o")){periode = "Mois";}
            else if(reponse3.equals("j")){periode = "Jour";}
            else if(reponse3.equals("h")){periode = "Heure";}
            else if(reponse3.equals("m")){periode = "Minute";}
            else{periode = "Seconde";}
            JsonManager.modifyArgumentOfList("JsonPropositionDeBail.json", "Identifiant", nomProposition, "Periode", periode);
        }
        else if(reponse3.equals("n")){
            System.out.println("Entrez la nouvelle valeur du nombre de périodes.");
            long reponse = Interface.takePositiveInteger();
            JsonManager.modifyIntArgumentOfList("JsonPropositionDeBail.json", "Identifiant", nomProposition, "Nombre de periode", reponse);
        }
        else if(reponse3.equals("r")){
            System.out.println("Entrez si vous voulez que le bail soit renouvelable. (y = oui, n = non)");
            String[] stringArray3 = {"y","n"};
            reponse3 = Interface.takeValidAnswer(stringArray3);
            Boolean renouvelable;
            if(reponse3.equals("y")){renouvelable =true;}
            else{renouvelable=false;}
            JsonManager.modifyBoolArgumentOfList("JsonPropositionDeBail.json", "Identifiant", nomProposition, "Renouvelable", renouvelable);
        }
        else if(reponse3.equals("a")){
            Scanner scanner = new Scanner(System.in);
            JSONObject supplement = new JSONObject();
            System.out.print("Entrez le nom du nouveau supplément: ");
            String nomSuplement = scanner.nextLine();
            System.out.print("- Entrez une description du suplément: ");
            String descriptionSuplement =  scanner.nextLine();
            System.out.println("- Entrez coût du supplément par période en $ canadien (seulement des entiers son acceptés).");
            long cout = Interface.takePositiveInteger();
            supplement.put("Nom", nomSuplement);
            supplement.put("Description", descriptionSuplement);
            supplement.put("Cout", cout);
            JSONArray supplements = (JSONArray)proposition.get("Supplements");
            supplements.add(supplement);
            JsonManager.modifyJArrayArgumentOfList("JsonPropositionDeBail.json", "Identifiant", nomProposition, "Supplements", supplements);
        }
        else if(reponse3.equals("s")){
            JSONArray supplements = (JSONArray)proposition.get("Supplements");
            System.out.println("Lequel de ces suppléments voulez vous supprimer? (Entrez l'indexe associé au supplément à supprimer)");
            ArrayList<String> indexSupp = new ArrayList<String>();
            int compte2 = 0;
            for (Object object : supplements) {
                JSONObject supplement = (JSONObject)object;
                System.out.println(compte2+" - "+supplement.get("Nom")+": "+supplement.get("Description")+". "+supplement.get("Cout")+"$.");
                indexSupp.add(String.valueOf(compte2));
                compte2++;
            }
            String[] stringArray3 = new String[indexSupp.size()];
            stringArray1 = indexSupp.toArray(stringArray2);
            reponse3 = Interface.takeValidAnswer(stringArray1);
            int index = Integer.valueOf(reponse3);
            supplements.remove(index);
            System.out.println(index);
            System.out.println(supplements);
            JsonManager.modifyJArrayArgumentOfList("JsonPropositionDeBail.json", "Identifiant", nomProposition, "Supplements", supplements);
        }
        else{
            System.out.println("Entrez si vous voulez que la proposition de bail soit visible par les locataire. (y = oui, n = non)");
            String[] stringArray3 = {"y","n"};
            reponse3 = Interface.takeValidAnswer(stringArray3);
            Boolean visible;
            if(reponse3.equals("y")){visible =true;}
            else{visible=false;}
            JsonManager.modifyBoolArgumentOfList("JsonPropositionDeBail.json", "Identifiant", nomProposition, "Visible", visible);
        }
    } 
}
