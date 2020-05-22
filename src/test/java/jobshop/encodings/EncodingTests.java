package jobshop.encodings;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.solvers.BasicSolver;
import jobshop.solvers.DescentSolver;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class EncodingTests {

    @Test
    public void testJobNumbers() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

        // numéro de jobs : 1 2 2 1 1 2 (cf exercices)
        JobNumbers enc = new JobNumbers(instance);
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;

        Schedule sched = enc.toSchedule();
        // TODO: make it print something meaningful
        // by implementing the toString() method
        System.out.println(sched);
        assert sched.isValid();
        assert sched.makespan() == 12;



        // numéro de jobs : 1 1 2 2 1 2
        enc = new JobNumbers(instance);
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;

        sched = enc.toSchedule();
        assert sched.isValid();
        assert sched.makespan() == 14;
    }

    @Test
    public void testBasicSolver() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

        // build a solution that should be equal to the result of BasicSolver
        JobNumbers enc = new JobNumbers(instance);
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;

        Schedule sched = enc.toSchedule();
        assert sched.isValid();
        assert sched.makespan() == 12;

        Solver solver = new BasicSolver();
        Result result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.schedule.isValid();
        assert result.schedule.makespan() == sched.makespan(); // should have the same makespan
    }

    @Test
    public void testDescentSolver() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/ft06"));

        DescentSolver dsSolv = new DescentSolver();

        dsSolv.setGreedyPrio(6);

        Result r = dsSolv.solve(instance,1000);

        assert r.schedule.isValid();
        assert r.schedule.makespan() <= 61;

        instance = Instance.fromFile(Paths.get("instances/ft10"));

        r = dsSolv.solve(instance,1000);

        assert r.schedule.isValid();
        assert r.schedule.makespan() <= 1108;

        instance = Instance.fromFile(Paths.get("instances/ft20"));

        r = dsSolv.solve(instance,1000);

        assert r.schedule.isValid();
        assert r.schedule.makespan() <= 1501;

        instance = Instance.fromFile(Paths.get("instances/la02"));

        r = dsSolv.solve(instance,1000);

        assert r.schedule.isValid();
        assert r.schedule.makespan() <= 817;

        instance = Instance.fromFile(Paths.get("instances/la08"));

        r = dsSolv.solve(instance,1000);

        assert r.schedule.isValid();
        assert r.schedule.makespan() <= 939;

    }
}
