import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class aimerge {
    public static void parallelMerge(int[] A, int[] B, int[] C, int numThreads) {
        int lenA = A.length - 1;
        int lenB = B.length - 1;
        int lenC = lenA + lenB + 1;

        ForkJoinPool pool = new ForkJoinPool(numThreads);
        pool.invoke(new MergeTask(A, B, C, 0, lenA, 0, lenB, lenC));

            private static class MergeTask extends RecursiveAction {
                private int[] A;
                private int[] B;
                private int[] C;
                private int lowA;
                private int highA;
                private int lowB;
                private int highB;
                private int indexC;
        
                public MergeTask(int[] A, int[] B, int[] C, int lowA, int highA, int lowB, int highB, int indexC) {
                    this.A = A;
                    this.B = B;
                    this.C = C;
                    this.lowA = lowA;
                    this.highA = highA;
                    this.lowB = lowB;
                    this.highB = highB;
                    this.indexC = indexC;
                }
        
                @Override
                protected void compute() {
                    if (lowA > highA) {
                        for (int i = lowB; i <= highB; i++) {
                            C[indexC--] = B[i];
                        }
                    } else if (lowB > highB) {
                        for (int i = lowA; i <= highA; i++) {
                            C[indexC--] = A[i];
                        }
                    } else if (A[highA] >= B[highB]) {
                        C[indexC--] = A[highA--];
                        MergeTask task = new MergeTask(A, B, C, lowA, highA, lowB, highB, indexC);
                        task.fork();
                        task.join();
                    } else {
                        C[indexC--] = B[highB--];
                        MergeTask task = new MergeTask(A, B, C, lowA, highA, lowB, highB, indexC);
                        task.fork();
                        task.join();
                    }
                }
            }

    
}