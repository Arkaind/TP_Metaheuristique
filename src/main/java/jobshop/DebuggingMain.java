package jobshop;

import jobshop.encodings.ResourceOrder;
import jobshop.solvers.GreedySolver;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.format.ResolverStyle;

public class DebuggingMain {

    public static void main(String[] args) {
        try {
            // load instance (aaa1 / ft06 / ft10 / ft20 )
            Instance instance = Instance.fromFile(Paths.get("instances/ft20"));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static void testGreedy(Instance instance){
        ResourceOrder rso = new ResourceOrder(instance);

        GreedySolver grSo = new GreedySolver();

        grSo.setPriority(1);

        Result SPT = grSo.solve(instance,10000);

        System.out.println("Makespan SPT = " + SPT.schedule.makespan());

        grSo.setPriority(2);

        Result LPT = grSo.solve(instance,10000);

        System.out.println("Makespan LPT = " + LPT.schedule.makespan());

        grSo.setPriority(3);

        Result SRPT = grSo.solve(instance,10000);

        System.out.println("Makespan SRPT = " + SRPT.schedule.makespan());

        grSo.setPriority(4);

        Result LRPT = grSo.solve(instance,10000);

        System.out.println("Makespan LRPT = " + LRPT.schedule.makespan());

        grSo.setPriority(5);

        Result EST_SPT = grSo.solve(instance,10000);

        System.out.println("Makespan EST_SPT = " + EST_SPT.schedule.makespan());

        grSo.setPriority(6);

        Result EST_LRPT = grSo.solve(instance,10000);

        System.out.println("Makespan EST_LRPT = " + EST_LRPT.schedule.makespan());
    }
}