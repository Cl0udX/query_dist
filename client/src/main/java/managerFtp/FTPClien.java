package managerFtp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;

public class FTPClien {
    
    private FTPClient client;

    public FTPClien(String server, String user, String pass){
        System.out.println("Iniciando cliente ftp...");
        client = new FTPClient();
        try {
            client.connect(server, 21);
            client.login(user, pass);
            client.enterLocalPassiveMode();
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            System.out.println("Cliente FTP iniciado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(String remoteFilePath, String localFilePath) {
        try {
            File localFile = new File(localFilePath);
            if (!localFile.exists()) {
                System.out.println("El archivo no existe.");
            }else{
                System.out.println("Subiendo archivo...");
                InputStream inputStream = new FileInputStream(localFilePath);
                boolean done = client.storeFile(remoteFilePath, inputStream);
                if (done) {
                    System.out.println("El archivo se subió correctamente.");
                } else {
                    System.out.println("No se pudo subir el archivo.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(String remoteFilePath, String localFilePath) {
        try {
            File localFile = new File(localFilePath);
            if (!localFile.exists()) {
                localFile.createNewFile();
                
            }else{
                System.out.println("El archivo ya existe, se sobreescribirá.");
                localFile.delete();
                localFile.createNewFile();
            }
            OutputStream outputStream = new FileOutputStream(localFilePath);
            boolean success = client.retrieveFile(remoteFilePath, outputStream);
            if (success) {
                System.out.println("El archivo se descargó correctamente.");
            } else {
                System.out.println("No se pudo descargar el archivo.");
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (client.isConnected()) {
                client.logout();
                client.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
