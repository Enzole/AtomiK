class Atomik extends program{

    final int PV = 100;

    Acteur newActeur(String name, boolean vivant, String app){
        Acteur act = new Acteur();

        act.pv= PV;
        act.nom= name;
        act.type= vivant;
        act.sprite = app;

    }

    void toString(Acteur act){
        print(act.sprite);

    }

    void adversaire(Acteur act1, Acteur act2){
        println("pv :"+ (string) act1.pv +'/'+ (string) PV);



    }

    void combat(Acteur act1, Acteur act2);


}