package main;
import java.util.Scanner;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import manegerFtp.FTPServer;
import obse.ObseI;

public class Server {

    public static Communicator mainCommunicator = null;
    public static FTPServer ftpServer = null;
    public static void main(String[] args) throws Exception{
        try(Communicator communicator = Util.initialize(args, "properties.cfg")){
            mainCommunicator = communicator;
            ObjectAdapter adapter = communicator.createObjectAdapter("services");
            ObseI subject = new ObseI();
            adapter.add(subject, Util.stringToIdentity("subject"));

            adapter.activate();

            ftpServer = new FTPServer();
            Scanner scanner = new Scanner(System.in);
            System.out.println("El server esta corriendo, presione enter para detenerlo o escriba 'exit' y presione enter para salir");
            System.out.println("Para ejecutar la consulta escriba run-<ruta del sql> y presione enter");
            String line = "";
            while(true){
                line = scanner.nextLine();
                if (line.equals("") || line.equals("exit")){
                    System.out.println("Deteniendo el server");
                    break;
                }
                String ms[] = line.split("-");
                if(ms.length == 2){
                    subject.notifyObservers(ms[0],ms[1]);
                }
            }
            if (line.equals("") || line.equals("exit")){
                ftpServer.stop();
                communicator.shutdown();
            }
            communicator.waitForShutdown();
        }
    }
}
