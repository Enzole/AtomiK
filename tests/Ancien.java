
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


class AtomikTest {

    void testNewActeur() {
        Acteur acteur = atomik.newActeur("TestName", true, "TestSprite");

        assertEquals(200, acteur.pv);
        assertEquals("TestName", acteur.nom);
        assertTrue(acteur.joueur);
        assertEquals("TestSprite", acteur.sprite);
    }


    void testGetCouleurPV() {

        assertEquals("\033[32m", atomik.getCouleurPV(200));
        assertEquals("\033[33m", atomik.getCouleurPV(120));
        assertEquals("\033[31m", atomik.getCouleurPV(80));
        assertEquals("\033[31m", atomik.getCouleurPV(30));
    }

    void testMax() {

        assertEquals(5, atomik.max(3, 5));
        assertEquals(10, atomik.max(10, 10));
        assertEquals(7, atomik.max(7, 2));
    }

    void testInitialiserQuestion() {
        Question question = atomik.initialiserQuestion();

        assertNotNull(question);
        assertTrue(question.difficulte >= atomik.FACILE && question.difficulte <= atomik.DIFFICILE);
        assertTrue(question.valeur == 25 || question.valeur == 50 || question.valeur == 75 || question.valeur == 50);
        assertTrue(question.indiceCSV >= 1 && question.indiceCSV < atomik.rowCount(atomik.QUESTION));
    }

    void testQuestionToTab() {
        Question question = atomik.initialiserQuestion();
        String[] tab = atomik.questionToTab(question);

        assertNotNull(tab);
        assertEquals(2, tab.length);
        assertNotNull(tab[0]);
        assertNotNull(tab[1]);
    }

    void testVerifierQuestion() {
        Question question = atomik.initialiserQuestion();
        question.bonneReponse = 1;

        assertTrue(atomik.verifierQuestion(question));
    }

    void testVerifNom() {

        assertTrue(atomik.verifNom("ValidName"));
        assertFalse(atomik.verifNom("Invalid,Name"));
    }

    void testVerifSaisie() {

        assertEquals(2, atomik.verifSaisie(1, 3));
    }

    void testEstNombre() {

        assertTrue(atomik.estNombre("123"));
        assertFalse(atomik.estNombre("abc"));
    }

    void testQueChiffre() {

        assertEquals(123, atomik.queChiffre("123"));
    }
}
