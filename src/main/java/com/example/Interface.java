package com.example;

import java.util.ArrayList;
import java.util.Scanner;

import java.time.LocalDateTime;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Interface{

    // Main pour pouvoir mettre en marche le système en entier
    public static void main( String[] args ) throws ParseException {
        startSystem();
    }

    // Fonction pour mettre en marche le système 
    private static void startSystem() throws ParseException{
        System.out.println();
        System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        System.out.println("////  0      000   0000  000  00000 00000  000  0   0     00000     0   0 00000 0   0 0   0 00000 00000  000  00  ////");
        System.out.println("////  0     0   0 0     0   0   0     0   0   0 00  0     0         00 00   0   00  0 0   0   0   0     0     00  ////");
        System.out.println("////  0     0   0 0     00000   0     0   0   0 0 0 0     00000     0 0 0   0   0 0 0 0   0   0   00000  00   00  ////");
        System.out.println("////  0     0   0 0     0   0   0     0   0   0 0  00         0     0   0   0   0  00 0   0   0   0        0      ////");
        System.out.println("////  00000  000   0000 0   0   0   00000  000  0   0     0000      0   0 00000 0   0  000    0   00000 000   00  ////");
        System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n\n\n");
        
        System.out.println("Bienvenu sur le logiciel de location de propriété \"Location 5 minutes!\"\nVous pouvez quitter le logiciel à tout moment avec la comande Ctrl+c.\n\n");
        System.out.println("Voulez-vous vous conecter ou vous inscrire? (c = connecter, i = inscrire)");
        String[] stringArray0 = {"c","i"};
        String reponse0 = takeValidAnswer(stringArray0);

        String[] stringArray1 = {"l","p"};
        if(reponse0.equals("c")){
            System.out.println("Voulez-vous vous connecter comme un locataire ou comme un propriétaire? (l = locataire, p = propriétire)");
            String reponse1 = takeValidAnswer(stringArray1);
            if(reponse1.equals("l")){
                seConnecterLocataire();
            }else{
                seConnecterProprietaire();
            }
        }
        else{
            System.out.println("Voulez-vous vous incrire comme un locataire ou comme un propriétaire? (l = locataire, p = propriétire)");
            String reponse1 = takeValidAnswer(stringArray1);
            if(reponse1.equals("l")){
                sInscrireLocataire();
            }else{
                sInscrireProprietaire();
            }
        }

    }

    // Permet de mettre à jour les différents fichiers Json lorsque nécessaire
    private static void miseAJour(){
        JSONObject now = TimeManager.getPresentTime();

        // Scan la liste de bail pour voir ce qui est à mettre à jour
        JSONArray listBails = JsonManager.getArrayOfJsonFile("JsonBail.json");
        for (Object object : listBails) {
            JSONObject bail = (JSONObject)object;
            JSONObject dateDeFin = (JSONObject)bail.get("Date de fin");
            if(TimeManager.getDate1BeforeDate2(dateDeFin, now)&&!(Boolean)bail.get("Termine")){
                JSONObject locataire =JsonManager.getJsonObjectOfAList("JsonLocataire.json", "Nom d'utilisateur", bail.get("Locataire").toString());
                JSONObject  unite = JsonManager.getJsonObjectOfAList("JsonUnite.json", "Nom d'utilisateur du proprietaire", bail.get("Proprietaire").toString());
                JsonManager.modifyBoolArgumentOfList("JsonBail.json", "Identifiant", bail.get("Identifiant").toString(), "Termine", true);
                String etat = unite.get("Etat").toString();
                String newEtat;
                if(etat.equals("Loue")){newEtat="Libre";}
                else{newEtat="Loue";}
                JsonManager.modifyArgumentOfList("JsonUnite.json", "Identifiant", unite.get("Identifiant").toString(), "Etat", newEtat);
                if((Boolean)locataire.get("A renouvele le bail")){
                    JSONObject proposition = JsonManager.getJsonObjectOfAList("JsonPropositionDeBail.json", "Identifiant de l'unite", unite.get("Identifiant").toString());
                    JsonManager.modifyBoolArgumentOfList("JsonUnite.json", "Identifiant", unite.get("Identifiant").toString(), "Possede une proposition de bail", false);
                    JsonManager.modifyBoolArgumentOfList("JsonLocataire.json", "Nom d'utilisateur", locataire.get("Nom d'utilisateur").toString(), "A renouvele le bail", false);
                    JsonManager.removeObjectToJsonList("Identifiant", proposition.get("Identifiant").toString(),"JsonPropositionDeBail");
                    Bail.addBailToJson(proposition, bail.get("Nom assureur").toString(), bail.get("Numero assurance").toString(), bail.get("Locataire").toString());
                }
                else{
                    JsonManager.modifyBoolArgumentOfList("JsonLocataire.json", "Nom d'utilisateur", locataire.get("Nom d'utilisateur").toString(), "Cherche location", true);
                }

            }
        }

        // Scan la liste de proposition de bail pour voir ce qui est à mettre à jour
        JSONArray listPropositions = JsonManager.getArrayOfJsonFile("JsonPropositionDeBail.json");
        for (Object object : listPropositions) {
            JSONObject proposition = (JSONObject)object;
            JSONObject dateDeDebut = (JSONObject)proposition.get("Date de debut");
            if(TimeManager.getDate1BeforeDate2(dateDeDebut, now)&&!(Boolean)proposition.get("Est pour un renouvlement")){
                JsonManager.modifyBoolArgumentOfList("JsonUnite.json", "Identifiant", proposition.get("Identifiant de l'unite").toString(), "Possede une proposition de bail", false);
                JsonManager.removeObjectToJsonList("Identifiant", proposition.get("Identifiant").toString(), "JsonPropositionDeBail.json");
            }
        }
    }

    // Permet de seulement prendre comme réponse valide une des string de la string[] entré en argument
    public static String takeValidAnswer(String[] arguments){
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print("Réponse: ");
            String reponse = scanner.next();

            for (String string : arguments) {
                if(reponse.equals(string)){
                    return reponse;
                }
            }
            System.out.println("ERREUR: Réponse invalide. Svp entrer une réponse valide.");
        }
    }

    // Permet de seulement prendre comme réponse valide un entier positif
    public static long takePositiveInteger(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            try{
                System.out.print("Réponse: ");
                long reponse = Integer.valueOf(scanner.next());
                if(reponse >= 0){
                    return reponse;
                }
                System.out.println("ERREUR: Réponse invalide. Svp entrer une réponse valide.");
            }catch(Exception e){
                System.out.println("ERREUR: Réponse invalide. Svp entrer une réponse valide.");
            }
        } 
    }


    ////////////////////////////////////////
    /////// Fonctions pour locataire ///////
    ////////////////////////////////////////

    // Permet de se connceter en tant que locataire
    private static void seConnecterLocataire() throws ParseException{
        System.out.println("\n\n////// Connexion - Locataire //////");
        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.print("Veuillez entrer votre nom d'utilisayeur: ");
            String nomDUtilisateur = scanner.next();
            System.out.print("Veuillez entrer votre mot de passe: ");
            String motDePasse = scanner.next();

            if(Locataire.getLocataire(nomDUtilisateur) != null){
                JSONObject personne = Personne.getPersonne(nomDUtilisateur);
                String motDePasseSystem = (String)personne.get("Mot de passe");

                if(motDePasse.equals(motDePasseSystem)){
                    System.out.println("La connexion s'est fait avec succès!");
                    System.out.println("Vous ètes maintenant redirigé vers le menu du logiel en tant que locataire.\n");
                    System.out.print("Appuyez enter pour continuer.");
                    scanner.nextLine();
                    scanner.nextLine();
                    menuLocataire(nomDUtilisateur);
                    return;
                }
                System.out.println("ERREUR: Mot de passe invalide. Svp entrer un mot de passe valide.");
            }
            else{
                System.out.println("ERREUR: Nom d'utilisateur invalide. Svp entrer un nom d'utilisateur valide.");
            }
            System.out.println("--------------------");
        }
    }

    // Permet de s'inscrire en tant que locataire
    private static void sInscrireLocataire() throws ParseException{
        System.out.println("\n\n////// Inscription - Locataire //////");
        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.print("Veuillez entrer votre nom d'utilisayeur: ");
            String nomDUtilisateur = scanner.next();
            System.out.print("Veuillez entrer votre mot de passe: ");
            String motDePasse = scanner.next();
            System.out.print("Veuillez entrer votre prenom: ");
            String prenom = scanner.next();
            System.out.print("Veuillez entrer votre nom: ");
            String nom = scanner.next();
            System.out.print("Veuillez entrer votre cote de crédit: ");
            String coteDeCredit = scanner.next();
            System.out.println("Ètes-vous satisfait avec ces valeurs entrées pour créer votre compte locataire? (y = oui, n = non)");
            String[] stringArray = {"y","n"};
            String reponse = takeValidAnswer(stringArray);
            if(reponse.equals("y")){
                Locataire.addLocataireToJson(nomDUtilisateur, motDePasse, prenom, nom, coteDeCredit);
                System.out.println("L'inscrption s'est fait avec succès!");
                System.out.println("Vous ètes maintenant redirigé vers la connexion au logiel en tant que locataire.\n");
                System.out.print("Appuyez enter pour continuer.");
                scanner.nextLine();
                scanner.nextLine();
                seConnecterLocataire();
                return;
            }else{System.out.println("--------------------");}

        }
    }
    
    // Menu qui permet d'avoir accès à toutes les fonctions d'un locataire
    private static void menuLocataire(String locataireName) throws ParseException{
        System.out.println("\n\n\n////////////////////////////////////////");
        System.out.println("/////////// Menu - Locataire ///////////");
        System.out.println("////////////////////////////////////////");
        while(true){
            System.out.println("Veuillez sélectionner la page ou l'action que vous voulez consulter ou effectuer.");
            System.out.println("- Rechercher unités = u");
            System.out.println("- Paiment = p");
            System.out.println("- Consulter bail = b");
            System.out.println("- Compte = c");
            System.out.println("- Quitter la session = s");
            System.out.println("- Quitter le logiciel = q");

            String[] stringArray0 = {"u","p","b","c","s","q"};
            String reponse0 = takeValidAnswer(stringArray0);
            if(reponse0.equals("u")){
                rechercheUniteLocataire(locataireName);
            }
            else if(reponse0.equals("p")){
                paimentLocataire(locataireName);
            }
            else if(reponse0.equals("b")){
                bailLocataire(locataireName);
            }
            else if(reponse0.equals("c")){
                compteLocataire(locataireName);
                return;
            }
            else if(reponse0.equals("s")){
                System.out.println("Ètes-vous sûr de vouloir quitter la session? (y = oui, n = non)");
                String[] stringArray1 = {"y","n"};
                String reponse1 = takeValidAnswer(stringArray1);
                if(reponse1.equals("y")){
                    startSystem();
                    return;
                }
            }
            else{
                System.out.println("Ètes-vous sûr de vouloir quitter le logiciel? (y = oui, n = non)");
                String[] stringArray1 = {"y","n"};
                String reponse1 = takeValidAnswer(stringArray1);
                if(reponse1.equals("y")){
                    System.out.println("\nBonne fin de journée!");
                    System.out.println("\n\n------ Fermeture du logiciel ------");
                    System.exit(0);
                }
            }
            System.out.println();
        }
    }

    // Permet de faire une recherche parmi les unités et de conclure un bail
    private static void rechercheUniteLocataire(String nomLocataire){
        miseAJour();
        //1 - demande si veut juste voir toutes les unitées, toutes les unitées avec proposition de bail ou faire une recherche avancée.
        System.out.println("\nVoulez vous voir toutes les unités = t, seulements celles possédent une proposition de bail = p ou faire une recherche avancée = r.");
        String[] stringArray0 = {"t","p","r"};
        String reponse = takeValidAnswer(stringArray0);
        JSONArray jarray = JsonManager.getArrayOfJsonFile("JsonUnite.json");
        ArrayList<String> unitesAvecProposition = new ArrayList<String>();
        ArrayList<String> stringArrayListUniteNames = new ArrayList<String>(); 

        int compte = 0;
         
        //2 - Affiche les unités selon le choix en 1.
        if(reponse.equals("t")){
            System.out.println("\nVoici les unités:\n");
            System.out.println("X - ///// Type /// Aire(m2) /// Condition /// État /// Nom propriétaire /// Possède une proposition de bail /// Adresse /////");
            for (Object object : jarray) {
                JSONObject unite = (JSONObject)object;
                JSONObject proprietaire = JsonManager.getJsonObjectOfAList("JsonPersonne.json", "Nom d'utilisateur", unite.get("Nom d'utilisateur du proprietaire").toString());
                
                stringArrayListUniteNames.add((String)unite.get("Identifiant"));

                System.out.print(compte + " - ///// "+unite.get("Type").toString()+
                " /// "+unite.get("Aire").toString()+
                " /// "+unite.get("Condition").toString()+
                " /// "+unite.get("Etat").toString()+
                " /// "+proprietaire.get("Prenom").toString()+" "+proprietaire.get("Nom").toString());
                if((Boolean)unite.get("Possede une proposition de bail")){
                    JSONObject proposition = JsonManager.getJsonObjectOfAList("JsonPropositionDeBail.json", "Identifiant de l'unite", unite.get("Identifiant").toString());
                    if((Boolean)proposition.get("Visible")){
                        unitesAvecProposition.add(String.valueOf(compte));
                        System.out.print(" /// true");
                    }else{System.out.print(" /// false");}
                }
                System.out.println(" /// "+unite.get("Adresse").toString()+" /////");
                
                compte++;
            }
        }
        else if(reponse.equals("p")){
            System.out.println("\nVoici les unités:\n");
            System.out.println("X - ///// Type /// Aire(m2) /// Condition /// État /// Nom propriétaire /// Adresse /////");
            for (Object object : jarray) {
                JSONObject unite = (JSONObject)object;
                JSONObject proprietaire = JsonManager.getJsonObjectOfAList("JsonPersonne.json", "Nom d'utilisateur", unite.get("Nom d'utilisateur du proprietaire").toString());
                if((Boolean)unite.get("Possede une proposition de bail")){
                    JSONObject proposition = JsonManager.getJsonObjectOfAList("JsonPropositionDeBail.json", "Identifiant de l'unite", unite.get("Identifiant").toString());
                    if((Boolean)proposition.get("Visible")){
                        stringArrayListUniteNames.add((String)unite.get("Identifiant").toString());
                        unitesAvecProposition.add(String.valueOf(compte));
                        System.out.println(compte + " - ///// "+unite.get("Type").toString()+
                        " /// "+unite.get("Aire").toString()+
                        " /// "+unite.get("Condition").toString()+
                        " /// "+unite.get("Etat").toString()+
                        " /// "+proprietaire.get("Prenom").toString()+" "+proprietaire.get("Nom").toString()+
                        " /// "+unite.get("Adresse").toString()+" /////");
                        compte++;
                    }
                }
            }
        }
        else{
            System.out.println("\nVoulez-vous voir les unités de logement = l, de surfaces ouvertes commerciales = c, ou de magasin = m?");
            String[] stringArray1 = {"l","c","m"};
            String reponse2 = takeValidAnswer(stringArray1);
            String[] stringArray2 = {"a","b","c","r"};
            int[] reponse3 = {0,0,0};
            while(true){
                String temp = "";
                System.out.println("\nVoulez-vous ajouter d'autres critères de recherche?");
                if(reponse3[0] == 0){
                    System.out.println("Aire de l'untié = a");
                }
                if(reponse3[1] == 0){
                    System.out.println("Nombre de chambre de bain de l'unité = b");
                }
                if(reponse2.equals("l") && reponse3[2] == 0){
                    System.out.println("Nombre de chambre de l'unité = c");
                }
                System.out.println("Commencer recherche = r");
                temp = takeValidAnswer(stringArray2);
                if(temp.equals("a") && reponse3[0] == 0){
                    System.out.println("Quel aire minimum recherchez-vous?");
                    reponse3[0] = (int)takePositiveInteger();
                }
                else if(temp.equals("b") && reponse3[1] == 0){
                    System.out.println("Combien de chambres de bains recherchez-vous?");
                    reponse3[1] = (int)takePositiveInteger();
                }
                else if(reponse2.equals("l") && temp.equals("c") && reponse3[2] == 0){
                    System.out.println("Combien de chambres recherchez-vous?");
                    reponse3[2] = (int)takePositiveInteger();
                }
                else if(temp.equals("r")){
                    break;
                }
            }
            System.out.println("");
            System.out.println("\nVoici les unités:\n");
            System.out.println("X - ///// Type /// Aire(m2) /// Condition /// État /// Nom propriétaire /// Possède une proposition de bail /// Adresse /////");
            for (Object object : jarray) {
                JSONObject unite = (JSONObject)object;
                JSONObject proprietaire = JsonManager.getJsonObjectOfAList("JsonPersonne.json", "Nom d'utilisateur", unite.get("Nom d'utilisateur du proprietaire").toString());
                stringArrayListUniteNames.add((String)unite.get("Identifiant"));
                if(Integer.parseInt(unite.get("Aire").toString()) >= reponse3[0] && Integer.parseInt(unite.get("Nombre de salle de bain").toString()) >= reponse3[1]){
                    /*if(reponse2.equals("l")){
                        if(unite.get("Type").toString().equals(arg0)){
                            Integer.parseInt(unite.get("Nombre de chambre").toString()) < reponse3[2]){
                                continue;
                            }
                        }
                    }*/
                    System.out.print(compte + " - ///// "+unite.get("Type").toString()+
                    " /// "+unite.get("Aire").toString()+
                    " /// "+unite.get("Condition").toString()+
                    " /// "+unite.get("Etat").toString()+
                    " /// "+proprietaire.get("Prenom").toString()+" "+proprietaire.get("Nom").toString());
                    if((Boolean)unite.get("Possede une proposition de bail")){
                        JSONObject proposition = JsonManager.getJsonObjectOfAList("JsonPropositionDeBail", "Identifiant de l'unite", unite.get("Identifiant").toString());
                        if((Boolean)proposition.get("Visible")){
                            unitesAvecProposition.add(String.valueOf(compte));
                            System.out.print(" /// true");
                        }else{System.out.print(" /// false");}
                    }
                    System.out.println(" /// "+unite.get("Adresse").toString()+" /////");
                    
                    compte++;
                }
            }
        }
        JSONObject locataire = JsonManager.getJsonObjectOfAList("JsonLocataire.json", "Nom d'utilisateur", nomLocataire);
        if(!(boolean)locataire.get("Cherche location")){
            return;
        }
        //3 - Demande s'il veut créer un bail à partir d'une des unités avec prop de bail dans le cas ou il possède pas encore de bail ou s'il veut retoutner au menu.
        System.out.println("Voulez vous créer un bail à partir de l'une de ces unitées possédent une proposition de bail? (y = oui, n= non)");
        String[] stringsArray1 = {"y","n"};
        reponse = takeValidAnswer(stringsArray1);
        String[] arrayUniteAvecPropositions = new String[unitesAvecProposition.size()];
        arrayUniteAvecPropositions = unitesAvecProposition.toArray(arrayUniteAvecPropositions);
        if(reponse.equals("n")){
            return;
        }
        JSONObject propositionDeBail = new JSONObject();
        while(true){
            System.out.println("Quelle unité voulez vous faire un bail avec?");
            reponse = takeValidAnswer(arrayUniteAvecPropositions);
            //Écrit les infos du bail
            System.out.println("Voici les information du bail: ");
            String nomUnite = stringArrayListUniteNames.get(Integer.valueOf(reponse));
            propositionDeBail = JsonManager.getJsonObjectOfAList("JsonPropositionDeBail.json", "Identifiant de l'unite", nomUnite);
            JSONObject dateDeDebut = (JSONObject)propositionDeBail.get("Date de debut");
            System.out.println("- Durée d'une periode: "+propositionDeBail.get("Periode")+
            "\n- Nombre de période: "+propositionDeBail.get("Nombre de periode")+
            "\n- Loyer: "+propositionDeBail.get("Loyer")+"$ par période."+
            "\n- Date du début du bail: "+dateDeDebut.get("Annee")+"/"+dateDeDebut.get("Mois")+"/"+dateDeDebut.get("Jour")+" "+dateDeDebut.get("Heure")+":"+dateDeDebut.get("Minute")+":"+dateDeDebut.get("Seconde"));
            System.out.println("- Suppléments:");
            JSONArray sumplements = (JSONArray)propositionDeBail.get("Supplements");
            for (Object object : sumplements) {
                JSONObject jObject = (JSONObject)object; 
                System.out.println("    * "+jObject.get("Nom").toString()+": "+
                jObject.get("Description").toString()+
                ". Coût: "+jObject.get("Cout").toString()+"$ par période.");
            }

            System.out.println("Voulez vous faire un bail = b, choisir une autre unité = u ou retourner au menu = r");
            String[] stringArray1 = {"b","u","r"};
            reponse =takeValidAnswer(stringArray1);
            if(reponse.equals("b")){
                break;
            }
            else if(reponse.equals("u")){
                System.out.println();
            }
            else{return;}
        }
        //4 - Entre son assurance locataire s'il en possède une.
        System.out.println("Avez vous une assurance locataire? (y = oui, n = non)");
        reponse = takeValidAnswer(stringsArray1);
        Scanner scanner = new Scanner(System.in);
        String nomAssureur = null;
        String numeroReferenceAssureur = null;
        if(reponse.equals("y")){
            System.out.print("Veuillez entrer le nom de l'assureur: ");
            nomAssureur = scanner.nextLine();
            System.out.print("Veuillez entrer le numéro de référence de la police d'assuerance: ");
            numeroReferenceAssureur = scanner.nextLine();
        }
        //5 - Création du bail.
        Bail.addBailToJson(propositionDeBail, nomAssureur, numeroReferenceAssureur, nomLocataire);
    }

    // Permet de voir l'historique des paiments et le total des soldes dus
    private static void paimentLocataire(String nomLocataire){
        Solde.afficherSoldeEtHistoriqueDePaiementLocataire(nomLocataire);;
    }

    // Permet de gerrer les différentes information d'un compte locataire
    private static void compteLocataire(String nomLocataire) throws ParseException{
        System.out.println("\n\n////// Compte - Locataire //////");

        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Veuillez sélectionner l'action que vous voulez effectuer.");
            System.out.println("- Changer de mot de passe = m");
            System.out.println("- Changer la cote de crédit = c");
            System.out.println("- Changer les types d'intérêt de location = i");
            System.out.println("- Retourner au menu principal = r");

            String[] stringArray0 = {"m","c","i","r"};
            String reponse0 = takeValidAnswer(stringArray0);
            
            if(reponse0.equals("m")){
                System.out.print("\nVeuillez entrer le nouveau mot de passe: ");
                String nouveauMDP = scanner.next();
                JsonManager.modifyArgumentOfList("JsonPersonne.json", "Nom d'utilisateur", nomLocataire, "Mot de passe", nouveauMDP);
                System.out.println();
            }
            else if (reponse0.equals("c")){
                System.out.print("\nVeuillez entrer la nouvlle cote de crédit: ");
                String nouvelleCote = scanner.next();
                JsonManager.modifyArgumentOfList("JsonLocataire.json", "Nom d'utilisateur", nomLocataire, "Cote de credit", nouvelleCote);
                System.out.println();
            }

            // Changer les intérêts 
            else if (reponse0.equals("i")){
                JSONObject object = JsonManager.getJsonObjectOfAList("JsonLocataire.json", "Nom d'utilisateur", nomLocataire);
                JSONObject interets = (JSONObject)object.get("Interet");
                System.out.print("\nVoici à quoi ressemble vos préférence pour le moment: ");
                System.out.println(interets);

                System.out.println("Voulez vous ajouter ou enlever un type d'intérêt? (a = ajouter, e = enlever)");
                String[] stringArray1 = {"a","e"};
                String reponse1 = takeValidAnswer(stringArray1);

                if(reponse1.equals("a")){
                    System.out.println("\nVoulez-vous ajouter le type commercial = c, résidentiel = r ou autre = a?");
                    String[] stringArray2 = {"c","r","a"};
                    String reponse2 = takeValidAnswer(stringArray2);

                    if(reponse2.equals("c")){interets.put("Commercial", true);}
                    else if (reponse2.equals("r")){interets.put("Residentiel", true);}
                    else{interets.put("Autre", true);}
                }
                else{
                    System.out.println("\nVoulez-vous enlever le type commercial = c, résidentiel = r ou autre = a?");
                    String[] stringArray2 = {"c","r","a"};
                    String reponse2 = takeValidAnswer(stringArray2);

                    if(reponse2.equals("c")){interets.put("Commercial", false);}
                    else if (reponse2.equals("r")){interets.put("Residentiel", false);}
                    else{interets.put("Autre", false);}
                }
                JsonManager.modifyJSONObjectOfAnObject("JsonLocataire.json", "Nom d'utilisateur", nomLocataire, "Interet", interets.toString());
            }
            else{
                menuLocataire(nomLocataire);
                System.out.println("--------------------");
                return;
            }
            System.out.println("\n--------------------\n");
        }
    }

    // Permet de visualiser le bail actuel et de renouveler le bail si possible
    private static void bailLocataire(String nomLocataire){
        miseAJour();
        Scanner scanner = new Scanner(System.in);
        JSONObject locataire = JsonManager.getJsonObjectOfAList("JsonLocataire.json", "Nom d'utilisateur", nomLocataire);
        if(!(Boolean)locataire.get("Cherche location")){
            JSONObject bail = new JSONObject();
            JSONArray bails = JsonManager.getArrayOfJsonFile("JsonBail.json");
            for (Object object : bails) {
                JSONObject jObject = (JSONObject)object;
                if(jObject.get("Locataire").toString().equals(nomLocataire)&&!(Boolean)jObject.get("Termine")){
                    bail = jObject;
                    break;
                }
            }
            JSONObject proprietaire = JsonManager.getJsonObjectOfAList("JsonPersonne.json", "Nom d'utilisateur", bail.get("Proprietaire").toString());
            JSONObject unite = JsonManager.getJsonObjectOfAList("JsonUnite.json", "Identifiant", bail.get("Identifiant de l'unite").toString());
            JSONObject dateDeDebut = (JSONObject)bail.get("Date de debut");
            JSONObject dateDeFin = (JSONObject)bail.get("Date de fin");
            JSONObject solde = JsonManager.getJsonObjectOfAList("JsonSolde.json", "Identifiant du bail", bail.get("Identifiant").toString());
            System.out.println("\nVoici les information sur votre bail:");
            System.out.println("- Adresse: "+unite.get("Adresse").toString()+ 
            "\n- Propriétaire: "+proprietaire.get("Prenom").toString()+" "+proprietaire.get("Nom").toString()+
            "\n- Date de début: "+dateDeDebut.get("Annee")+"/"+dateDeDebut.get("Mois")+"/"+dateDeDebut.get("Jour")+" "+
            dateDeDebut.get("Heure")+":"+dateDeDebut.get("Minute")+":"+dateDeDebut.get("Seconde")+
            "\n- Date de fin: "+dateDeFin.get("Annee")+"/"+dateDeFin.get("Mois")+"/"+dateDeFin.get("Jour")+" "+
            dateDeFin.get("Heure")+":"+dateDeFin.get("Minute")+":"+dateDeFin.get("Seconde")+
            "\n- Nom de l'assureur: "+bail.get("Nom assureur")+
            "\n- Numéro de l'assurance: "+bail.get("Numero d'assurance")+
            "\n- Renouvelable: "+bail.get("Renouvelable")+
            "\n- Renouvelé: "+locataire.get("A renouvele le bail"));
            JSONArray supplements = (JSONArray)bail.get("Supplements");
            if(!supplements.isEmpty()){
                System.out.println("- Supléments:");
                for (Object object : supplements) {
                    JSONObject supplement = (JSONObject)object;
                    System.out.println("    * "+supplement.get("Nom")+": "+supplement.get("Description"));
                } 
            }
            System.out.println("- Coût total: "+solde.get("Total")+
            "\n- Payé: "+solde.get("Payer"));
            
            if((Boolean)bail.get("Renouvelable")&&!(Boolean)locataire.get("A renouvele le bail")){
                System.out.print("\nAppuyez sur la touche entrer pour continuer.");
                scanner.nextLine();
                JSONObject proposition = JsonManager.getJsonObjectOfAList("JsonPropositionDeBail.json", "Identifiant de l'unite", unite.get("Identifiant").toString());
                JSONObject dateDeDebutp = (JSONObject)proposition.get("Date de debut");
                JSONObject dateDeFinp = (JSONObject)proposition.get("Date de fin");
                System.out.println("\nVoulez-vous renouveler ce bail? (y = oui, n = non) Voici les informations de la proposition de bail pour le renouvellement:");
                System.out.println("- Période: "+proposition.get("Periode")+ 
                "\n- Nombre de périodes: "+proposition.get("Nombre de periode")+
                "\n- Loyer par période: "+proposition.get("Loyer")+
                "\n- Nombre de périodes: "+proposition.get("Nombre de periode")+
                "\n- Date de début: "+dateDeDebutp.get("Annee")+"/"+dateDeDebutp.get("Mois")+"/"+dateDeDebutp.get("Jour")+" "+
                dateDeDebutp.get("Heure")+":"+dateDeDebutp.get("Minute")+":"+dateDeDebutp.get("Seconde")+
                "\n- Date de fin: "+dateDeFinp.get("Annee")+"/"+dateDeFinp.get("Mois")+"/"+dateDeFinp.get("Jour")+" "+
                dateDeFinp.get("Heure")+":"+dateDeFinp.get("Minute")+":"+dateDeFinp.get("Seconde")+
                "\nRenouvelalble: "+proposition.get("Renouvelalble"));
                JSONArray supplementsp = (JSONArray)proposition.get("Supplements");
                if(!supplementsp.isEmpty()){
                    System.out.println("- Supléments:");
                    for (Object object : supplementsp) {
                        JSONObject supplement = (JSONObject)object;
                        System.out.println("    * "+supplement.get("Nom")+": "+supplement.get("Description")+". Prix: "+supplement.get("Cout")+"$.");
                    } 
                }
                System.out.println();
                String[] arrayList0 = {"y","n"};
                String reponse0 = takeValidAnswer(arrayList0);
                if(reponse0.equals("y")){
                    //Changer le paramètre à renouvelé le bail de locataire
                    JsonManager.modifyBoolArgumentOfList("JsonLocataire.json", "Nom d'utilisateur", nomLocataire, "A renouvele le bail", true);
                    //Changer le paramètre de visibilité et Est pour un renouvelement de la proposition de bail
                    JsonManager.modifyBoolArgumentOfList("JsonPropositionDeBail.json", "Identifiant de l'unite", unite.get("Identifiant").toString(), "Visible", false);
                    JsonManager.modifyBoolArgumentOfList("JsonPropositionDeBail.json", "Identifiant de l'unite", unite.get("Identifiant").toString(), "Est pour un renouvelement", true);
                    System.out.println("\nLe bail a été renouvelé avec succès!");
                }
            }

        }
        else{
            System.out.println("Vous n'avez présentement pas de bail.");
        }

        System.out.print("\nAppuyez sur la touche entrer pour retourner au menu.");
        scanner.nextLine();
    }



    ///////////////////////////////////////////
    /////// Fonctions pour proprietaire ///////
    ///////////////////////////////////////////

    // permet de se connecter en tant que propriétaire 
    private static void seConnecterProprietaire() throws ParseException{
        System.out.println("\n\n////// Connexion - Propriétaire //////");
        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.print("Veuillez entrer votre nom d'utilisayeur: ");
            String nomDUtilisateur = scanner.next();
            System.out.print("Veuillez entrer votre mot de passe: ");
            String motDePasse = scanner.next();

            if(Proprietaire.getProprietaire(nomDUtilisateur) != null){
                JSONObject personne = Personne.getPersonne(nomDUtilisateur);
                String motDePasseSystem = (String)personne.get("Mot de passe");

                if(motDePasse.equals(motDePasseSystem)){
                    System.out.println("La connexion s'est fait avec succès!");
                    System.out.println("Vous ètes maintenant redirigé vers le menu du logiel en tant que prpopriétaite.\n");
                    System.out.print("Appuyez enter pour continuer.");
                    scanner.nextLine();
                    scanner.nextLine();
                    menuProprietaire(nomDUtilisateur);
                    return;
                }
                System.out.println("ERREUR: Mot de passe invalide. Svp entrer un mot de passe valide.");
            }
            else{
                System.out.println("ERREUR: Nom d'utilisateur invalide. Svp entrer un nom d'utilisateur valide.");
            }
            System.out.println("--------------------");
        }
    }

    // Permet de s'inscrire en tant que propriétaire
    private static void sInscrireProprietaire() throws ParseException{
        System.out.println("\n\n////// Inscription - Propriétaire //////");
        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.print("Veuillez entrer votre nom d'utilisayeur: ");
            String nomDUtilisateur = scanner.next();
            System.out.print("Veuillez entrer votre mot de passe: ");
            String motDePasse = scanner.next();
            System.out.print("Veuillez entrer votre prenom: ");
            String prenom = scanner.next();
            System.out.print("Veuillez entrer votre nom: ");
            String nom = scanner.next();
            System.out.println("Ètes-vous satisfait avec ces valeurs entrées pour créer votre compte propriétaire? (y = oui, n = non)");
            String[] stringArray = {"y","n"};
            String reponse = takeValidAnswer(stringArray);
            if(reponse.equals("y")){
                Proprietaire.addProprietaireToJson(nomDUtilisateur, motDePasse, prenom, nom);
                System.out.println("L'inscrption s'est fait avec succès!");
                System.out.println("Vous ètes maintenant redirigé vers la connexion au logiel en tant que prpopriétaite.\n");
                System.out.print("Appuyez enter pour continuer.");
                scanner.nextLine();
                scanner.nextLine();
                seConnecterProprietaire();
                return;
            }else{System.out.println("--------------------");}

        }
    }

    // Menu pour accéder à toutes les foonctionnalité disponible pour un propriétaire
    private static void menuProprietaire(String nomProprietaire) throws ParseException{
        System.out.println("\n\n\n/////////////////////////////////////////");
        System.out.println("////////// Menu - Propriétaire //////////");
        System.out.println("/////////////////////////////////////////");
        while(true){
            System.out.println("Veuillez sélectionner la page ou l'action que vous voulez consulter ou effectuer.");
            System.out.println("- Paiments = p");
            System.out.println("- Unités et baux = u");
            System.out.println("- Compte = c");
            System.out.println("- Quitter la session = s");
            System.out.println("- Quitter le logiciel = q");

            String[] stringArray0 = {"p","u","c","s","q"};
            String reponse0 = takeValidAnswer(stringArray0);
            if(reponse0.equals("p")){
                paimentProprietaire(nomProprietaire);
            }
            else if(reponse0.equals("u")){
                uniteProprietaire(nomProprietaire);
                return;
            }
            else if(reponse0.equals("c")){
                compteProprietaire(nomProprietaire);
                return;
            }
            else if(reponse0.equals("r")){
                System.out.println("Ètes-vous sûr de vouloir quitter la session? (y = oui, n = non)");
                String[] stringArray1 = {"y","n"};
                String reponse1 = takeValidAnswer(stringArray1);
                if(reponse1.equals("y")){
                    startSystem();
                    return;
                }
            }
            else{
                System.out.println("Ètes-vous sûr de vouloir quitter le logiciel? (y = oui, n = non)");
                String[] stringArray1 = {"y","n"};
                String reponse1 = takeValidAnswer(stringArray1);
                if(reponse1.equals("y")){
                    System.out.println("\nBonne fin de journée!");
                    System.out.println("\n\n------ Fermeture du logiciel ------");
                    System.exit(0);
                }
            }
            System.out.println();
        }
    }

    // Permetde modifier les information du compte d'un propriétaire
    private static void compteProprietaire(String nomProprietaire) throws ParseException{
        System.out.println("\n\n////// Compte - Propriétaire //////");

        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Veuillez sélectionner l'action que vous voulez effectuer.");
            System.out.println("- Changer de mot de passe = m");
            System.out.println("- Retourner au menu principal = r");

            String[] stringArray0 = {"m","r"};
            String reponse0 = takeValidAnswer(stringArray0);
            
            if(reponse0.equals("m")){
                System.out.print("\nVeuillez entrer le nouveau mot de passe: ");
                String nouveauMDP = scanner.next();
                JsonManager.modifyArgumentOfList("JsonPersonne.json", "Nom d'utilisateur", nomProprietaire, "Mot de passe", nouveauMDP);
                System.out.println();
            }
            
            else{
                menuLocataire(nomProprietaire);
                System.out.println("--------------------");
                return;
            }
            System.out.println("\n--------------------\n");
        }
        
    }

    // Permet de visualiser l'historique des paiements et d'entrer le paiment d'un locataire
    private static void paimentProprietaire(String nomProprietaire){
        System.out.println("\n\n////// Paiements - Propriétaire //////");
        while(true){
            System.out.println("Veuillez sélectionner l'action que vous voulez effectuer.");
            System.out.println("- Afficher l'historique de paiement = h");
            System.out.println("- Saisir le paiement d'un locataire = p");
            System.out.println("- Retourner au menu principal = r");

            String[] stringArray0 = {"h","p","r"};
            String reponse = takeValidAnswer(stringArray0);

            if(reponse.equals("h")){
                Solde.afficherSoldeEtHistoriqueDePaiementProprietaire(nomProprietaire);
            }
            else if(reponse.equals("p")){
                Solde.saisirPaiement(nomProprietaire);
            }
            else{
                return;
            }
        }
    }

    // Permet de faire toutes les oppérations en lien avec les unités et les baux
    private static void uniteProprietaire(String nomProprietaire) throws ParseException{
        System.out.println("\n\n////// Unités et baux - Propriétaire //////");

        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Veuillez sélectionner l'action que vous voulez effectuer.");
            System.out.println("- Créer une nouvelle unité = u");
            System.out.println("- Modifier une unité existante = m");
            System.out.println("- Consulter les listes d'unités = l");
            System.out.println("- Créer une proposition de bail - b");
            System.out.println("- Modifier une proposition de bail - p");
            System.out.println("- Retourner au menu principal = r");

            String[] stringArray0 = {"u","m","l","b","p","r"};
            String reponse0 = takeValidAnswer(stringArray0);
            
            //// Création d'une nouvelle unité
            if(reponse0.equals("u")){
                while(true){
                    System.out.println("\nVeuillez entrer toutes les nouvelles informations en lien avec la nouvelle unité.");
                    System.out.println("- Entrez le type de l'unité (l = logement, s = surface ouverte commerciale, m = magasin)");
                    String[] stringArray1 = {"l","s","m"};
                    String reponse1 = takeValidAnswer(stringArray1);
                    String type;
                    if(reponse1.equals("l")){type = "Logement";}
                    else if(reponse1.equals("s")){type = "Surface ouverte commerciale";}
                    else{type = "Magasin";}
                    System.out.print("- Entrez l'adresse de l'unité: ");
                    String adresse = scanner.nextLine();
                    System.out.print("- Entrez la ville de l'unité: ");
                    String ville = scanner.nextLine();
                    System.out.println("- Entrez l'aire de l'unité (en m2):");
                    long aire = takePositiveInteger();
                    long nbChambre;
                    if(type.equals("Logement")){
                        System.out.println("- Entrez le nombre de chambre(s):");
                        nbChambre = takePositiveInteger();
                    }
                    else{nbChambre = -1;}
                    System.out.println("- Entrez le nombre de salle(s) de bains(s):");
                    long nbSalleDeBain= takePositiveInteger();
                    System.out.print("- Entrez la date de construction de l'unité: ");
                    String date = scanner.next();
                    System.out.println("- Entrez la condition de l'unité (l = louable, r = pas louable pour réparation)");
                    String[] stringArray2 = {"l","r"};
                    String reponse2 = takeValidAnswer(stringArray2);
                    String condition;
                    if(reponse2.equals("l")){condition = "Louable";}
                    else{condition="Pas louable pour reparation";}

                    System.out.println("\nÈtes-vous satisfait avec ces valeurs entrées pour créer cette nouvelle unité? (y = oui, n = non)");
                    String[] stringArray = {"y","n"};
                    String reponse = takeValidAnswer(stringArray);
                    if(reponse.equals("y")){
                        Unite.addUniteToJson(type, adresse, ville, aire, nomProprietaire, nbChambre, nbSalleDeBain, date, condition);
                        System.out.println("L'ajout de l'unnité s'est fait avec succès!");
                        break;
                    }else{System.out.println("--------------------");}
                }
                
            }

            //// Modification d'une unité
            else if(reponse0.equals("m")){
                miseAJour();
                //1 - Montre toute les unités du propriétaire
                System.out.println("\nVoici vos unités:\n");
                JSONArray jarray = JsonManager.getArrayOfJsonFile("JsonUnite.json");
                ArrayList<String> unitesDuProprietaire = new ArrayList<String>();
                ArrayList<String> stringArrayList0 = new ArrayList<String>(); 
                stringArrayList0.add("r");

                int compte = 0;

                System.out.println("Combiens d'unités à la fois voulez-vous voir?");
                long n = takePositiveInteger();
                long itteration = 0; 

                System.out.println("X - ///// Type /// Aire(m2) /// Condition /// Adresse /////");
                for (Object object : jarray) {
                    JSONObject unite = (JSONObject)object;
                    if(unite.get("Nom d'utilisateur du proprietaire").equals(nomProprietaire)){
                        stringArrayList0.add(String.valueOf(compte));
                        unitesDuProprietaire.add(unite.get("Identifiant").toString());
                        System.out.println(compte + " - ///// "+unite.get("Type").toString()+
                        " /// "+unite.get("Aire").toString()+
                        " /// "+unite.get("Condition").toString()+
                        " /// "+unite.get("Adresse").toString()+" /////");

                        itteration++;
                        if(itteration==n){
                            String[] stringArrayYN = {"y","n"};
                            System.out.println("Voulez voir plus d'unités? (y = oui, n = non)");
                            String reponse = takeValidAnswer(stringArrayYN);
                            if(reponse.equals("n")){break;}
                            itteration = 0;
                        }

                        compte++;
                    }
                }
                //2 - Demande quelle unité il veut modifier ou s'il veut retourner au menu unité
                String[] stringArray1 = new String[stringArrayList0.size()];
                stringArray1 = stringArrayList0.toArray(stringArray1);
                System.out.println("\nVeuillez entrer le numéro de l'unité que vous désirez modiffier enterz r pour retourner au menu des unités.");
                String reponse1 = takeValidAnswer(stringArray1);
                if(!reponse1.equals("r")){
                    String nomUnite = unitesDuProprietaire.get(Integer.valueOf(reponse1));
                    JSONObject jUnite = JsonManager.getJsonObjectOfAList("JsonUnite.json", "Identifiant", nomUnite);
                    ArrayList<String> stringArrayList1 = new ArrayList<String>();
                    //3 - Demande quel élément de l'unité il veut changer et s'il veut retourner au menu unité
                    System.out.println("Quel élément de l'unité voulez vous changer?");
                    System.out.println("- L'aire = a");
                    stringArrayList1.add("a");
                    if(jUnite.get("Type").toString().equals("Logement")){
                        System.out.println("- Le nombre de chambre de l'unité = m");
                        stringArrayList1.add("m");
                    }
                    System.out.println("- Le nombre de salle de bain de l'unité = s");
                    stringArrayList1.add("s");
                    System.out.println("- La condition de l'unité = c");
                    stringArrayList1.add("c");
                    String[] stringArray2 = new String[stringArrayList1.size()];
                    stringArray1 = stringArrayList1.toArray(stringArray2);
                    String reponse3 = takeValidAnswer(stringArray2);
                    //4 - Input la nouvelle valeur et met dans une boucle jusqu'à avoir une valeur satisfaisante
                    String elementChange;
                    if(reponse3.equals("a")){elementChange = "Aire";}
                    else if(reponse3.equals("m")){elementChange = "Nombre de chambre";}
                    else if(reponse3.equals("s")){elementChange = "Nombre de salle de bain";}
                    else{elementChange = "Condition";}

                    System.out.println();
                    if(reponse3.equals("c")){
                        System.out.println("Enterz la nouvelle condition de l'unité. (l = louable, r = pas louable pour réparation)");
                        String[] stringArray3 = {"l","r"};
                        String reponse4 = takeValidAnswer(stringArray3);
                        if(reponse4.equals("l")){
                            JsonManager.modifyArgumentOfList("JsonUnite.json", "Identifiant", nomUnite, elementChange, "Louable");
                        }
                        else{
                            JsonManager.modifyArgumentOfList("JsonUnite.json", "Identifiant", nomUnite, elementChange, "Pas louable pour reparation");
                            JSONArray jArray2 = JsonManager.getArrayOfJsonFile("JsonPropositionDeBail.json");
                            for (Object object : jArray2) {
                                JSONObject proposition = (JSONObject)object;
                                if(proposition.get("Proprietaire").equals(nomProprietaire)){
                                    JsonManager.removeObjectToJsonList("Proprietaire", nomProprietaire, "JsonPropositionDeBail.json");
                                    break;
                                }
                            }
                        }
                    }
                    else{
                        System.out.println("Entrez la nouvelle valeur désiré.");
                        Long reponse = takePositiveInteger();
                        JsonManager.modifyIntArgumentOfList("JsonUnite.json", "Identifiant", nomUnite, elementChange, reponse);
                    }
                }
                //5 - Retour au menu
            }

            //// Consulter les listes d'unités 
            else if(reponse0.equals("l")){
                JSONArray bail_jarray = JsonManager.getArrayOfJsonFile("JsonBail.json");
                JSONArray solde_jarray = JsonManager.getArrayOfJsonFile("JsonSolde.json");
                System.out.println("\nVoulez-vous voir la liste de baux à renouvellement prochain = r, de futur vacants = f, ou de collection = c?");
                String[] stringArray1 = {"r","f","c"};
                String reponse1 = takeValidAnswer(stringArray1);
                System.out.println("");
                int compte = 0;

                System.out.println("Combiens d'unités à la fois voulez-vous voir?");
                long n = takePositiveInteger();
                long itteration = 0; 

                if(reponse1.equals("r")){

                    System.out.println("\nVoici les baux à renouvellement prochain:\n");
                    System.out.println("X - ///// Locataire /// Nombre de periodes /////");
                    for(Object object : bail_jarray){
                        JSONObject bail = (JSONObject)object;
                        if((Integer.parseInt(bail.get("Nombre de periode").toString()) <= 6) && Boolean.parseBoolean(bail.get("Renouvelable").toString())){
                            System.out.println(compte + " - ///// " + bail.get("Locataire").toString() + " /// " + bail.get("Nombre de periode") + " /////");
                        }

                        itteration++;
                        if(itteration==n){
                            String[] stringArrayYN = {"y","n"};
                            System.out.println("Voulez voir plus d'unités? (y = oui, n = non)");
                            String reponse = takeValidAnswer(stringArrayYN);
                            if(reponse.equals("n")){break;}
                            itteration = 0;
                        }

                        compte++;
                    }
                }
                else if(reponse1.equals("f")){
                    System.out.println("\nVoici les baux de futur vacants:\n");
                    System.out.println("X - ///// Locataire /// Nombre de periodes /////");
                    for(Object object : bail_jarray){
                        JSONObject bail = (JSONObject)object;
                        if((Integer.parseInt(bail.get("Nombre de periode").toString()) <= 5) && !Boolean.parseBoolean(bail.get("Renouvelable").toString())){
                            System.out.println(compte + " - ///// " + bail.get("Locataire").toString() + " /// " + bail.get("Nombre de periode") + " /////");
                        }

                        itteration++;
                        if(itteration==n){
                            String[] stringArrayYN = {"y","n"};
                            System.out.println("Voulez voir plus d'unités? (y = oui, n = non)");
                            String reponse = takeValidAnswer(stringArrayYN);
                            if(reponse.equals("n")){break;}
                            itteration = 0;
                        }

                        compte++;
                    }
                }
                else if(reponse1.equals("c")){
                    System.out.println("\nVoici les soldes de baux à collecter\n");
                    System.out.println("X - ///// Locataire /// Solde dû /// Unité /////");
                    for(Object object : solde_jarray){
                        JSONObject solde = (JSONObject)object;
                        if((Integer.parseInt(solde.get("Total").toString()) - Integer.parseInt(solde.get("Payer").toString())) >= 2*Integer.parseInt(solde.get("Loyer").toString())){
                            System.out.println(compte + " - ///// " + solde.get("Locataire").toString() + " /// " + (Integer.parseInt(solde.get("Total").toString()) - Integer.parseInt(solde.get("Payer").toString())) + " /// " + solde.get("Identifiant de l'unite").toString() + " /////");
                        }

                        itteration++;
                        if(itteration==n){
                            String[] stringArrayYN = {"y","n"};
                            System.out.println("Voulez voir plus d'unités? (y = oui, n = non)");
                            String reponse = takeValidAnswer(stringArrayYN);
                            if(reponse.equals("n")){break;}
                            itteration = 0;
                        }

                        compte++;
                    }
                }
            }

            //// Créer une propostion de bail
            else if(reponse0.equals("b")){
                miseAJour();
                //1 Affiche les unités qui peuvent avoir une nouvelle porp de bail (pas réservé, pas en construction, n'a pas déjà une prop)
                System.out.println("\nVoici vos unités qui peuvent se voir créer une nouvelle porposition de bail:\n");
                JSONArray jarray = JsonManager.getArrayOfJsonFile("JsonUnite.json");
                ArrayList<String> unitesDuProprietaire = new ArrayList<String>();
                ArrayList<String> stringArrayList0 = new ArrayList<String>(); 
                stringArrayList0.add("r");

                int compte = 0;
                System.out.println("X - ///// Type /// Aire(m2) /// Condition /// /// Nombre de salle de bain /// Adresse /////");
                for (Object object : jarray) {
                    JSONObject unite = (JSONObject)object;
                    if(unite.get("Nom d'utilisateur du proprietaire").equals(nomProprietaire)){
                        if(!(Boolean)unite.get("Possede une proposition de bail") && !unite.get("Etat").equals("Reserve") && unite.get("Condition").toString().equals("Louable")){
                            stringArrayList0.add(String.valueOf(compte));
                            unitesDuProprietaire.add(unite.get("Identifiant").toString());
                            System.out.println(compte + " - ///// "+unite.get("Type").toString()+
                            " /// "+unite.get("Aire").toString()+
                            " /// "+unite.get("Condition").toString()+
                            " /// "+unite.get("Nombre de salle de bain").toString()+
                            " /// "+unite.get("Adresse").toString()+" /////");
                            compte++;
                        }
                    }
                }
                //2 - Demande quelle unité il veut créer une proposition de bail ou s'il veut retourner au menu unité
                String[] stringArray1 = new String[stringArrayList0.size()];
                stringArray1 = stringArrayList0.toArray(stringArray1);
                System.out.println("\nVeuillez entrer le numéro de l'unité que vous désirez créer une proposition de bail. Enterz r pour retourner au menu des unités.");
                String reponse1 = takeValidAnswer(stringArray1);
                if(!reponse1.equals("r")){
                    String nomUnite = unitesDuProprietaire.get(Integer.valueOf(reponse1));
                    JSONObject jUnite = JsonManager.getJsonObjectOfAList("JsonUnite.json", "Identifiant", nomUnite);
                    ArrayList<String> stringArrayList1 = new ArrayList<String>();
                    //3 entre les information sur la proposition de bail
                    System.out.println("Veuillez entrer les informations sur la nouvelle proposition de bail");
                    System.out.println("- Entrez l'unité de la période de location. (s = secondes, m = minutes, h = heures, j = jours, o = mois(30 jours))");
                    String[] stringArray2 = {"s","m","h","j","o"};
                    String reponse2 = takeValidAnswer(stringArray2);
                    String periode;
                    if(reponse2.equals("s")){
                        periode = "Seconde";
                    }
                    else if(reponse2.equals("m")){
                        periode = "Minute";
                    }
                    else if(reponse2.equals("h")){
                        periode = "Heure";
                    }
                    else if(reponse2.equals("j")){
                        periode = "Jour";
                    }
                    else{
                        periode = "Mois";
                    }
                    System.out.println("- Entrez le nombre de période que durera la location.");
                    long nbPeriode = takePositiveInteger(); 
                    System.out.println("- Entrez le loyer par période en $ canadien (seulement des entiers son acceptés).");
                    long loyer = takePositiveInteger(); 
                    System.out.println("- Entrez si le bail est renouvelable ou non (y = oui, n = non)");
                    String[] stringArray3 = {"y","n"};
                    String reponse3 = takeValidAnswer(stringArray3);
                    Boolean renouvelable;
                    if(reponse3.equals("y")){renouvelable = true;}
                    else{renouvelable = false;}
                    JSONObject dateDeDebut = new JSONObject();
                    
                    if(jUnite.get("Etat").toString().equals("Loue")){
                        JSONArray bails = JsonManager.getArrayOfJsonFile("JsonBail.json");
                        JSONObject bail = new JSONObject();
                        for (Object object : bails) {
                            JSONObject jBail = (JSONObject)object;
                            if(jBail.get("Identifiant de l'unite").toString().equals(jUnite.get("Identifiant").toString())){
                                bail = jBail;
                            }
                        }
                        while(true){
                            System.out.println("- Entrez la date de début de location sous le format aaaa-MM-jj-hh-mm-ss.");
                            dateDeDebut = TimeManager.takeValidJObjectDate();
                            JSONObject dateFinBail = (JSONObject)bail.get("Date de fin");
                            if(!TimeManager.getDate1BeforeDate2(dateDeDebut, dateFinBail)){
                                break;
                            }
                            System.out.println("ERREUR: La date de début entré est avant la fin du bail courrant de cette unité.");
                        }
                    }
                    else{
                        System.out.println("- Entrez la date de début de location sous le format aaaa-MM-jj-hh-mm-ss.");
                        dateDeDebut = TimeManager.takeValidJObjectDate();
                    }
                    long interval = TimeManager.calulateTimeIntervalInSeconds(periode, nbPeriode);
                    JSONObject dateDeFin = TimeManager.addTimeIntervalToJDate(dateDeDebut, interval);
                    System.out.println("Voulez vous ajouter un supplément à cette proposition de bail? (y = oui, n = non)");
                    reponse3 = takeValidAnswer(stringArray3);
                    JSONArray suplements = new JSONArray();
                    if(reponse3.equals("y")){
                        while(true){
                            JSONObject suplement = new JSONObject();
                            System.out.print("- Entrez le nom du supplément: ");
                            String nomSuplement = scanner.nextLine();
                            System.out.print("- Entrez une description du suplément: ");
                            String descriptionSuplement =  scanner.nextLine();
                            System.out.println("- Entrez coût du supplément par période en $ canadien (seulement des entiers son acceptés).");
                            long cout = takePositiveInteger();
                            suplement.put("Nom", nomSuplement);
                            suplement.put("Description", descriptionSuplement);
                            suplement.put("Cout", cout);
                            suplements.add(suplement);
                            System.out.println("Voulez vous ajouter un autre supplément à cette proposition de bail? (y = oui, n = non)");
                            reponse3 = takeValidAnswer(stringArray3);
                            if(reponse3.equals("n")){break;}
                        }
                    }
                    //4 Créer la proposition de bail via les reponses donnes
                    System.out.println("Voulez vous publier cette proposition de bail? (y = oui, n = non)");
                    reponse3 = takeValidAnswer(stringArray3);
                    if(reponse3.equals("y")){
                        PropositionDeBail.createPropositionDeBail(nomUnite, periode, nbPeriode, loyer, dateDeDebut, dateDeFin, renouvelable, nomProprietaire, suplements);
                    }
                }
                
            }

            //// Modifier une proposition de bail
            else if(reponse0.equals("p")){
                miseAJour();
                PropositionDeBail.modifierPropositionDeBail(nomProprietaire);
            }
            

            //// Retourner au menu pricipal
            else{
                menuLocataire(nomProprietaire);
                System.out.println("--------------------");
                return;
            }
            System.out.println("\n--------------------\n");
        }
    }
}