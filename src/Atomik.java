import extensions.File;
import extensions.CSVFile;

class Atomik extends Program {

    // Variable des fichiers sprites
    File SHORSE = newFile("ressources/sprites/shorse.txt");
    final int PV = 200;

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

    CSVFile sauvegarde = loadCSV("ressources/sauvegardes/sauvegardes.csv");
    int nbParties = rowCount(sauvegarde) - 1;
    int choixParties = 0;

    int choixPartiesJ1 = 0;
    int choixPartiesJ2 = 0;
    
    String nomJ1 = "aaa";
    String nomJ2 = "bbb";


    int vitessedialogues = 30;
    String loginMulti = "non";

    Acteur newActeur(String name, boolean est_ordi, String apparence) {
        Acteur act = new Acteur();
        act.pv = PV;
        act.nom = name;
        act.joueur = est_ordi;
        act.sprite = apparence;
        return act;
    }

    // === Fonctions d'affichages ===
    void afficherAdversaire(Acteur act1, Acteur act2) {

        String couleurPVJ1 = getCouleurPV(act1.pv);
        String couleurPVJ2 = getCouleurPV(act2.pv);
        
        File sprite1 = newFile("ressources/sprites/" + act1.sprite + ".txt");
        File sprite2 = newFile("ressources/sprites/" + act2.sprite + ".txt");

        println("PV de " + act1.nom + ":" +couleurPVJ1+ (act1.pv) + '/' + (PV) + "\033[0m    PV de "+ act2.nom + ":" +couleurPVJ2+ (act2.pv) + '/' + (PV) + "\n\033[0m");

        while (ready(sprite1) && ready(sprite2)) {
            println(readLine(sprite1) + "    " + readLine(sprite2));
        }

        println();
    }

    String getCouleurPV(int pv) {
        if (pv > 150) {
            return "\033[32m";
        } else if (pv > 100) {
            return "\033[33m";
        } else if (pv > 50) {
            return "\033[31m";
        } else {
            return "\033[31m";
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
        dialogues("\033[3mRappel de la puissance des attaques :\033[0m 1. Facile > 25 pts | 2. Moyen > 50 pts | 3. Difficile > 75 pts | 4. Défis > restaure 50 pts de vie. \n");
        dialogues("Quel niveau de difficulté voulez vous ?");

        int choixDifficulte = verifSaisie(1, 4);

        switch (choixDifficulte) {
            case 1:
                question.difficulte = FACILE;
                break;
            case 2:
                question.difficulte = NORMAL;
                break;
            case 3:
                question.difficulte = DIFFICILE;
                break;
            case 4:
                question.difficulte = DIFFICILE;
                break;
        }


        question.estBonus = choixDifficulte == 4;

        switch (choixDifficulte) {
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

        String[] affiche = new String[2];
        question.bonneReponse = (int) (1 + random() * question.difficulte);

        affiche[1] = "";
        affiche[0] = getCell(QUESTION, question.indiceCSV, 0);

        String[] reponses = new String[question.difficulte];
        reponses[question.bonneReponse - 1] = getCell(QUESTION, question.indiceCSV, 1);

        for (int i = 0; i < question.difficulte; i++) {
            if (i != question.bonneReponse - 1) {
                String reponse;
                boolean unique;
                do {
                    unique = true;
                    reponse = getCell(QUESTION, question.indiceCSV, (int) (1 + random() * 6));
                    if (equals(reponse, getCell(QUESTION, question.indiceCSV, 1))) {
                        unique = false;
                    }
                    for (int j = 0; j < i; j++) {
                        if (equals(reponses[j], reponse)) {
                            unique = false;
                            break;
                        }
                    }
                } while (!unique);
                reponses[i] = reponse;
            }
            affiche[1] = affiche[1] + (i + 1) + ". " + reponses[i] + " ";
        }

        return affiche;
    }

    boolean verifierQuestion(Question question) {

        dialogues("Votre réponse ?");

        int entree = verifSaisie(1, question.difficulte);

        return equals(getCell(QUESTION, question.indiceCSV, question.bonneReponse), getCell(QUESTION, question.indiceCSV, entree));
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

        if (nbParties == 0) {
            nouvelleSauvegarde();
            intro();
        } else {
            dialogues("Voici les sauvegardes disponibles.");

            String[] parties = new String[nbParties];
            
            for (int i = 0; i < length(parties); i++) {
                if (combatEnCours && i == choixPartiesJ1-1) { //on ignore la première ligne du csv
                    parties[i] = (i + 1) + ". Déjà prise par J1.";
                }else{
                    parties[i] = (i + 1) + ". " + getCell(sauvegarde, i + 1, 0);
                }
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

    void sauvegarder(int idxChangements, String modif, int joueur) {

        // modifie la valeur du csv à partir de son indice de colonne.
        String[][] newSave = new String[rowCount(sauvegarde)][5];
        int nbSave = rowCount(sauvegarde);

        for (int i = 0; i < nbSave; i++) {
            for (int j = 0; j < 5; j++) {
                newSave[i][j] = getCell(sauvegarde, i, j);
            }
        }

        int choixPartie;
        if (joueur == 1) {
            choixPartie = choixPartiesJ1;
        } else {
            choixPartie = choixPartiesJ2;
        }

        for (int i = 0; i < 5; i++) {
            if (i != idxChangements) {
            newSave[choixPartie][i] = getCell(sauvegarde, choixPartie, i);
            } else {
            newSave[choixPartie][i] = modif;
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
        int choix = verifSaisie(1, 4);

        switch (choix) {
            case 1:
                combat(0, "Puel");
                break;
            case 2:
                combat(1, null);
                break;
            case 3:
                dialogues("Cette fonctionnalité n'est pas encore disponible.");
                menu();
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
        int choix = verifSaisie(1, 3); //à réactiver avant livraison !!!

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
                dialogues("\nLancement du menu de tests...\n");
                Debugmenu();
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
                sauvegarder(CREATURE,choixCompagnon(), 1);
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

        sauvegarder(VITESSE, vitessedialogues + "",1);
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
        
        dialogues("1. \033[1mGradon\033[0m");
        afficherSpriteAvecNom("Gradon");
        delay(500);
        dialogues("2. \033[1mDragon\033[0m");
        afficherSpriteAvecNom("Dragon");
        delay(500);
        dialogues("3. \033[1mGhost\033[0m");
        afficherSpriteAvecNom("Ghost");
        delay(500);
        dialogues("4. \033[1mShorse\033[0m");
        afficherSpriteAvecNom("Shorse");
        
        int choix = verifSaisie(1, 4);
        
        switch(choix){
            case 1: return "gradon";
            case 2: return "dragon";
            case 3: return "ghost";
            case 4: return "shorse";

            default: return "shorse";
        }
    }

    void afficherSpriteAvecNom(String nom) {
        File sprite = newFile("ressources/sprites/" +nom + ".txt");
        while (ready(sprite)) {
            println(readLine(sprite));
        }
        println();
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
            dialogues(readLine(INTRO1));
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
                clearScreen();
            }

            if(event==6){
                up(1);
                clearLine();
                sauvegarder(CREATURE,choixCompagnon(),1);
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
                    combat(0, "Puel");
                }
                menu();
            }
        }
    }

    Acteur prepaBot(String botNom){

        String[] creatures = {"shorse", "gradon", "dragon", "ghost"};
        int randomIndex = (int) (random() * length(creatures));

        Acteur act2 = newActeur(botNom, true, creatures[randomIndex]);
        
        return act2;
    }

    Acteur prepaJoueur(String nomJoueur){
                    
        dialogues("Veuillez choisir une sauvegarde pour le second joueur");
        println("\033[2mAppuyez sur entrée pour continuer.\033[0m");
        readString();
        chargerSauvegarde();

        return newActeur(getCell(sauvegarde, choixPartiesJ2, NOM), false, getCell(sauvegarde, choixPartiesJ2, CREATURE));
    }

    int usageRobot(Acteur bot, String nomJ1){
        dialogues("\033[2m"+bot.nom + " est en train de jouer...\033[0m");
        delay(800);
        int reussiteBot = (int) (random() * 1);

        if(reussiteBot==0){
            int botDifficulte= (int) (random() * 3);
            int valeurReponse=0;
            switch(botDifficulte){
                case 0 : valeurReponse = 25; break;
                case 1 : valeurReponse = 50; break;
                case 2 : valeurReponse = 75; break; 
            }

            dialogues("\033[32mAttaque réussie, " + nomJ1 + " subit \033[1m" + valeurReponse + "\033[0m\033[32m dégâts.\033[0m\n");

            return valeurReponse;

        }else{
                dialogues("\033[31m"+bot.nom+" a échoué l'attaque ![0m\n");
                return 0;
        }
                
    }


    void combat(int typePartie, String botNom){
        // typePartie = 1 -> multijoueur Local, 2 -> multiPoste, 0 -> solo
        //Couleurs : question  en vert, "C'est au tour" plutôt discret.

        clearScreen();
        combatEnCours = true;
        Acteur act1 = newActeur(getCell(sauvegarde, choixPartiesJ1, NOM), false, getCell(sauvegarde, choixPartiesJ1, CREATURE));
        Acteur act2 = null;

        switch (typePartie) {
            case 1: act2 = newActeur(getCell(sauvegarde, choixPartiesJ2, NOM), false, getCell(sauvegarde, choixPartiesJ2, CREATURE)); break;
            case 0: act2 = prepaBot(botNom); break;
            // Add more cases if needed for other types of parties
        }

        boolean who = false;
        Question qcm;

        if ((int)(random() * 2) == 0) { // choix aléatoire du joueur de départ.
            dialogues("\033[2mC'est à " + act2.nom + " de commencer.\033[0m");
            who = true;
        } else {
            dialogues("\033[2mC'est à " + act1.nom + " de commencer.\033[0m");
            who = false;
        }

        while (act1.pv >= 0 && act2.pv >= 0) {
            afficherAdversaire(act1, act2);

            if (who && typePartie == 0) {
                act1.pv -= usageRobot(act2, act1.nom);
            } else {
                qcm = initialiserQuestion();
                engloberCadre(questionToTab(qcm));
                if (verifierQuestion(qcm)) {
                    if (!qcm.estBonus && who && typePartie == 1) {
                        act1.pv -= qcm.valeur;
                        dialogues("\033[32mAttaque réussie, " + act1.nom + " subit \033[1m" + qcm.valeur + "\033[0m\033[32m dégâts.\033[0m\n");
                    } else if (!qcm.estBonus && !who) {
                        act2.pv -= qcm.valeur;
                        dialogues("\033[32mAttaque réussie, " + act2.nom + " subit \033[1m" + qcm.valeur + "\033[0m\033[32m dégâts.\033[0m\n");
                    } else if (qcm.estBonus && who && typePartie == 1) {
                        act1.pv += qcm.valeur;
                        dialogues("\033[32mAttaque réussie, " + act1.nom + " se soigne de \033[1m" + qcm.valeur + "\033[0m\033[32m points de vie.\033[0m\n");
                    } else if (qcm.estBonus && !who) {
                        act2.pv += qcm.valeur;
                        dialogues("\033[32mAttaque réussie, " + act2.nom + " se soigne de \033[1m" + qcm.valeur + "\033[0m\033[32m points de vie.\033[0m\n");
                    }
                } else {
                    dialogues("\033[31mAttaque échouée... La bonne réponse était \033[1m" + getCell(QUESTION, qcm.indiceCSV, 1) + "\033[0m\n");
                }
            }

            if (act1.pv <= 0 && act2.pv <= 0) {
                dialogues("Égalité !");
                combatEnCours = false;
                sauvegarder(VICTOIRE, (stringToInt(getCell(sauvegarde, choixPartiesJ1, VICTOIRE)) + 1) + "", 1);
                sauvegarder(VICTOIRE, (stringToInt(getCell(sauvegarde, choixPartiesJ1, VICTOIRE)) + 1) + "", 2);
                menu();
            } else if(act1.pv <= 0){
                dialogues("Joueur " + act2.nom + " gagne !");
                combatEnCours = false;
                sauvegarder(DEFAITE, (stringToInt(getCell(sauvegarde, choixPartiesJ1, DEFAITE)) + 1) + "", 1);
                sauvegarder(VICTOIRE, (stringToInt(getCell(sauvegarde, choixPartiesJ1, VICTOIRE)) + 1) + "", 2);
                menu();
            } else if(act2.pv <= 0){
                dialogues("Joueur " + act1.nom + " gagne !");
                combatEnCours = false;
                sauvegarder(VICTOIRE, (stringToInt(getCell(sauvegarde, choixPartiesJ1, VICTOIRE)) + 1) + "", 1);
                sauvegarder(DEFAITE, (stringToInt(getCell(sauvegarde, choixPartiesJ1, DEFAITE)) + 1) + "", 2);
                menu();
            }

            delay(500);
            clearScreen();

            if (combatEnCours) {
                who = !who;
                if (who) {
                    dialogues("\033[2mC'est au tour de \033[3m" + act2.nom + ".\033[0m");
                } else {
                    dialogues("\033[2mC'est au tour de \033[3m" + act1.nom + ".\033[0m");
                }
            }
        }
    }

    void algorithm() {
        chargerSauvegarde();
    }

    //Fonction débug

    void debugAfficherQuestion(){
        clearScreen();

        Question qcm;
        for(int i=0; i<10; i++){
            qcm = initialiserQuestion();
            engloberCadre(questionToTab(qcm));
        }

        Debugmenu();

    }

    void Debugmenu(){
        vitessedialogues=2;
        final String[] options = {"1. DEBUG AffichageQuestion", " ", "2. DEBUG MultiPostes", " ", "3. Spécial Puel", " ", "4. Retour au menu"};
        afficherLogo();
        engloberCadre(options);

        int choix = readInt();

        switch(choix){
            case 1:
                debugAfficherQuestion(); break;

            case 2:
                //debugMultiPostes(); break;

            case 3:
                intro(); break;
            case 4:
                menu(); break;
            default:
                menu(); 
        }
    }

    void testNewActeur() {
        Acteur acteur = newActeur("TestName", true, "TestSprite");

        assertEquals(200, acteur.pv);
        assertEquals("TestName", acteur.nom);
        assertTrue(acteur.joueur);
        assertEquals("TestSprite", acteur.sprite);
    }


    void testGetCouleurPV() {

        assertEquals("\033[32m", getCouleurPV(200));
        assertEquals("\033[33m", getCouleurPV(120));
        assertEquals("\033[31m", getCouleurPV(80));
        assertEquals("\033[31m", getCouleurPV(30));
    }

    void testMax() {

        assertEquals(5, max(3, 5));
        assertEquals(10, max(10, 10));
        assertEquals(7, max(7, 2));
    }

    void testInitialiserQuestion() {
        Question question = initialiserQuestion();

        assertTrue(question.difficulte >= FACILE && question.difficulte <= DIFFICILE);
        assertTrue(question.valeur == 25 || question.valeur == 50 || question.valeur == 75 || question.valeur == 50);
        assertTrue(question.indiceCSV >= 1 && question.indiceCSV < rowCount(QUESTION));
    }

    void testQuestionToTab() {
        Question question = initialiserQuestion();
        String[] tab = questionToTab(question);
        assertEquals(2, length(tab));

    }

    void testVerifierQuestion() {
        Question question = initialiserQuestion();
        question.bonneReponse = 1;

        engloberCadre(questionToTab(question));
        assertTrue(verifierQuestion(question));
    }

    void testVerifNom() {

        assertTrue(verifNom("ValidName"));
        assertFalse(verifNom("Invalid,Name"));
    }

    void testVerifSaisie() {

        assertEquals(2, verifSaisie(1, 3));
    }

    void testQueChiffre() {

        assertEquals(123, queChiffre("123"));
    }


    //void hebergerPartie(){ // Un concept original dont l'implémentation ne pourra être faite dans les temps...

        //String[][] data = new String[rowCount(start)][columnCount(start,0)];

        //for(int i = 0; i<length(data,1);i++){
          //  for(int j=0; j<length(data,1);j++){

            //    data[i][j]= getCell(start, i, j);
            //}
        //}

        //println("Veuiller entrer votre login pour héberger la partie sur votre session.");
        //print('>');
        
        //String login = readString();

        //println("Veuillez executer la commande suivante dans un terminal pour rendre votre partie accesible");
        //println("mkdir ~/AtomikComm && chmod g+rw ~/AtomikComm && touch comm.csv");
        //print("Appuyez sur entrée lorsque vous êtes prêt");

        //readString();


        //saveCSV(data, "/home/infoetu/"+login+"/AtomikComm/comm.csv");

    //}
}



