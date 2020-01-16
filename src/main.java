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
        double utilization= 3.8;
        task t[];
        //power trace path
        String path="Tasks\\";
        String benchmark[] = {"blackscholes","bodytrack","dedup1","dedup2","ferret1","ferret2","fluidanimate","x264","vips1","vips2"};
        int benchmark_time[] = {909, 591, 249, 875, 378, 270, 450, 510, 750, 596};


        task_generator task_generator=new task_generator(utilization,number_of_tasks,n_core,benchmark,benchmark_time);
        task_generator.generate();
        deadline=task_generator.getDeadline();
        t= task_generator.getT().stream().toArray(task[]::new);
        CPU cpu=new CPU(deadline,n_core,task_generator.getT());
        Scheduling scheduling=new Scheduling(deadline,n_core,t,cpu,tolerable_fault,overhead);
        scheduling.schedule();




    }
}
