import extensions.File;
import extensions.CSVFile;

class Atomik extends Program {

    // Variable des fichiers sprites
    File SHORSE = newFile("ressources/sprites/shorse.txt");

    // Constante du fichier attaques, et nombres de réponses par niveau de difficulté
    final CSVFile QUESTION = loadCSV("ressources/attaques/facile.csv");
    final int FACILE = 3;
    final int NORMAL = 4;
    final int DIFFICILE = 6;

    //Position des valeurs dans le csv (indices)
    final int NOM = 0;
    final int VITESSE = 1;
    final int CREATURE = 2;
    final int VICTOIRE = 3;
    final int DEFAITE = 4;

    // Paramètres de partie Nécéssaires pour la gestion des CSV
    boolean combatEnCours = false;

    int choixParties = 0;

    int choixPartiesJ1 = 0;
    int choixPartiesJ2 = 0;
    
    String nomJ1 = "aaa";
    String nomJ2 = "bbb";

    CSVFile sauvegarde = loadCSV("ressources/sauvegardes/sauvegardes.csv");
    int vitessedialogues = 30;
    String loginMulti = "non";

    // Gestion des "Acteurs" (Ordi ou joueurs)
    final int PV = 100;

    Acteur newActeur(String name, boolean est_ordi, File apparence) {
        Acteur act = new Acteur();
        act.pv = PV;
        act.nom = name;
        act.joueur = est_ordi;
        act.sprite = apparence;
        return act;
    }

    // === Fonctions d'affichages ===
    void afficherAdversaire(Acteur act1, Acteur act2) {
        dialogues("pv de " + act1.nom + ":" + (act1.pv) + '/' + (PV) + "\n");
        dialogues("pv de " + act2.nom + ":" + (act2.pv) + '/' + (PV) + "\n");

        while (ready(act1.sprite) || ready(act2.sprite)) {
            print(readLine(act1.sprite));
            forward(5);
            print(readLine(act2.sprite));
            backward(5);
        }
    }

    void dialogues(String dialoguesg) {
        for (int i = 0; i < length(dialoguesg); i++) {
            print(charAt(dialoguesg, i));
            delay(vitessedialogues);
        }
        println();
    }

    void engloberCadre(String[] lignes) {
        // Largeur du cadre
        int largeurCadre = 40;

        // Trouver la longueur maximale parmi les lignes
        int largeurMax = 0;
        for (int i = 0; i < length(lignes); i++) {
            largeurMax = max(largeurMax, length(lignes[i]));
        }

        // Ajouter des marges (2 caractères pour les barres verticales)
        int largeurCadreAjustee = largeurMax + 2;

        String haut = "\033[33m╔";
        for (int i = 0; i < largeurCadreAjustee-2; i++) {
            haut = haut + "═";
        }
        haut = haut + "╗\033[0m";

        // Afficher la ligne du haut
        println(haut);

        // Afficher chaque ligne centrée
        for (int i = 0; i < length(lignes); i++) {
            // Calculer les espaces avant et après pour centrer la ligne
            int espacesAvant = (largeurCadreAjustee - 2 - length(lignes[i])) / 2;
            String centrerLigne = "";

            // Ajouter les espaces avant la chaîne
            for (int j = 0; j < espacesAvant; j++) {
                centrerLigne = centrerLigne + " ";
            }

            // Ajouter la chaîne de caractères
            centrerLigne = centrerLigne + lignes[i];

            // Ajouter les espaces après la chaîne
            for (int j = 0; j < largeurCadreAjustee - 2 - espacesAvant - length(lignes[i]); j++) {
                centrerLigne = centrerLigne + " ";
            }

            // Afficher la ligne avec les barres verticales
            println("\033[33m║\033[0m" + centrerLigne + "\033[33m║\033[0m");
        }

        String bas = "\033[33m╚";
        for (int i = 0; i < largeurCadreAjustee-2; i++) {
            bas = bas + "═";
        }
        bas = bas + "╝\033[0m";

        println(bas);
    }

    void afficherLogo() {
        final File LOGO = newFile("ressources/sprites/logo.txt");

        while (ready(LOGO)) {
            println(readLine(LOGO));
        }
    }

    void regles(){
        clearScreen();
        final File regles = newFile("ressources/dialogues/regles.txt");
    
        while(ready(regles)){
            println(readLine(regles));
        }
        println("\033[2mAppuyez sur entrée pour continuer.\033[0m");
        readString();
        clearScreen();
    }

    // == Fonctions "principales" ===
    int max(int i, int j) {
        if (i > j) {
            return i;
        }
        return j;
    }

    Question initialiserQuestion() {

        Question question = new Question();
        dialogues("Rappel de la puissance des attaques : Facile > 25 pts | Moyen > 50 pts | Difficile > 75 pts | Défis > restaure 50 pts de vie.");
        dialogues("Quel niveau de difficulté voulez vous ?");
        question.difficulte = verifSaisie(1, 4);
        question.estBonus = question.difficulte == 4;

        switch (question.difficulte) {
            case 1: question.valeur = 25; break;
            case 2: question.valeur = 50; break;
            case 3: question.valeur = 75; break;
            case 4: question.valeur = 50; break;
        }

        int aleaquestion = (int) (1 + (rowCount(QUESTION) - 1) * random()); // on ignore la première ligne et on empêche le out of bound;
        question.indiceCSV = aleaquestion;

        return question;
    }

    String[] questionToTab(Question question) { //pour l'affichage des questions/réponses dans un cadre

        String[] affiche = new String [question.difficulte+1];
        affiche[0] = getCell(QUESTION, question.indiceCSV, 0);

        for (int i = 0; i < length(affiche, 2); i++) {
            affiche[i+1] = getCell(QUESTION, question.indiceCSV, ((int) random() * i + 1));
        }

        return affiche;
    }

    boolean verifierQuestion(Question question) {

        engloberCadre(questionToTab(question));
        dialogues("Votre réponse ? > ");

        int entree = verifSaisie(1, question.difficulte);

        return equals(getCell(QUESTION, question.indiceCSV, 1), getCell(QUESTION, question.indiceCSV, entree));
    }

    
    boolean verifNom(String mot){
        for(int i = 0 ; i<length(mot); i++){
            if(charAt(mot,i)==','){return false;}
        }
        return true;
    }

    void nouvelleSauvegarde(){
        String[][] newSave = new String[rowCount(sauvegarde) + 1][5];
                int nbSave = rowCount(sauvegarde);
                int nbParties = rowCount(sauvegarde) - 1;

                for (int i = 0; i < nbSave; i++) {
                    for (int j = 0; j < 5; j++) {
                        newSave[i][j] = getCell(sauvegarde, i, j);
                    }
                }

                dialogues("\033[32mEntrez votre nom s'il vous plaît.\033[0m");
                print(">>>");

                String nom = readString();

                while(!verifNom(nom)){
                    dialogues("\033[31mDésolé, votre nom de sauvegarde ne peut contenir de virgule (,).\033[0m");
                    print(">>>");
                    nom = readString();
                }

                // initialisation des valeurs de la nouvelle sauvegarde
                newSave[nbSave][NOM] = nom;
                newSave[nbSave][VITESSE] = "30";
                newSave[nbSave][CREATURE] = "shorse";
                newSave[nbSave][VICTOIRE] = "0";
                newSave[nbSave][DEFAITE] = "0";

                saveCSV(newSave, "ressources/sauvegardes/sauvegardes.csv");
                sauvegarde = loadCSV("ressources/sauvegardes/sauvegardes.csv");
                choixParties = nbParties + 1;

    }

    void chargerSauvegarde(){
        clearScreen();
        int nbParties = rowCount(sauvegarde) - 1;

        if (nbParties == 0) {
            nouvelleSauvegarde();
            intro();
        } else {
            dialogues("Voici les sauvegardes disponibles.");
            String[] parties = new String[nbParties];

            for (int i = 0; i < length(parties); i++) {
                parties[i] = (i + 1) + ". " + getCell(sauvegarde, i + 1, 0);
            }

            engloberCadre(parties);
            dialogues("Pour commencer une nouvelle partie,\033[1m faites le choix 0. \033[0m");
            dialogues("\033[44mVeuillez choisir un joueur.\033[0m");
            choixParties = verifSaisie(0, nbParties);

            if (choixParties == 0) {
                nouvelleSauvegarde();
                dialogues("Votre sauvegarde a été créée sous le nom \033[3m" + getCell(sauvegarde, (rowCount(sauvegarde) - 1), NOM)+"\033[0m.");
                dialogues("\033[2mAppuyez sur entrée pour continuer.\033[0m");
                readString();

                if(!combatEnCours){
                    nomJ1 = getCell(sauvegarde, choixParties, NOM);
                    choixPartiesJ1=choixParties;
                    intro();
                }
                
                nomJ2 = getCell(sauvegarde, choixParties, NOM);
                choixPartiesJ2=choixParties;


                
            } else {
                if(!combatEnCours){
                    vitessedialogues = stringToInt(getCell(sauvegarde, choixParties, VITESSE));
                    nomJ1 = getCell(sauvegarde, choixParties, NOM);
                    choixPartiesJ1=choixParties;
                    menu();
                }
                
                nomJ2 = getCell(sauvegarde, choixParties, NOM);
                choixPartiesJ2=choixParties;
            }
        }
    }

    void sauvegarder(int idxChangements, String modif) {

        // modifie la valeur du csv à partir de son indice de colonne.
        String[][] newSave = new String[rowCount(sauvegarde)][5];
        int nbSave = rowCount(sauvegarde);

        for (int i = 0; i < nbSave; i++) {
            for (int j = 0; j < 5; j++) {
                newSave[i][j] = getCell(sauvegarde, i, j);
            }
        }

        for (int i = 0; i < 5; i++) {
            if (i != idxChangements) {
                newSave[choixParties][i] = getCell(sauvegarde, choixParties, i);
            } else {
                newSave[choixParties][i] = modif;
            }
        }

        saveCSV(newSave, "ressources/sauvegardes/sauvegardes.csv");
        dialogues("\033[1mUne sauvegarde a été effectuée.\033[0m");
        sauvegarde=loadCSV("ressources/sauvegardes/sauvegardes.csv");
        println("\033[2mAppuyez sur entrée pour continuer.\033[0m");
        readString();
    }

    void prepaPartie() {
        clearScreen();
        dialogues("Selectionnez un type de partie.");
        final String[] typesParties = {"1. Un Joueur", " ", "2. Deux joueurs (Multijoueur Local)", " ", "3. Deux joueurs (Multi Postes)", "", "4. Retour au menu"};
        engloberCadre(typesParties);
        int choix = verifSaisie(1, 3);

        switch (choix) {
            case 1:
                combat(1, null);
                break;
            case 2:
                combat(1, null);
                break;
            case 3:
                combat(1, null);
                break;

            case 4:
                menu();
                break;

            default:
                dialogues("Type de partie inconnu. Retour au menu.");
                menu();
        }
    }

    void menu() {

        clearScreen();
        afficherLogo();

        dialogues("Bienvenue sur Atomik, \033[3m" + nomJ1 + "\033[0m");
        println("\033[1m\033[34mCréature : \033[0m" + getCell(sauvegarde, choixParties, CREATURE) + "\033[1m | \033[32mVictoires : \033[0m" + getCell(sauvegarde, choixParties, VICTOIRE) + "\033[1m | \033[31mDéfaites : \033[0m" + getCell(sauvegarde, choixParties, DEFAITE) + "\n");
        dialogues("Veuillez faire un choix :");

        final String[] options = {"1. Partie Rapide", " ", "2. Paramètres", " ", "3. Règles"};
        engloberCadre(options);
        int choix = verifSaisie(1, 3);

        switch (choix) {
            case 1:
                prepaPartie();
                break;
            case 2:
                parametres();
                break;
            case 3:
                regles();
                menu();
                break;
            default:
                dialogues("\nCe choix est invalide... \n");
                menu();
        }
    }

    void parametres() {

        clearScreen();
        afficherLogo();

        String[] parametres = {"1. Changer la vitesse de défilement du texte", "", "2. Configurer le jeu multiPostes", "","3. Changer de compagnon","","4. Changement de sauvegarde","", "5. Retour au menu"};
        engloberCadre(parametres);
        dialogues("Veuillez faire un choix.");
        int choix = verifSaisie(1, 5);

        switch (choix) {
            case 1:
                reglageDialogues();
                break;
            case 2:
                // parametres();
                break;
            case 3:
                sauvegarder(CREATURE,choixCompagnon());
                menu();
                break;
            case 4:
                chargerSauvegarde();
                break;
            
            case 5:
                menu();
                break;

            default:
                dialogues("\nCe choix est invalide... Retour au menu. \n");
                menu();
        }
    }

    void reglageDialogues() {
        clearScreen();
        String[] vitesses = {"1. Rapide", "2. Normal", "3. Lent"};
        engloberCadre(vitesses);
        int choix = verifSaisie(1, 3);

        switch (choix) {
            case 1:
                vitessedialogues = 20;
                break;
            case 2:
                vitessedialogues = 40;
                break;
            case 3:
                vitessedialogues = 60;
                break;
        }

        boolean oui = true;
        dialogues("Voici la nouvelle vitesse des dialogues. Ce choix vous convient il ?");
        dialogues("1. oui 2. non");

        if (verifSaisie(1, 2) == 2) {
            reglageDialogues();
        }

        sauvegarder(VITESSE, vitessedialogues + "");
        menu();
    }

    int verifSaisie(int debut, int fin) {
        println("\033[1mEntrez une valeur entre " + debut + " et " + fin + " compris.\033[0m");
        print(">>> ");
        int valeur = queChiffre(readString());

        while (valeur < debut || valeur > fin) {
            println("Saisie incorrecte. Réessayez.");
            println("\033[1mEntrez une valeur entre " + debut + " et " + fin + " compris.\033[0m");
            print(">>> ");
            valeur = queChiffre(readString());
        }

        return valeur;
    }

    boolean estNombre(String entree) {
        for (int i = 0; i < length(entree); i++) {
            if (charAt(entree, i) >= '0' && charAt(entree, i) <= '9') {
                return false;
            }
        }

        return true;
    }

    int queChiffre(String entree) {
        while (estNombre(entree)) {
            println("\033[31mVeillez à saisir uniquement un ou plusieurs chiffre(s).\033[0m");
            print(">>> ");
            entree = readString();
        }

        return stringToInt(entree);
    }

    String choixCompagnon(){
        clearScreen();
        dialogues("\033[1mChoisissez un compagnon pour vous accompagner dans votre aventure.\033[0m");
        
        dialogues("1. \033[3mShorse\033[0m 2. \033[3mProf\033[0m 3. \033[3mLogo\033[0m");
        int choix = verifSaisie(1, 3);
    
        switch(choix){
            case 1:
                return "Shorse";
            case 2:
                return "Prof";
            case 3:
                return "Logo";
            default:
                return null;
        }

    }

    // Fonction de "Gameplay"
    void intro() {
        clearScreen();
        File PROF = newFile("ressources/sprites/prof.txt");
        final File INTRO1 = newFile("ressources/dialogues/intro1.txt");
        int event = 0;


        while (ready(INTRO1)) {

            while (ready(PROF)) {
                println(readLine(PROF));
            }

            event++;
            dialogues(readLine(INTRO1)+event);
            println("\033[2mAppuyez sur entrée pour passer les dialogues.\033[0m");
            readString();
            up(3);
            clearLine();

            if(event==4){
                dialogues("1. Oui 2. Non");
                int choix = verifSaisie(1, 2);
                if(choix == 1){
                    regles();
                }
                PROF = newFile("ressources/sprites/prof.txt");// permet le réaffichage du proffesseur.
            }

            if(event==6){
                up(1);
                clearLine();
                sauvegarder(CREATURE,choixCompagnon());
                PROF = newFile("ressources/sprites/prof.txt");// permet le réaffichage du proffesseur.
                clearScreen();
            }

            if(event==8){
                up(1);
                clearLine();
                dialogues("Acceptez vous la proposition ?");
                dialogues("1. Oui 2. Non");
                int choix = verifSaisie(1, 2);
                if(choix == 1){
                    combat(1, null);
                }
                menu();
            }
        }
    }

    void combat(int typePartie, String botNom){
        // typePartie = 1 -> multijoueur Local, 2 -> multiPoste, 0 -> solo

        combatEnCours = true;
        Acteur act1 = newActeur(getCell(sauvegarde, choixPartiesJ1, NOM), false, newFile("ressources/sprites/"+getCell(sauvegarde, choixPartiesJ1, CREATURE)+".txt"));
        if(typePartie==1){
            dialogues("Veuillez choisir une sauvegarde pour le second joueur");
            println("\033[2mAppuyez sur entrée pour passer les dialogues.\033[0m");
            readString();
            chargerSauvegarde();
            Acteur act2 = newActeur(getCell(sauvegarde, choixPartiesJ2, NOM), false, newFile("ressources/sprites/"+getCell(sauvegarde, choixPartiesJ2, CREATURE)+".txt"));
        }

        boolean who = false;
        Acteur current = null;

        if ((random() * 1) % 2 == 0) { // choix aléatoire du joueur de départ.
            dialogues("C'est a " + act1.nom + " de commencer.");
            who = true;
            //current = act2;
        } else {
            //dialogues("C'est a " + act2.nom + " de commencer");
            current = act1;
        }
    }

    void algorithm() {
        chargerSauvegarde();
    }
}


