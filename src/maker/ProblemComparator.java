package maker;

import java.util.Comparator;

public class ProblemComparator implements Comparator<Problem>{

    public int compare(Problem problem1, Problem problem2) {
        return Integer.compare(problem1.getId(), problem2.getId());
     }
}
