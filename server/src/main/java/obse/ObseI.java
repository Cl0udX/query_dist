package obse;

import FunctionsPoint.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zeroc.Ice.Current;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ftpserver.command.impl.listing.ListArgument;

import managerTask.*;

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
                o.shutdownObserver();
            }
            observers.clear();
            managerTask.clearAll();
            System.exit(0);
        }
    }

    private void combineResults() {
        System.out.println("Combinando resultados");
        ArrayList<Task> tasks = managerTask.getTasksDone();
        String outputFile = "result.csv";
        String result = "";
        try {
            CSVWriter writer = new CSVWriter(new java.io.FileWriter(outputFile));
            boolean first = true;
            for (Task task : tasks){
                result += task.getInfo() + "-result.csv";
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
                    Task task = new Task(extractIPAddress(o.toString()));
                    managerTask.addTask(task);
                }
            }
            for(ObserverPrx o : observers){
                if (verifyObserver(o)){
                    o.update(command, message);
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

    public String extractIPAddress(String input){
         Pattern pattern = Pattern.compile("-h\\s+(\\S+)");
         Matcher matcher = pattern.matcher(input);
         if (matcher.find()) {
             return matcher.group(1);
         }
         return null;
    }
}
