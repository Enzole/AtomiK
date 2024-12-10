import extensions.File;
import extensions.CSVFile; 

class Atomik extends Program{

    // Constante des fichiers sprites 

    final File shorse = newFile("ressources/sprites/shorse");

    //Constante des attaques par difficulté

    final CSVFile FACILE = loadCSV("ressources/attaques/facile.csv");
    final CSVFile NORMAL = loadCSV("ressources/attaques/normal.csv");
    final CSVFile DIFFICILE = loadCSV("ressources/attaques/difficile.csv");
    final CSVFile DEFIS = loadCSV("ressources/attaques/defis.csv");





    

    final int PV = 100;

// ===Gestion des "Acteurs" (Ordi ou joueurs)===

    Acteur newActeur(String name, boolean est_ordi, String chm){
        Acteur act = new Acteur();

        act.pv= PV;
        act.nom= name;
        act.joueur= est_ordi;
        act.sprite = chm;

        return act;

    }

// ===Fonctions d'affichages===

    void afficherAdversaire(Acteur act1){
        
        println("pv :"+ (act1.pv) +'/'+ (PV) +"\n");

        while(ready(shorse)){
            println(readLine(shorse));
        }

    }

    void afficherAttaques(){

        println("Rappel de la puissance des attaques : Facile > 25 pts | Moyen > 50 pts | Difficile > 75 pts | Défi > restaure 50 pts de vie");





    }

    //void combat(Acteur act1, Acteur act2);

    

    void algorithm(){

        Acteur j = newActeur("SHORSE", false, " ");

        Acteur ordi = newActeur("SECQ", false, "");

        //afficherAdversaire(j);


    }


}