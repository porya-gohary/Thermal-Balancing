import java.io.*;
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
    double idle_power = 3.0;

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
         System.out.println(Task+"  "+ Start+"  "+End+"   Core: "+Core);
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

    //a function for determine end time in each core
    public int Endtime(int core) {
        for (int i = deadline - 1; i >= 0; i--) {
            if (this.getRunningTask(core, i) != null) {
                return i;
            }
        }
        return 0;
    }

    public int getSlack(int core) {
        return (deadline - Endtime(core));
    }

    public int getSmallestSlack() {
        int temp = deadline;
        for (int i = 0; i < n_Cores; i++) {
            if (getSlack(i) < temp) temp = getSlack(i);
        }
        return temp;
    }

    public int getEndApplication(){
        int temp = 0;
        for (int i = 0; i < n_Cores; i++) {
           if(Endtime(i)>temp)temp=Endtime(i);
        }
        return temp;
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

    //Write Scheduling In File for Debugging
    public void debug(String Filename) throws IOException {
        BufferedWriter outputWriter = null;
        outputWriter = new BufferedWriter(new FileWriter(Filename + ".csv"));
        for (int i = 0; i < getN_Cores(); i++) {
            for (int j = 0; j < getDeadline(); j++) {
                outputWriter.write(core[i][j] + ",");
            }
            ;
            outputWriter.write("\n");
        }
        outputWriter.flush();
        outputWriter.close();
    }

    public void insert_checkpoint(int overhead, int start) throws Exception {
        Task_Shifter(start, overhead);
        for (int j = 0; j < n_Cores; j++) {
            for (int i = start; i < start + overhead; i++) {
                core[j][i]="CHK";
                power[j][i]=idle_power;
            }
        }
    }

    public void Task_Shifter(int shiftTime, int amount) throws Exception {
        // System.out.println("TASK SHIFTER  "+ shiftTime+"  > > "+amount);
        for (int i = 0; i < n_Cores; i++) {
            for (int j = Endtime(i); j > (shiftTime); j--) {
                try {
                    this.SetTask(i, j + amount, this.getRunningTask(i, j));
                    power[i][j + amount] = power[i][j];
                } catch (Exception ex) {
                    System.err.println(this.getRunningTask(i, j) + "  ⚠ ⚠ Infeasible!");
                    throw new Exception("Infeasible!");
                    // System.exit(1);
                }
            }
            for (int j = shiftTime + 1; j < shiftTime + amount + 1; j++) {
                this.SetTask(i, j, null);
                power[i][j] = 0.5;
            }
        }
    }

    public void SetTask(int core_number, int time, String task) throws Exception {
        try {
            core[core_number][time] = task;

        } catch (Exception e) {
            //System.err.println(task+"  ⚠ ⚠ Infeasible!");
            e.printStackTrace();
            //System.out.println("Core  "+core_number+"  Time "+time);
            throw new Exception("Infeasible!");
            //System.exit(1);
        }
    }

    //Return End Time of a Specific Replica of Tasks
    public int getEndTimeTask(String Task) {
        int e = -1;
        //System.out.println(Task);
        for (int i = 0; i < n_Cores; i++) {
            if (Arrays.asList(core[i]).lastIndexOf(Task) != -1) {
                if (Arrays.asList(core[i]).lastIndexOf(Task) > e) {
                    e = Arrays.asList(core[i]).lastIndexOf(Task);
                }
            }
        }
        //   System.out.println("   >>> "+e);
        return e;
    }

    public void Save_Power(String mFolder, String Folder, String Filename) throws IOException {
        BufferedWriter outputWriter = null;
        File newFolder2 = new File(mFolder);
        newFolder2.mkdir();
        File newFolder = new File(mFolder + "\\" + Folder);
        newFolder.mkdir();
        for (int i = 0; i < getN_Cores(); i++) {
            outputWriter = new BufferedWriter(new FileWriter(mFolder + "\\" + Folder + "\\" + Filename + "_Core_" + i + ".txt"));
            for (int j = 0; j < getDeadline(); j++) {
                outputWriter.write(power[i][j] + "\n");
            }
            ;
            outputWriter.flush();
            outputWriter.close();

        }
    }

    //Calculate Average Power Consumption of CPU in a Specific Interval
    public double[] averagePowerInInterval(int start, int end) {
        double[] p = new double[n_Cores];
        for (int i = 0; i < n_Cores; i++) {
            for (int j = start; j <= end; j++) {
                p[i] += power[i][j];
            }
            p[i] /= deadline;

        }
        return p;
    }

    //A Simple Function for Swap tasks in a specific interval
    public void taskSwap(int core1, int core2, int start, int end) {
        System.out.println("[DEBUG]   "+start+"   "+end);
        double[] p = new double[end - start];
        String[] s = new String[end - start];

        int temp = 0;
        for (int i = start; i < end; i++) {
            p[temp] = power[core1][i];
            s[temp] = core[core1][i];
            temp++;
        }
        temp = 0;
        for (int i = start; i < end; i++) {
            power[core1][i] = power[core2][i];
            core[core1][i] = core[core2][i];

            power[core2][i] = p[temp];
            core[core2][i] = s[temp];
            temp++;
        }
        System.out.println("Time: "+ start +"  Core1=> "+core1+" Core2=> "+core2);
    }

    public void setN_Cores(int n_Cores) {
        this.n_Cores = n_Cores;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public int getN_Cores() {
        return n_Cores;
    }

    public int getDeadline() {
        return deadline;
    }


}
