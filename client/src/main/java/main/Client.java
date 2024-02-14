package main;
import java.util.Properties;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import FunctionsPoint.ObserverPrx;
import FunctionsPoint.SubjectPrx;
import obse.ObserverI;

public class Client {

    public static final Properties properties = new Properties();  

    public static void main(String[] args) throws Exception {
        String host = null;
        String environment = null;
        for (int i = 0; i < args.length; i++) {
            if ("-h".equals(args[i]) && i + 1 < args.length) {
                host = args[i + 1];
            }
            if ("-d".equals(args[i]) && i + 1 < args.length) {
                environment = args[i + 1];
            }
        }
        if (host == null) {
            System.err.println("No se especifico el host se usara localhost por defecto");
        }else{
            System.out.println("Host: " + host);
        }
        if (environment == null) {
            System.err.println("No se especifico el ambiente se usara prod por defecto");
            environment = "prod";
        }else{
            if (!environment.equals("prod") && !environment.equals("dev")) {
                System.err.println("Ambiente no valido se usara prod por defecto");
                environment = "prod";
            }else{
                System.out.println("Ambiente: " + environment);
            }      
        }
        properties.load(Client.class.getResourceAsStream("/"+environment+"-properties.cfg"));
        try(Communicator communicator = Util.initialize(args, (environment+"-properties.cfg"))){
            ObjectPrx baseSubject;
            if (host != null) {
               baseSubject = communicator.stringToProxy("subject:default -h " + host + " -p 25567");
            }else{
                baseSubject = communicator.propertyToProxy("subject.proxy");
            }
            SubjectPrx subject = SubjectPrx.checkedCast(baseSubject);

            ObjectAdapter adapter = communicator.createObjectAdapter("observer");
            ObserverI observer = new ObserverI(subject);
            ObjectPrx obsPrx = adapter.add(observer, Util.stringToIdentity("notNecessary"));
            adapter.activate();
            ObserverPrx observerPrx = ObserverPrx.checkedCast(obsPrx);

            if(subject == null){
                throw new Error("Invalid proxy");
            }
            subject.addObserver(observerPrx);

            communicator.waitForShutdown();
        }
    }
}
