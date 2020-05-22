package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.awt.event.WindowStateListener;
import java.lang.reflect.Array;
import java.time.format.ResolverStyle;
import java.util.*;

public class DescentSolver implements Solver {

    GreedySolver solverGreedy = new GreedySolver();


    static class Block {

        final int machine;

        final int firstTask;

        final int lastTask;

        Block(int machine, int firstTask, int lastTask) {
            this.machine = machine;
            this.firstTask = firstTask;
            this.lastTask = lastTask;
        }
    }

    static class Swap {
        final int machine;
        final int t1;
        final int t2;

        Swap(int machine, int t1, int t2) {
            this.machine = machine;
            this.t1 = t1;
            this.t2 = t2;
        }

        public void applyOn(ResourceOrder order) {

            int swapTask1 = this.t1;
            int swapTask2 = this.t2;
            int machine = this.machine;


            Task temp = order.tasksByMachine[machine][swapTask1];
            order.tasksByMachine[machine][swapTask1] = order.tasksByMachine[machine][swapTask2];
            order.tasksByMachine[machine][swapTask2] = temp;

        }
    }


    public void setGreedyPrio(int i){
        solverGreedy.setPriority(i);
    }


    @Override
    public Result solve(Instance instance, long deadline) {

        deadline = deadline + System.currentTimeMillis();

        if(solverGreedy.getPriority() == -1){
            this.setGreedyPrio(6);
        }

        Result r = solverGreedy.solve(instance, deadline);

        ResourceOrder rso = new ResourceOrder(r.schedule);

        ResourceOrder optimalRso = rso.copy();
        int optimalMakespan = optimalRso.toSchedule().makespan();

        boolean newVoisin = true;

        while (newVoisin == true && (deadline - System.currentTimeMillis() > 1)) {

            newVoisin=false;

            ResourceOrder temp = optimalRso.copy();


            List<Swap> alSwap = new ArrayList<>();
            List<Block> alBlock = this.blocksOfCriticalPath(temp);
            for (int i = 0; i < alBlock.size(); i++) {
                alSwap.addAll(this.neighbors(alBlock.get(i)));
            }

            for (int i = 0; i < alSwap.size(); i++) {

                ResourceOrder current = temp.copy();
                Schedule sch = current.toSchedule();
                alSwap.get(i).applyOn(current);
                Schedule newSch = current.toSchedule();

                if(newSch != null){

                    int makespanCurrent = newSch.makespan();

                    if (optimalMakespan > makespanCurrent) {
                        optimalRso = current;
                        optimalMakespan = makespanCurrent;
                        newVoisin=true;
                    }
                }


            }
        }

        return new Result(instance, optimalRso.toSchedule(), Result.ExitCause.Blocked);
    }

    int indexTaskOnMachine(ResourceOrder rso, int machine, Task taskObj) {

        int indexTask = -1;

        for (int i = 0; i < rso.instance.numJobs; i++) {
            if (rso.tasksByMachine[machine][i].equals(taskObj)) {
                indexTask = i;
            }
        }

        return indexTask;

    }

    List<Block> blocksOfCriticalPath(ResourceOrder order) {

        Schedule sch = order.toSchedule();

        List<Task> listTask = sch.criticalPath();

        ArrayList<Integer> alMachines = new ArrayList<Integer>();

        int indexDebut = -1;
        int indexFin = -1;
        int currentMachine = -1;

        ArrayList<Block> alBlock = new ArrayList<>();

        for (int j = 0; j < listTask.size() - 1; j++) {
            int task = listTask.get(j).task;
            int job = listTask.get(j).job;
            int machine = order.instance.machine(job, task);
            int indexTask = indexTaskOnMachine(order, machine, listTask.get(j));

            if (currentMachine == -1 || currentMachine != machine) {
                currentMachine = machine;
                indexDebut = indexTask;
            }

            int nextTask = listTask.get(j + 1).task;
            int nextJob = listTask.get(j + 1).job;
            int nextMachine = order.instance.machine(nextJob, nextTask);
            int nextIndexTask = indexTaskOnMachine(order, nextMachine, listTask.get(j + 1));

            if (nextMachine != machine) {
                indexFin = indexTask;
                if (indexDebut != indexFin) {
                    alBlock.add(new Block(machine, indexDebut, indexFin));
                }
            } else if (j == listTask.size() - 2) {
                indexFin = nextIndexTask;
                if (indexDebut != indexFin) {
                    alBlock.add(new Block(machine, indexDebut, indexFin));
                }
            }

        }

        return alBlock;
    }

    List<Swap> neighbors(Block block) {

        int machine = block.machine;
        int firstTask = block.firstTask;
        int lastTask = block.lastTask;

        ArrayList<Swap> alSwap = new ArrayList<>();
        if(lastTask-firstTask == 1){
            alSwap.add(new Swap(machine,lastTask,firstTask));
        }else{
            alSwap.add(new Swap(machine,firstTask,firstTask+1));
            alSwap.add(new Swap(machine,lastTask-1,lastTask));
        }

        return alSwap;
    }

}
