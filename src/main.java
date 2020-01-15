import jdk.internal.org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class main {
    public static void main(String args[]) throws IOException, SAXException, ParserConfigurationException {

        int deadline = 900;
        int n_core = 4;
        int number_of_tasks=10;
        double utilization= 3.8;
        //power trace path
        String path="Tasks\\";
        String benchmark[] = {"Basicmath", "Bitcount", "Dijkstra", "FFT", "JPEG", "Patricia", "Qsort", "Sha", "Stringsearch", "Susan"};
        int benchmark_time[] = {156, 25, 33, 160, 28, 87, 25, 13, 8, 20};

        task_generator task_generator=new task_generator(utilization,number_of_tasks,n_core,benchmark,benchmark_time);
        task_generator.generate();


    }
}
