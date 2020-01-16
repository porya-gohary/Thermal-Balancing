import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class checkpoint_Insertion {
    //Checkpointing interval
    int interval;
    //CPU
    CPU cpu;
    //Number of Injected fault
    private int injected_fault;
    //Checkpointing overhead
    private int overhead;
    //Task Set
    private task[] t;

    //Last inserted checkpoint time
    int last_Checkpoint=0;

    private int number_of_fault;

    //Number of checkpoint
    int n_checkpoint;

    public checkpoint_Insertion(int interval, CPU cpu, int injected_fault, int overhead, task[] t, int number_of_fault) {
        this.interval = interval;
        this.cpu = cpu;
        this.injected_fault = injected_fault;
        this.overhead = overhead;
        this.t = t;
        this.number_of_fault = number_of_fault;
    }

    public void insert() throws Exception {
        double[] p;
        n_checkpoint=(cpu.getEndApplication()+(overhead*number_of_fault))/interval;
        System.out.println("[DEBUG]  Number of Checkpoint: "+n_checkpoint);
        for (int i = 0; i < n_checkpoint; i++) {
            p=cpu.averagePowerInInterval(last_Checkpoint,last_Checkpoint+interval);
            last_Checkpoint+=interval;

            for (int j = 0; j < (cpu.getN_Cores()/2); j++) {
                double min=50;
                int minindex=0;
                double max=-1;
                int maxindex=0;
                for (int k = 0; k <(cpu.getN_Cores()) ; k++) {
                    if(p[k]<min && p[k]!=-1){
                        minindex=k;
                        min=p[k];
                    }
                }

                for (int k = 0; k <(cpu.getN_Cores()) ; k++) {
                    if(p[k]>max && p[k]!=-1){
                        maxindex=k;
                        max=p[k];
                    }
                }
                System.out.println("DEBUG  "+p[maxindex]+"   "+p[minindex]);
                p[maxindex]=-1;
                p[minindex]=-1;

                cpu.taskSwap(maxindex,minindex,last_Checkpoint,last_Checkpoint+interval);

            }

            cpu.insert_checkpoint(overhead,last_Checkpoint);
            last_Checkpoint+=overhead;
        }
    }

}
