package manegerFtp;
import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;


public class FTPServer {

    private FtpServerFactory serverFactory;
    private ListenerFactory listenerFactory;
    private FtpServer server;
    private PropertiesUserManagerFactory userManagerFactory;
    private UserManager userManager;
    private BaseUser user;
    
    public FTPServer() throws FtpException{
        System.out.println("Iniciando servidor ftp...");
        serverFactory = new FtpServerFactory();

        userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
        userManager = userManagerFactory.createUserManager();

        user = new BaseUser();
        user.setName("query_ftp");
        user.setPassword("query_ftp");
        user.setHomeDirectory(System.getProperty("user.dir") + "/files");

        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);

        userManager.save(user);
        serverFactory.setUserManager(userManager);

        listenerFactory = new ListenerFactory();
        listenerFactory.setPort(21);
        serverFactory.addListener("default", listenerFactory.createListener());
        
        server = serverFactory.createServer();
        try {
            server.start();
            System.out.println("Servidor FTP iniciado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
