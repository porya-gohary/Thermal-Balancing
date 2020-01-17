import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PPA_EDF {

    //CPU
    CPU cpu;
    //Number of Injected fault
    private int injected_fault;
    //Task Set
    private task[] t;
    private int deadline;
    private int n_Cores;

    public PPA_EDF(int injected_fault, task[] t, int deadline, int n_Cores) {
        this.injected_fault = injected_fault;
        this.t = t;
        this.deadline = deadline;
        this.n_Cores = n_Cores;
        Set<task> set =new HashSet<>(Arrays.stream(t).collect(Collectors.toSet()));
        cpu =new CPU(deadline,n_Cores,set);
    }

    public void Schedule() throws Exception {
        Sort();
        for (int i = 0; i < t.length/2; i++) {
            cpu.SetTaskOnCore(t[i].name,worseFitCoreSelector(false),cpu.Endtime(worseFitCoreSelector(false)),
                    cpu.Endtime(worseFitCoreSelector(false))+t[i].runtime);
        }
        for (int i = 0; i <n_Cores/2; i++) {
            int temp=deadline-1;
            int cT=n_Cores-1-i;
            for (int j = 0; j < cpu.Endtime(i); j++) {
                cpu.core[cT][temp]=cpu.core[i][j];
                cpu.power[cT][temp]=cpu.power[i][j];
                temp--;
            }
        }
    }

    public void Sort() {
        Arrays.sort(t);
        //Collections.reverse(Arrays.asList(v));
    }


    public int worseFitCoreSelector(boolean spare){
        int temp=deadline;
        int core=0;
        if(!spare) {
            for (int i = 0; i < n_Cores / 2; i++) {
                if (temp > cpu.Endtime(i)) {
                    temp = cpu.Endtime(i);
                    core = i;
                }
            }
        }else{
            for (int i = n_Cores / 2; i < n_Cores; i++) {
                if (temp > cpu.Endtime(i)) {
                    temp = cpu.Endtime(i);
                    core = i;
                }
            }
        }
        return core;
    }
}
