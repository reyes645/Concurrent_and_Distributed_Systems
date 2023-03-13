//UT-EID= rcr2662

import java.util.*;
import java.util.concurrent.*;

public class ForkJoinPSort{
    /* Notes:
     * The input array (A) is also the output array,
     * The range to be sorted extends from index begin, inclusive, to index end, exclusive,
     * Sort in increasing order when increasing=true, and decreasing order when increasing=false,
     */

    public static void parallelSort(int[] A, int begin, int end, boolean increasing) {
        // TODO: Implement your parallel sort function using ForkJoinPool
        if (A == null || A.length <= 1) return;
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        QuickSortTask quicksort = new QuickSortTask(A, begin, end, increasing);
        forkJoinPool.invoke(quicksort);  
        if(!increasing){
            Integer[] AReverse = new Integer[A.length];
            for (int i = 0; i < A.length; i++) {
                AReverse[i] = A[i];
            }
            
            Arrays.sort(AReverse, begin, end, Collections.reverseOrder());
            System.arraycopy( Arrays.stream(AReverse).mapToInt(Integer::intValue).toArray(), 0, A, 0, A.length);
        } 
    }

    

    static class QuickSortTask extends RecursiveAction{
        int[] A;
        int begin;
        int end;
        boolean increasing;

        public QuickSortTask(int[] A, int begin, int end, boolean increasing) {
            this.A = A;
            this.begin = begin;
            this.end = end;
            this.increasing = increasing;
          }
        
        @Override
        protected void compute() {
            if (begin >= end)return;
            if (end-begin <= 16){
                Arrays.sort(A, begin, end);
            } else {
                int pivot = partition(A, begin, end);
                QuickSortTask left = new QuickSortTask(A, begin, pivot+1, increasing);
                QuickSortTask right = new QuickSortTask(A, pivot+1, end, increasing);
                left.fork();
                right.compute();
                left.join();
            }
        }

        static int partition(int A[], int begin,int end){
            int pivot = A[begin];
            int i = begin-1, j = end;
            while(true) {
                do { i++; } while(A[i] < pivot);
                do { j--; } while(A[j] > pivot);
                if (i < j) swap(A, i, j);
                else return j;
            }
            
        }

        static void swap(int[] A, int i, int j) {
            int temp = A[i];
            A[i] = A[j];
            A[j] = temp;
          }
    }
}

