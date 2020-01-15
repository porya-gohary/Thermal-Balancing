import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class task_generator {

    private double utilization;
    private int deadline;
    private int number_of_tasks;
    private int n_core;
    private Set<task> t;
    private String[] benchmark;
    private int[] benchmark_time;

    public task_generator(double utilization, int number_of_tasks, int n_core, String[] benchmark, int[] benchmark_time) {
        this.utilization = utilization;
        this.number_of_tasks = number_of_tasks;
        this.n_core = n_core;
        this.benchmark = benchmark;
        this.benchmark_time = benchmark_time;
        t = new HashSet<task>();

    }

    public void generate(){
        Random rn= new Random();
        for (int i = 0; i < number_of_tasks; i++) {
            //Random Mapping Benchmark to Tasks
            int random=0;
            random=rn.nextInt(benchmark.length);
            task temp_task=new task("T"+i);
            temp_task.setBenchmark(benchmark[random]);
            temp_task.setRuntime(benchmark_time[random]);
            t.add(temp_task);

        }
        int total_exec_time=0;
        for (task a: t) {
            total_exec_time+=a.getRuntime();
        }
        deadline=(int)((double)total_exec_time/(utilization/n_core));
        System.out.println("Deadline= "+deadline);
        System.out.println("Total Exec. Time="+ total_exec_time);

    }

    public double getUtilization() {
        return utilization;
    }

    public void setUtilization(double utilization) {
        this.utilization = utilization;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public int getNumber_of_tasks() {
        return number_of_tasks;
    }

    public void setNumber_of_tasks(int number_of_tasks) {
        this.number_of_tasks = number_of_tasks;
    }

    public int getN_core() {
        return n_core;
    }

    public void setN_core(int n_core) {
        this.n_core = n_core;
    }

    public Set<task> getT() {
        return t;
    }

    public void setT(Set<task> t) {
        this.t = t;
    }
}
