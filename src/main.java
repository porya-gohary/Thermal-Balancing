import jdk.internal.org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class main {
    public static void main(String args[]) throws Exception {

        int deadline = 900;
        int n_core = 8;
        int tolerable_fault=2;
        int overhead=7;
        int number_of_tasks=30;
        double utilization=7.0;
        int iteration= 10;


        int injected_fault=2;
        task t[];
        int interval=0;

        String benchmark[] = {"blackscholes","bodytrack","dedup1","dedup2","ferret1","ferret2","fluidanimate","x264","vips1","vips2"};
        int benchmark_time[] = {909, 591, 249, 875, 378, 270, 450, 510, 750, 596};

        for (int i = 0; i < iteration ; i++) {

            task_generator task_generator=new task_generator(utilization,number_of_tasks,n_core,benchmark,benchmark_time,tolerable_fault,overhead);
            task_generator.generate();
            deadline=task_generator.getDeadline();
            t= task_generator.getT().stream().toArray(task[]::new);
            CPU cpu=new CPU(deadline,n_core,task_generator.getT());
            Scheduling scheduling=new Scheduling(deadline,n_core,t,cpu,tolerable_fault,overhead);
            try {
                scheduling.schedule();
            }catch (Exception e){
                System.out.println("[INFEASEBLE]");
                i--;
                continue;
            }
            cpu.Save_Power("U "+utilization,"R"+i,"Main");
            Checkpoint_interval checkpoint_interval=new Checkpoint_interval(cpu,tolerable_fault,overhead,t);
            interval=checkpoint_interval.getInterval();
            System.out.println("Checkpoint Interval "+interval);
            if(interval<=0){
                i--;
                continue;
            }
            cpu.setInterval(interval);

            checkpoint_Insertion checkpoint_insertion=new checkpoint_Insertion(interval,cpu,injected_fault,overhead,t,tolerable_fault);
            try {
                checkpoint_insertion.insert();

            }catch (Exception e){
            System.out.println("[INFEASEBLE]");
            i--;
            continue;
        }
            cpu.Save_Power("U "+utilization,"R"+i,"Proposed_Method");
            //PPA-EDF
            PPA_EDF ppa_edf=new PPA_EDF(injected_fault,t,deadline,n_core);
            ppa_edf.Schedule();
            ppa_edf.cpu.Save_Power("U "+utilization,"R"+i,"PPA-EDF");

            File newFolder1 = new File("U "+utilization+"\\R"+i+"\\Main");
            newFolder1.mkdir();
            File newFolder2 = new File("U "+utilization+"\\R"+i+"\\Proposed_Method");
            newFolder2.mkdir();
            File newFolder3 = new File("U "+utilization+"\\R"+i+"\\PPA-EDF");
            newFolder3.mkdir();

            for (int j = 0; j < n_core; j++) {
                Path path = Files.move
                        (Paths.get("U "+utilization+"\\R"+i+"\\Main_Core_"+j+".txt"),
                                Paths.get("U "+utilization+"\\R"+i+"\\Main\\"+"core_"+j));
                path = Files.move
                        (Paths.get("U "+utilization+"\\R"+i+"\\Proposed_Method_Core_"+j+".txt"),
                                Paths.get("U "+utilization+"\\R"+i+"\\Proposed_Method\\"+"core_"+j));
                path = Files.move
                        (Paths.get("U "+utilization+"\\R"+i+"\\PPA-EDF_Core_"+j+".txt"),
                                Paths.get("U "+utilization+"\\R"+i+"\\PPA-EDF\\"+"core_"+j));


            }

        }

    }
}
