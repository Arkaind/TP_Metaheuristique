package jobshop.solvers;

import java.util.ArrayList;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.encodings.JobNumbers;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.HashSet;

public class GreedySolver implements Solver {

    ResourceOrder rso;

    int priorityRule = -1;

    /* priorité 1 = SPT
    priorité 2 = LPT
    priorité 3 = SRPT
    priorité 4 = LRPT
    priorité 5 = EST_SPT
    priorité 6 = EST_LRPT
    */

    public void setPriority(int prio) {
        priorityRule = prio;
    }

    public int getPriority(){
        return priorityRule;
    }

    @Override
    public Result solve(Instance instance, long deadline) {

        if(this.priorityRule == -1){
            this.priorityRule = 6;
        }

        ArrayList<Task> al_Task_Realisables = new ArrayList();

        deadline = deadline + System.currentTimeMillis();

        ResourceOrder rso = new ResourceOrder(instance);

        for (int j = 0; j < instance.numJobs; j++) {
            al_Task_Realisables.add(new Task(j, 0));
        }

        if (priorityRule == 1) {

            while (al_Task_Realisables.size() > 0 && (deadline - System.currentTimeMillis() > 1)) {

                int index_shortest = 0;

                for (int k = 0; k < al_Task_Realisables.size(); k++) {

                    Task current = al_Task_Realisables.get(k);

                    Task shortest = al_Task_Realisables.get(index_shortest);

                    if (instance.duration(shortest) > instance.duration(current)) {
                        index_shortest = k;
                    }
                }

                Task shortest = al_Task_Realisables.get(index_shortest);

                int job = shortest.job;
                int task = shortest.task;

                int resource = instance.machine(job, task);

                rso.addTask(resource, job, task);

                al_Task_Realisables.remove(index_shortest);

                if (instance.numTasks - 1 > task) {
                    al_Task_Realisables.add(new Task(job, task + 1));
                }


            }

        } else if (priorityRule == 2) {

            while (al_Task_Realisables.size() > 0 && (deadline - System.currentTimeMillis() > 1)) {

                int index_shortest = 0;

                for (int k = 0; k < al_Task_Realisables.size(); k++) {

                    Task current = al_Task_Realisables.get(k);

                    Task shortest = al_Task_Realisables.get(index_shortest);

                    if (instance.duration(shortest) < instance.duration(current)) {
                        index_shortest = k;
                    }
                }

                Task shortest = al_Task_Realisables.get(index_shortest);

                int job = shortest.job;
                int task = shortest.task;

                int resource = instance.machine(job, task);

                rso.addTask(resource, job, task);

                al_Task_Realisables.remove(index_shortest);

                if (instance.numTasks - 1 > task) {
                    al_Task_Realisables.add(new Task(job, task + 1));
                }

            }

        } else if (priorityRule == 3) {

            int timeToEnd[] = new int[instance.numJobs];

            for (int i = 0; i < instance.numJobs; i++) {
                timeToEnd[i] = 0;
                for (int j = 0; j < instance.numTasks; j++) {
                    timeToEnd[i] += instance.duration(i, j);
                }
            }

            while (al_Task_Realisables.size() > 0 && (deadline - System.currentTimeMillis() > 1)) {

                int index_shortest = 0;

                for (int k = 0; k < al_Task_Realisables.size(); k++) {

                    Task current = al_Task_Realisables.get(k);
                    int jobCurrent = current.job;

                    Task shortest = al_Task_Realisables.get(index_shortest);
                    int jobShortest = shortest.job;

                    if (timeToEnd[jobCurrent] < timeToEnd[jobShortest]) {
                        index_shortest = k;
                    }
                }

                Task shortest = al_Task_Realisables.get(index_shortest);

                int job = shortest.job;
                int task = shortest.task;

                int resource = instance.machine(job, task);

                rso.addTask(resource, job, task);

                al_Task_Realisables.remove(index_shortest);
                timeToEnd[job] -= instance.duration(job, task);


                if (instance.numTasks - 1 > task) {
                    al_Task_Realisables.add(new Task(job, task + 1));
                }

            }

        } else if (priorityRule == 4) {

            int timeToEnd[] = new int[instance.numJobs];

            for (int i = 0; i < instance.numJobs; i++) {
                timeToEnd[i] = 0;
                for (int j = 0; j < instance.numTasks; j++) {
                    timeToEnd[i] += instance.duration(i, j);
                }
            }

            while (al_Task_Realisables.size() > 0 && (deadline - System.currentTimeMillis() > 1)) {

                int index_shortest = 0;

                for (int k = 0; k < al_Task_Realisables.size(); k++) {

                    Task current = al_Task_Realisables.get(k);
                    int jobCurrent = current.job;

                    Task shortest = al_Task_Realisables.get(index_shortest);
                    int jobShortest = shortest.job;

                    if (timeToEnd[jobCurrent] > timeToEnd[jobShortest]) {
                        index_shortest = k;
                    }
                }

                Task shortest = al_Task_Realisables.get(index_shortest);

                int job = shortest.job;
                int task = shortest.task;

                int resource = instance.machine(job, task);

                rso.addTask(resource, job, task);

                al_Task_Realisables.remove(index_shortest);
                timeToEnd[job] -= instance.duration(job, task);

                if (instance.numTasks - 1 > task) {
                    al_Task_Realisables.add(new Task(job, task + 1));
                }

            }

        } else if (priorityRule == 5) {

            int startTimes[] = new int[instance.numJobs];

            for (int i = 0; i < instance.numJobs; i++) {
                startTimes[i] = 0;
            }

            int machineTime [] = new int[instance.numMachines];
            Arrays.fill(machineTime,0);

            while (al_Task_Realisables.size() > 0 && (deadline - System.currentTimeMillis() > 1)) {

                int startTime;
                int best_startTime = -1;

                Task next = null;

                int duration = 0;

                ArrayList<Task> taskTemporaire = new ArrayList<Task>();

                for (int k = 0; k < al_Task_Realisables.size(); k++) {

                    Task current = al_Task_Realisables.get(k);
                    int currentJob = current.job;
                    int currentTask = current.task;

                    if(best_startTime==-1){
                        best_startTime = Math.max(startTimes[currentJob],machineTime[instance.machine(current)]);
                        taskTemporaire.add(current);

                    }else{
                        startTime = Math.max(startTimes[currentJob],machineTime[instance.machine(current)]);
                        if(startTime == best_startTime){
                            taskTemporaire.add(current);
                        }
                        if(startTime < best_startTime){
                            taskTemporaire.clear();
                            taskTemporaire.add(current);
                            best_startTime = startTime;
                        }
                    }
                }

                int shortestDuration = -1;

                for (int k = 0; k < taskTemporaire.size(); k++) {

                    Task current = taskTemporaire.get(k);
                    int currentJob = current.job;
                    int currentTask = current.task;

                    if(shortestDuration == -1){
                        shortestDuration = instance.duration(current);
                        next = current;
                    }else{
                        duration = instance.duration(current);
                        if(duration < shortestDuration){
                            next = current;
                            shortestDuration = duration;
                        }
                    }

                }

                int job = next.job;
                int task = next.task;


                machineTime[instance.machine(next)] = best_startTime + instance.duration(next);
                al_Task_Realisables.remove(next);

                if (task != (instance.numTasks-1)) {
                    startTimes[job] = best_startTime + instance.duration(job,task);
                    al_Task_Realisables.add(new Task(job, task + 1));
                }

                rso.addTask(instance.machine(job,task), job, task);


            }

        }else if (priorityRule == 6) {

            int startTimes[] = new int[instance.numJobs];

            int timeToEnd[] = new int[instance.numJobs];

            for (int i = 0; i < instance.numJobs; i++) {
                startTimes[i] = 0;
            }


            for (int i = 0; i < instance.numJobs; i++) {
                timeToEnd[i] = 0;
                for (int j = 0; j < instance.numTasks; j++) {
                    timeToEnd[i] += instance.duration(i, j);
                }
            }

            int machineTime [] = new int[instance.numMachines];
            Arrays.fill(machineTime,0);


            while (!al_Task_Realisables.isEmpty() && (deadline - System.currentTimeMillis() > 1)) {

                int startTime;
                int best_startTime = -1;

                Task next = null;

                int duration = 0;

                ArrayList<Task> taskTemporaire = new ArrayList<Task>();

                for (int k = 0; k < al_Task_Realisables.size(); k++) {

                    Task current = al_Task_Realisables.get(k);
                    int currentJob = current.job;
                    int currentTask = current.task;

                    if(best_startTime==-1){
                        best_startTime = Math.max(startTimes[currentJob],machineTime[instance.machine(current)]);
                        taskTemporaire.add(current);

                    }else{
                        startTime = Math.max(startTimes[currentJob],machineTime[instance.machine(current)]);
                        if(startTime == best_startTime){
                            taskTemporaire.add(current);
                        }
                        if(startTime < best_startTime){
                            taskTemporaire.clear();
                            taskTemporaire.add(current);
                            best_startTime = startTime;
                        }
                    }
                }

                for (int k = 0; k < taskTemporaire.size(); k++) {
                    Task current = taskTemporaire.get(k);
                    int currentJob = current.job;
                    int currentTask = current.task;

                    if(next == null){
                        next = current;
                        duration = timeToEnd[currentJob];
                    }else if(duration < timeToEnd[currentJob]){
                        next = current;
                        duration = timeToEnd[currentJob];
                    }
                }


                int job = next.job;
                int task = next.task;

                timeToEnd[job] -= instance.duration(job,task);
                machineTime[instance.machine(next)] = best_startTime + instance.duration(next);
                al_Task_Realisables.remove(next);

                if (task != (instance.numTasks-1)) {
                    startTimes[job] = best_startTime + instance.duration(job,task);
                    al_Task_Realisables.add(new Task(job, task + 1));
                }

                rso.addTask(instance.machine(job,task), job, task);

            }
        }

        return new Result(instance, rso.toSchedule(), Result.ExitCause.Blocked);
    }

}
