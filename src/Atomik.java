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




    // ===Gestion des "Acteurs" (Ordi ou joueurs)===

    final int PV = 100;

    Acteur newActeur(String name, boolean est_ordi, File apparence){
        Acteur act = new Acteur();

        act.pv= PV;
        act.nom= name;
        act.joueur= est_ordi;
        act.sprite = apparence;

        return act;

    }



// ===Fonctions d'affichages===

    void afficherAdversaire(Acteur act){
        
        println("pv :"+ (act.pv) +'/'+ (PV) +"\n");
    
        println(readLine(act.sprite));

    }

    void afficherAttaques(){

        println("Rappel de la puissance des attaques : Facile > "+ getCell(FACILE, 0,1) +"pts | Normal > "+ getCell(NORMAL,0,1)+ " pts | Difficile > " +getCell(DIFFICILE,0,1)+ "pts | Défi > restaure "+ getCell(DEFIS,0,1)+" pts de vie");
        engloberCadre("aaa");


    }


    void engloberCadre(String chaine){

        
        String haut = "";
        String cote ="";
        String centrer = "";

        for(int i =0; i< 40 ; i++){
            haut = haut + '=';
        }


        
        println(haut+"\n|\n|\n"+"hello"+"|\n|\n"+haut);

    }

    //void combat(Acteur act1, Acteur act2);

    

    void algorithm(){

        Acteur j = newActeur("SHORSE", false, shorse);

        Acteur ordi = newActeur("SECQ", false,shorse);

        //afficherAdversaire(j);
        afficherAttaques();


    }


}