package obse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zeroc.Ice.Current;

import FunctionsPoint.ObserverPrx;
import FunctionsPoint.Subject;
import managerTask.ManagerTask;
import managerTask.Task;

public class ObseI implements Subject{

    private ArrayList<ObserverPrx> observers;
    private ManagerTask managerTask;

    public ObseI() {
        observers = new ArrayList<ObserverPrx>();
        managerTask = new ManagerTask();
    }

    public ArrayList<ObserverPrx> getObservers() {
        return observers;
    }

    @Override
    public void addObserver(ObserverPrx o, Current current){
        if (!observers.contains(o)){
            System.out.println("Nueva conexion: "+o.toString());
            System.out.println("Numero de Clientes: "+(observers.size() + 1));
            observers.add(o);
        }
    }
    
    @Override
    public void removeObserver(ObserverPrx o, Current current){
        observers.remove(o);
    }

    @Override
    public void getTask(Current current){
    }

    @Override
    public void addPartialResult(String fileNameResult, Current current){
        String ip = fileNameResult.split("-")[0];
        System.out.println(ip+" respondio");
        managerTask.addTaskDone(new Task(ip));
        if (managerTask.getTasks().size() == managerTask.getTasksDone().size()){
            System.out.println("Todos los clientes respondieron");
            combineResults();
            for(ObserverPrx o : observers){
                new Thread(() -> {
                    o.shutdownObserver();
                }).start();
            }
            observers.clear();
            managerTask.clearAll();
            // System.exit(0);
        }
    }

    private void combineResults() {
        System.out.println("Combinando resultados");
        ArrayList<Task> tasks = managerTask.getTasksDone();
        String outputFile = System.getProperty("user.dir")+ "/files/result.csv";
        String result = "";
        try {
            CSVWriter writer = new CSVWriter(new java.io.FileWriter(outputFile));
            boolean first = true;
            for (Task task : tasks){
                result = task.getInfo() + "-result.csv";
                CSVReader reader = new CSVReader(new java.io.FileReader(System.getProperty("user.dir")+ "/files/"+result));
                List<String[]> allRows = reader.readAll();
                if (!first){
                    allRows.remove(0);
                    first = false;
                }
                writer.writeAll(allRows);
            }
            writer.close();
            System.out.println("Resultados combinados");
        } catch (Exception e) {
            System.out.println("Error al combinar los resultados" + e.getMessage());
        }
    }

    public void notifyObservers(String command, String message){
        if (command.equals("run")){
            for(ObserverPrx o : observers){
                if (verifyObserver(o)){
                    Task task = new Task(extractIPAddresses(o.ice_getConnection().toString())[0]);
                    managerTask.addTask(task);
                }
            }
            for(ObserverPrx o : observers){
                if (verifyObserver(o)){
                    String ip = extractIPAddresses(o.ice_getConnection().toString())[0];
                    new Thread(() -> {
                        o.update(command, ip+'-'+message);
                    }).start();
                }
            }
        }
    }

    public boolean verifyObserver(ObserverPrx o){
        try{
            o.ice_ping();
            return true;
        }catch(Exception e){
            removeObserver(o, null);
            return false;
        }
    }

    /**
     * Extrae las direcciones IP de una cadena que contiene información de conexión Ice.
     * La cadena puede tener el formato "local address = IP:puerto\nremote address = IP:puerto"
     * o "remote address = IP:puerto\nlocal address = IP:puerto".
     *
     * @param input La cadena que contiene la información de conexión Ice.
     * @return Un arreglo de strings que contiene las direcciones IP local y remota, respectivamente en un arreglo.
    */
    public static String[] extractIPAddresses(String input) {
        String[] ips = new String[2];
        Pattern pattern = Pattern.compile("(local address|remote address) = (\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):\\d+");
        Matcher matcher = pattern.matcher(input);
        int index = 0;
        while (matcher.find() && index < 2) {
            ips[index++] = matcher.group(2);
        }
        return ips;
    }
}
