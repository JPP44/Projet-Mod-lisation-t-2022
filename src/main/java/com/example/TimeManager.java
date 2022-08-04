package com.example;

import java.time.LocalDateTime;

import java.util.Calendar;

import org.json.simple.JSONObject;

public class TimeManager {

    public static void main(String[] args) {
        JSONObject newDate = takeValidJObjectDate();
        System.out.println(newDate);

    }
    public static long calulateTimeIntervalInSeconds(String periode, long nbPeriode){
        long timeInterval;
        if(periode.equals("Mois")){
            timeInterval = nbPeriode*2592000;
        }
        else if(periode.equals("Jour")){
            timeInterval = nbPeriode*86400;
        }
        else if(periode.equals("Heure")){
            timeInterval = nbPeriode*3600;
        }
        else if(periode.equals("Minute")){
            timeInterval = nbPeriode*60;
        }
        else{
            timeInterval = nbPeriode;
        }
        return timeInterval;
    }

    public static JSONObject addTimeIntervalToJDate(JSONObject jDate, long interval){
        long anneL = (long)jDate.get("Annee");
        long moisL = (long)jDate.get("Mois");
        long jourL = (long)jDate.get("Jour");
        long heureL = (long)jDate.get("Heure");
        long minuteL = (long)jDate.get("Minute");
        long secondeL = (long)jDate.get("Seconde");
        int anne = (int)anneL;
        int mois = (int)moisL-1;
        int jour = (int)jourL;
        int heure = (int)heureL;
        int minute = (int)minuteL;
        int seconde = (int)secondeL;
        Calendar cal = Calendar.getInstance();
        cal.set(anne,mois,jour,heure,minute,seconde);
        cal.add(cal.SECOND,(int)interval);
        JSONObject newJDate = new JSONObject();
        newJDate.put("Annee",(long)cal.get(Calendar.YEAR));
        newJDate.put("Mois",(long)cal.get(Calendar.MONTH)+1);
        newJDate.put("Jour",(long)cal.get(Calendar.DAY_OF_MONTH));
        newJDate.put("Heure",(long)cal.get(Calendar.HOUR_OF_DAY));
        newJDate.put("Minute",(long)cal.get(Calendar.MINUTE));
        newJDate.put("Seconde",(long)cal.get(Calendar.SECOND));
        return newJDate;
    }

    public static JSONObject getPresentTime(){
        Calendar cal = Calendar.getInstance();
        JSONObject jDate = new JSONObject();
        jDate.put("Annee",(long)cal.get(Calendar.YEAR));
        jDate.put("Mois",(long)cal.get(Calendar.MONTH)+1);
        jDate.put("Jour",(long)cal.get(Calendar.DAY_OF_MONTH));
        jDate.put("Heure",(long)cal.get(Calendar.HOUR_OF_DAY));
        jDate.put("Minute",(long)cal.get(Calendar.MINUTE));
        jDate.put("Seconde",(long)cal.get(Calendar.SECOND));
        return jDate;
    }

    //Générise l'entrée de date pour renvoyer un objet de type JSONObject
    public static JSONObject takeValidJObjectDate(){
        long seconde, minute, heure, jour, mois, annee;
        Boolean isToday;
        LocalDateTime date = LocalDateTime.now();
        
        while(true){
            System.out.println("Entrez l'année:");
            annee = Interface.takePositiveInteger();
            if((annee>=date.getYear())){
                if(annee == date.getYear()){isToday = true;}
                else{isToday = false;}
                break;
            }
            System.out.println("ERREUR: Entrez une reponse valide");
        }

        while(true){
            System.out.println("Entrez le mois:");
            mois = Interface.takePositiveInteger();
            if((mois<=12)){
                if(isToday){
                    if(mois >= date.getMonthValue()){
                        if(!(mois == date.getMonthValue())){
                            isToday = false;
                        }
                        break;
                    }
                }else{break;}
            }
            System.out.println("ERREUR: Entrez une reponse valide");
        }

        while(true){
            System.out.println("Entrez le jour:");
            jour = Interface.takePositiveInteger();
            if((mois == 1 || mois == 3 || mois == 5 || mois == 7 || mois == 8 || mois == 10 || mois == 12)&&jour<=31){
                if(isToday){
                    if(jour >= date.getDayOfMonth()){
                        if(!(jour == date.getDayOfMonth())){
                            isToday = false;
                        }
                        break;
                    }
                }else{break;}
            }
            else if((mois == 4 || mois == 6 || mois == 9 || mois == 11)&&jour<=30){
                if(isToday){
                    if(jour >= date.getDayOfMonth()){
                        if(!(jour == date.getDayOfMonth())){
                            isToday = false;
                        }
                        break;
                    }
                }else{break;}
            }
            else if(jour <= 27){
                if(isToday){
                    if(jour >= date.getDayOfMonth()){
                        if(!(jour == date.getDayOfMonth())){
                            isToday = false;
                        }
                        break;
                    }
                }else{break;}
            }
            System.out.println("ERREUR: Entrez une reponse valide");
        }

        while(true){
            System.out.println("Entrez l'heure:");
            heure = Interface.takePositiveInteger();
            if((heure<24)){
                if(isToday){
                    if(heure >= date.getHour()){
                        if(!(heure == date.getHour())){
                            isToday = false;
                        }
                        break;
                    }
                }else{break;}
            }
            System.out.println("ERREUR: Entrez une reponse valide");
        }

        while(true){
            System.out.println("Entrez la minute:");
            minute = Interface.takePositiveInteger();
            if((minute<60)){
                if(isToday){
                    if(minute >= date.getMinute()){
                        if(!(minute == date.getMinute())){
                            isToday = false;
                        }
                        break;
                    }
                }else{break;}
            }
            System.out.println("ERREUR: Entrez une reponse valide");
        }

        while(true){
            System.out.println("Entrez la seconde:");
            seconde = Interface.takePositiveInteger();
            if((seconde<60)){
                if(isToday){
                    if(seconde >= date.getSecond()+1){
                        if(!(seconde == date.getSecond())){
                            isToday = false;
                        }
                        break;
                    }
                }else{break;}
            }
            System.out.println("ERREUR: Entrez une reponse valide");
        }
        JSONObject jDate = new JSONObject();
        jDate.put("Annee", annee);
        jDate.put("Mois", mois);
        jDate.put("Jour", jour);
        jDate.put("Heure", heure);
        jDate.put("Minute", minute);
        jDate.put("Seconde", seconde);
        return jDate;
    }

    public static Boolean getDate1BeforeDate2(JSONObject date1, JSONObject date2){
        long anneL1 = (long)date1.get("Annee");
        long moisL1 = (long)date1.get("Mois");
        long jourL1 = (long)date1.get("Jour");
        long heureL1 = (long)date1.get("Heure");
        long minuteL1 = (long)date1.get("Minute");
        long secondeL1 = (long)date1.get("Seconde");
        int anne1 = (int)anneL1;
        int mois1 = (int)moisL1-1;
        int jour1 = (int)jourL1;
        int heure1 = (int)heureL1;
        int minute1 = (int)minuteL1;
        int seconde1 = (int)secondeL1;
        Calendar cal1 = Calendar.getInstance();
        cal1.set(anne1,mois1,jour1,heure1,minute1,seconde1);

        long anneL = (long)date2.get("Annee");
        long moisL = (long)date2.get("Mois");
        long jourL = (long)date2.get("Jour");
        long heureL = (long)date2.get("Heure");
        long minuteL = (long)date2.get("Minute");
        long secondeL = (long)date2.get("Seconde");
        int anne = (int)anneL;
        int mois = (int)moisL-1;
        int jour = (int)jourL;
        int heure = (int)heureL;
        int minute = (int)minuteL;
        int seconde = (int)secondeL;
        Calendar cal2 = Calendar.getInstance();
        cal2.set(anne,mois,jour,heure,minute,seconde);
        return cal1.before(cal2);
    }
}
