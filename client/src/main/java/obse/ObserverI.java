package obse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zeroc.Ice.Current;

import FunctionsPoint.Observer;
import FunctionsPoint.SubjectPrx;
import manaegerSql.SqlClient;
import managerFtp.FTPClien;

public class ObserverI implements Observer {

    private SubjectPrx serverPrx;

    public ObserverI(SubjectPrx serverPrx) {
        this.serverPrx = serverPrx;
    }

    @Override
    public void update(String command,String msg, Current current){
        if (command.equals("run")){
            startProcess(msg);
        }
    }

    @Override
    public void shutdownObserver(Current current){
        System.out.println("Se desconecto el cliente del servidor");
        System.exit(0);
    }


    public String extractIPAddress(String input){
         Pattern pattern = Pattern.compile("-h\\s+(\\S+)");
         Matcher matcher = pattern.matcher(input);
         if (matcher.find()) {
             return matcher.group(1);
         }
         return null;
    }

    private void startProcess(String nameFile) {
        String ip = extractIPAddress(serverPrx.toString());
        System.out.println("Se ejecuto la consulta para el archivo: "+ nameFile);
        System.out.println("Server ip: "+ip);
        FTPClien clientFtp = new FTPClien(ip, "query_ftp", "query_ftp");
        clientFtp.downloadFile(nameFile, System.getProperty("user.dir")+ "/files/"+nameFile);
        SqlClient sqlClient = new SqlClient();
        sqlClient.executeQuery(System.getProperty("user.dir")+ "/files/"+nameFile);
        sqlClient.disconnect();
        clientFtp.uploadFile(ip+"-result.csv", System.getProperty("user.dir")+ "/files/result.csv");
        clientFtp.disconnect();
        serverPrx.addPartialResult(ip+"-result.csv");
    }
}