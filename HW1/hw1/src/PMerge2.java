//UT-EID= rcr2662

import java.util.*;
import java.util.concurrent.*;

public class PMerge2 {
    /* Notes:
     * Arrays A and B are sorted in the ascending order
     * These arrays may have different sizes.
     * Array C is the merged array sorted in the descending order
     */
    public static void parallelMerge(int[] A, int[] B, int[] C, int numThreads) {
        // TODO: Implement your parallel merge function
        ForkJoinPool pool = new ForkJoinPool();
        MergeTask merge = new MergeTask(A, B, C, numThreads, 1, 0, A.length, 0, B.length, 0, C.length-1);
        pool.invoke(merge);
        
    }

    static class MergeTask extends RecursiveAction{
        int[] A, B, C;
        int numThreads, threadID, AStart, AEnd, BStart, BEnd, CStart, CEnd;
        
        public MergeTask(int[] A, int[] B, int[] C, int numThreads, int threadID, 
                        int AStart, int AEnd, int BStart, int BEnd, int CStart, int CEnd){
            this.A = A;
            this.B = B;
            this.C = C;
            this.numThreads = numThreads;
            this.threadID = threadID;
            this.AStart = AStart;
            this.AEnd = AEnd;
            this.BStart = BStart;
            this.BEnd = BEnd;
            this.CStart = CStart;
            this.CEnd = CEnd;
           
        }

        @Override
        protected void compute() {

            if(threadID > numThreads) return;
            int AMiddle = AStart + (AEnd-AStart)/2;
            int BMiddle = BStart + (BEnd-BStart)/2;
            int CMiddle = CStart + (CEnd-CStart)/2;

            MergeTask t1 = new MergeTask(A, B, C, numThreads, threadID*2, AStart, AMiddle, BStart, BMiddle, CStart, CMiddle);
            MergeTask t2 = new MergeTask(A, B, C, numThreads, threadID*2 + 1, AMiddle, AEnd, BMiddle, BEnd, CMiddle, CEnd);
            invokeAll(t1, t2);
            merge(A, B, C, AStart , AEnd, BStart, BEnd, CEnd);
        }
        

        public static void merge(int[] A, int[] B, int[] C, int AStart, int AEnd, int BStart, int BEnd, int CEnd) {
            int i = AStart, j = BStart, k = CEnd;
            while ((i < A.length && i < AEnd) || (j < B.length && j < BEnd)) {
                if (i == A.length || i == AEnd) {
                    C[k--] = B[j++];
                } else if (j == B.length || j == BEnd) {
                    C[k--] = A[i++];
                } else {
                    if (A[i] < B[j])
                        C[k--] = A[i++];
                    else
                        C[k--] = B[j++];
                }
            }
        }
    }
}