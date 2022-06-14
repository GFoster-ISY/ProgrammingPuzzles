package application.problem;

import java.util.Comparator;

public class ProblemComparator implements Comparator<Problem>{

    public int compare(Problem problem1, Problem problem2) {
        return Integer.compare(problem1.getID(), problem2.getID());
     }
}
