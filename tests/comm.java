import extensions.CSVFile;

class comm extends Program{

    final CSVFile start = loadCSV("ressources/comm.csv");

    void algorithm(){
        hebergerPartie();
    }

    void hebergerPartie(){

        String[][] data = new String[rowCount(start)][columnCount(start,0)];

        for(int i = 0; i<length(data,1);i++){
            for(int j=0; j<length(data,1);j++){

                data[i][j]= getCell(start, i, j);
            }
        }

        println("Veuiller entrer votre login pour héberger la partie sur votre session.");
        print('>');
        
        String login = readString();

        println("Veuillez executer la commande suivante dans un terminal pour rendre votre partie accesible");
        println("mkdir ~/AtomikComm && chmod g+rw ~/AtomikComm && touch comm.csv");
        print("Appuyez sur entrée lorsque vous êtes prêt");

        readString();


        saveCSV(data, "/home/infoetu/"+login+"/AtomikComm/comm.csv");

    }



    
}