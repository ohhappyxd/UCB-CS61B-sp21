package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import edu.princeton.cs.introcs.StdRandom;
import student.StudentArrayDeque;

/** Randomly calls StudentArrayDeque and ArrayDequeSolution methods until they disagree on an output.*/
public class TestArrayDequeEC {
    @Test
    public void testSAD() {
        StudentArrayDeque<Integer> sad1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> solution1 = new ArrayDequeSolution<>();
        String message = "";

        for (int i = 0; i < 10000; i += 1) {
            double randomNumber = StdRandom.uniform(0.0, 4.0);

            if (randomNumber < 1.0) {
                sad1.addLast(i);
                solution1.addLast(i);
                message = message + "addLast(" + i + ")\n";
            } else if (randomNumber < 2.0) {
                sad1.addFirst(i);
                solution1.addFirst(i);
                message = message + "addFirst(" + i + ")\n";
            } else if (randomNumber < 3.0) {
                if (!sad1.isEmpty()) {
                    Integer student = sad1.removeLast();
                    Integer solution = solution1.removeLast();
                    message = message + "removeLast()\n";
                    assertEquals(message, student, solution);
                }
            } else {
                if (!sad1.isEmpty()) {
                    Integer student = sad1.removeFirst();
                    Integer solution = solution1.removeFirst();
                    message = message + "removeFirst()\n";
                    assertEquals(message, student, solution);
                }
            }
        }
    }
}
