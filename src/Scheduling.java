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


    public Scheduling(int deadline, int n_Cores, task [] t, CPU cpu) {
        this.deadline = deadline;
        this.n_Cores = n_Cores;
        this.t = t;
        this.cpu = cpu;
    }

    public void schedule() throws Exception {
        Sort();
        for (task task:t) {
            cpu.SetTaskOnCore(task.name,worseFitCoreSelector(),cpu.Endtime(worseFitCoreSelector()),
                    cpu.Endtime(worseFitCoreSelector())+task.runtime);
        }

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
