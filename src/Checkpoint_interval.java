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
import java.util.Set;

public class Checkpoint_interval {
    CPU cpu;
    private int number_of_fault;
    private int overhead;
    private task[] t;

    public Checkpoint_interval(CPU cpu, int number_of_fault, int overhead, task[] t) {
        this.cpu = cpu;
        this.number_of_fault = number_of_fault;
        this.overhead = overhead;
        this.t = t;
    }

    public int getInterval(){
        return ((cpu.getSmallestSlack()-(number_of_fault*overhead))/number_of_fault);
    }
}
