import java.util.Arrays;
import java.util.Set;

public class Scheduling {
    //Deadline of System
    private int deadline;
    //Number of Core in CPU
    private int n_Cores;
    //Task Set
    private task t[];
    //CPU
    CPU cpu;
    //Checkpoint Interval
    int interval;
    //Number of tolerable fault
    int tolerable_fault;
    //Checkpoint overhead
    int overhead;


    public Scheduling(int deadline, int n_Cores, task [] t, CPU cpu, int tolerable_fault,int overhead) {
        this.deadline = deadline;
        this.n_Cores = n_Cores;
        this.t = t;
        this.cpu = cpu;
        this.tolerable_fault=tolerable_fault;
        this.overhead=overhead;
    }

    public void schedule() throws Exception {
        Sort();
        for (task task:t) {
            cpu.SetTaskOnCore(task.name,worseFitCoreSelector(),cpu.Endtime(worseFitCoreSelector()),
                    cpu.Endtime(worseFitCoreSelector())+task.runtime);
        }
        Checkpoint_interval checkpoint_interval=new Checkpoint_interval(cpu,tolerable_fault,overhead,t);
        interval=checkpoint_interval.getInterval();

    }

    public void Sort() {
        Arrays.sort(t);
        //Collections.reverse(Arrays.asList(v));
    }

    public int worseFitCoreSelector(){
        int temp=0;
        int core=0;
        for (int i = 0; i < n_Cores; i++) {
            if(temp<cpu.Endtime(i)) {
                temp = cpu.Endtime(i);
                core = i;
            }
        }
        return core;
    }
}
