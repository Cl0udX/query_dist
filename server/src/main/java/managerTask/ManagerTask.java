package managerTask;
import java.util.*;

public class ManagerTask {
    private ArrayList<Task> tasks;
    private ArrayList<Task> tasksDone;

    public ManagerTask() {
        tasks = new ArrayList<Task>();
        tasksDone = new ArrayList<Task>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void addTaskDone(Task task) {
        tasksDone.add(task);
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public ArrayList<Task> getTasksDone() {
        return tasksDone;
    }

    public void clearAll() {
        tasks.clear();
        tasksDone.clear();
    }
    
}
