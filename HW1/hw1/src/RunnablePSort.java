//UT-EID= rcr2662

import java.util.*;

public class RunnablePSort {
    /* Notes:
     * The input array (A) is also the output array,
     * The range to be sorted extends from index begin, inclusive, to index end, exclusive,
     * Sort in increasing order when increasing=true, and decreasing order when increasing=false,
     */
    public static void parallelSort(int[] A, int begin, int end, boolean increasing) {
        if (A == null || A.length <= 1) return;
        Thread quicksort = new Thread(new QSortRunnable(A, begin, end, increasing));
        quicksort.start();
        try {
            quicksort.join();
            if(!increasing){
                Integer[] AReverse = new Integer[A.length];
                for (int i = 0; i < A.length; i++) {
                    AReverse[i] = A[i];
                }
                Arrays.sort(AReverse, begin, end, Collections.reverseOrder());
                System.arraycopy( Arrays.stream(AReverse).mapToInt(Integer::intValue).toArray(), 0, A, 0, A.length);
            }
        }catch(InterruptedException e){
            System.out.println(e);
        }
    }

static class QSortRunnable implements Runnable{
    int[] A;
    int begin;
    int end;
    boolean increasing;

    public QSortRunnable(int[] A, int begin, int end, boolean increasing){
        this.A = A;
        this.begin = begin;
        this.end = end;
        this.increasing = increasing;
    }
    @Override
    public void run() {
        if (begin >= end)return;
        if (end-begin <= 16){
            Arrays.sort(A, begin, end);
        } else {
            int pivot = partition(A, begin, end);
            Thread left = new Thread(new QSortRunnable(A, begin, pivot + 1, increasing));
            Thread right = new Thread(new QSortRunnable(A, pivot + 1, end, increasing));
            left.start();
            right.start();
            
            try {
                left.join();
                right.join();
            }catch(InterruptedException e){
                System.out.println(e);
            }
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