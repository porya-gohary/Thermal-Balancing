import jdk.internal.org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class main {
    public static void main(String args[]) throws Exception {

        int deadline = 900;
        int n_core = 4;
        int tolerable_fault=2;
        int overhead=10;
        int number_of_tasks=10;
        double utilization= 2.8;

        int injected_fault=2;
        task t[];
        int interval=0;

        String benchmark[] = {"blackscholes","bodytrack","dedup1","dedup2","ferret1","ferret2","fluidanimate","x264","vips1","vips2"};
        int benchmark_time[] = {909, 591, 249, 875, 378, 270, 450, 510, 750, 596};


        task_generator task_generator=new task_generator(utilization,number_of_tasks,n_core,benchmark,benchmark_time,tolerable_fault,overhead);
        task_generator.generate();
        deadline=task_generator.getDeadline();
        t= task_generator.getT().stream().toArray(task[]::new);
        CPU cpu=new CPU(deadline,n_core,task_generator.getT());
        Scheduling scheduling=new Scheduling(deadline,n_core,t,cpu,tolerable_fault,overhead);
        scheduling.schedule();

        Checkpoint_interval checkpoint_interval=new Checkpoint_interval(cpu,tolerable_fault,overhead,t);
        interval=checkpoint_interval.getInterval();
        System.out.println("Checkpoint Interval "+interval);

        checkpoint_Insertion checkpoint_insertion=new checkpoint_Insertion(interval,cpu,injected_fault,overhead,t,tolerable_fault);
        checkpoint_insertion.insert();




    }
}
