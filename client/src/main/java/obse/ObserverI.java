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
    private String ipLocal;

    public ObserverI(SubjectPrx serverPrx) {
        this.serverPrx = serverPrx;
    }

    @Override
    public void update(String command,String msg, Current current){
        if (command.equals("run")){
            String concat[] = msg.split("-");
            if (concat.length == 2){
                ipLocal = concat[0];
                startProcess(concat[1]);
            }
        }
    }

    @Override
    public void shutdownObserver(Current current){
        System.out.println("Se desconecto el cliente del servidor");
        System.exit(0);
    }


    /**
     * Extrae las direcciones IP de una cadena que contiene información de conexión Ice.
     * La cadena debe tener el formato "local address = IP:puerto\nremote address = IP:puerto".
     *
     * @param input La cadena que contiene la información de conexión Ice.
     * @return Un arreglo de strings que contiene las direcciones IP local y remota, respectivamente en un arreglo.
     */
    public String[] extractIPAddress(String input){
        String[] ips = new String[2];
        Pattern pattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):\\d+");
        Matcher matcher = pattern.matcher(input);
        int index = 0;
        while (matcher.find() && index < 2) {
            ips[index++] = matcher.group(1);
        }
        return ips;
    }

    private void startProcess(String nameFile) {
        String ip = extractIPAddress(serverPrx.ice_getConnection().toString())[0];
        System.out.println("Se ejecuto la consulta para el archivo: "+ nameFile);
        System.out.println("Server ip: "+ip);
        System.out.println("Client ip: "+ipLocal);
        FTPClien clientFtp = new FTPClien(ip, "query_ftp", "query_ftp");
        clientFtp.downloadFile(nameFile, System.getProperty("user.dir")+ "/files/"+nameFile);
        SqlClient sqlClient = new SqlClient();
        sqlClient.executeQuery(System.getProperty("user.dir")+ "/files/"+nameFile);
        sqlClient.disconnect();
        clientFtp.uploadFile(ipLocal+"-result.csv", System.getProperty("user.dir")+ "/files/result.csv");
        clientFtp.disconnect();
        serverPrx.addPartialResult(ipLocal+"-result.csv");
    }
}
