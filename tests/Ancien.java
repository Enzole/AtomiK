
    int gererQuestion(boolean bot) {
        // faire le type question !!
        print("choix difficulté >>>");
        int difficulte = readInt();

        switch (difficulte) {
            case 1:
                choix = FACILE;
                break;
            case 2:
                choix = NORMAL;
                break;
            case 3:
                choix = DIFFICILE;
                break;
            case 4:
                choix = DEFIS;
                break;
            default:
                dialogues("Ce niveau de difficulté : " + difficulte + " n'existe pas.");
        }

        int aleaquestion = (int) (1 + (rowCount(choix) - 1) * random()); // on ignore la première ligne et on empêche le out of bound;
        String[][] question = {getCell(choix, aleaquestion, 0)};

        engloberCadre(question);

        print(">> ");
        String reponse = toLowerCase(readString());

        if (equals(reponse, getCell(choix, aleaquestion, 1))) {
            dialogues("Attaque réussie, vous infligez " + getCell(choix, 0, 1) + " dégats \n");
            return stringToInt(getCell(choix, 0, 1));
        } else {
            dialogues("Attaque échouée... LA bonné réponse était " + getCell(choix, aleaquestion, 1) + "\n");
            return 0;
        }
    }


void combat(Acteur act1, Acteur act2) {
        Acteur current;
        boolean who = false;

        if ((random() * 1) % 2 == 0) { // choix aléatoire du joueur de départ.
            dialogues("C'est a " + act1.nom + " de commencer.");
            who = true;
            current = act2;
        } else {
            dialogues("C'est a " + act2.nom + " de commencer");
            current = act1;
        }

        while (current.pv > 0) {
            // afficherAdversaire(act1, act2);
            afficherAttaques();
            current.pv = current.pv - gererQuestion(current.joueur); // le choix de la difficulté se fera là, désormais.
            dialogues("C'est au tour de " + current.nom + '.');

            if (who) {
                current = act1;
                who = false;
            } else {
                current = act2;
                who = true;
            }
        }

        if (act1.pv < 0 && act2.pv < 0) {
            dialogues("égalité");
        } else {
            dialogues("Joueur " + current.nom + " gagne !");
        }
    }