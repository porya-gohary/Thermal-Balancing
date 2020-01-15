import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/*******************************************************************************
 * Copyright (c) 2020 Pourya Gohari
 * Written by Porya Gohary (Email: gohary@ce.sharif.edu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
public class CPU {
    //Core of CPU   [#Core] [#Time]
    private String[][] core;
    //Power Trace of Cores
    private double[][] power;
    //Deadline of System
    private int deadline;
    //Number of Core in CPU
    private int n_Cores;
    //Idle Power
    double idle_power = 5.0;

    private Set<task> t;

    //Location of Power Trace
    String location = "Parsec\\";

    public CPU(int deadline, int n_Cores, Set<task> t) {
        this.deadline = deadline;
        this.n_Cores = n_Cores;

        core = new String[n_Cores][deadline];
        power = new double[n_Cores][deadline];
        //initial Power
        for (int i = 0; i < n_Cores; i++) {
            Arrays.fill(power[i], idle_power);
        }

        this.t = t;

    }

    //GET Running Task in specific Time
    public String getRunningTask(int Core, int Time) {
        return (core[Core][Time] == null) ? null : core[Core][Time].split(" R")[0];
    }

    //If Time slot was free return true;
    public boolean CheckTimeSlot(int Core, int Start, int End) {
        if (Core > (n_Cores - 1)) return false;
        if (Start > End) return false;
        if (Start < 0 || End >= deadline || Start >= deadline) return false;
        for (int i = Start; i <= End; i++) {
//            System.out.println("Check Time: "+Core+"  "+i);
            if (core[Core][i] != null) return false;
        }
        return true;
    }

    //Set Task to Core
    public void SetTaskOnCore(String Task, int Core, int Start, int End) throws Exception {
        // System.out.println(Task+"  "+ Start+"  "+End);
        try {
            for (int i = Start; i <= End; i++) {
                core[Core][i] = Task;
            }
        } catch (Exception e) {
            System.err.println(Task + "  ⚠ ⚠ Infeasible!");
            e.printStackTrace();
            throw new Exception("Infeasible!");
            //System.exit(1);
        }


        try {
            this.setPower(getTaskbyName(Task), Start, Core);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // A function For Mapping Power Of Benchmarks
    public void setPower(task Task, int Start, int Core) throws IOException {

        String name = Task.benchmark;
        Double r[] = new Double[Task.getRuntime()];
        BufferedReader reader;
        File file = new File(location + name + ".txt");
        reader = new BufferedReader(new FileReader(file));
        int i = 0;
        String line = reader.readLine();
//            System.out.println("P START  :: "+Start+"   "+(Start+r.length));
//            System.out.println("<POWER> "+v.getName()+"  "+v.getLO_name()+"   "+v.getWcet(0)+"    "+v.getMin_freq());
        while (line != null) {
            r[i] = Double.parseDouble(line);
            line = reader.readLine();
            i++;
        }
        int l = 0;
        for (int k = Start; k < Start + r.length; k++) {
            power[Core][k] = r[l];
            l++;
        }


    }

    public task getTaskbyName(String name) {
        Iterator<task> it = t.iterator();
        while (it.hasNext()) {
            task n = it.next();
            if (n.getName().equalsIgnoreCase(name))
                return n;
        }
        return null;
    }


}
