//rcr26662

import java.util.concurrent.Semaphore;


/* Use only semaphores to accomplish the required synchronization */
public class SemaphoreCyclicBarrier implements CyclicBarrier {

    private int parties;
    private int count;
    private boolean active;
    private Semaphore mutex;
    private Semaphore barrier;

    public SemaphoreCyclicBarrier(int parties) {
        this.parties = parties;
        this.count = 0;
        this.active = true;
        this.mutex = new Semaphore(1);
        this.barrier = new Semaphore(0);
    }

    /*
     * An active CyclicBarrier waits until all parties have invoked
     * await on this CyclicBarrier. If the current thread is not
     * the last to arrive then it is disabled for thread scheduling
     * purposes and lies dormant until the last thread arrives.
     * An inactive CyclicBarrier does not block the calling thread. It
     * instead allows the thread to proceed by immediately returning.
     * Returns: the arrival index of the current thread, where index 0
     * indicates the first to arrive and (parties-1) indicates
     * the last to arrive.
     */
    public int await() throws InterruptedException {
        mutex.acquire();
        int index = -1;
        if(active){
            count++;
            if(count >= parties){
                index = parties - 1;
                barrier.release(parties-1);
                barrier = new Semaphore(0);
                count = 0;
            } else {
                mutex.release();
                barrier.acquire();
                mutex.acquire();
                index = count - 1;
            }
        }
        mutex.release();
        return index;
    }

    /*
     * This method activates the cyclic barrier. If it is already in
     * the active state, no change is made.
     * If the barrier is in the inactive state, it is activated and
     * the state of the barrier is reset to its initial value.
     */
    public void activate() throws InterruptedException {
        mutex.acquire();
        if(!active){
            active = true;
            count = 0;
            barrier = new Semaphore(0);
        }
        mutex.release();
    }

    /*
     * This method deactivates the cyclic barrier.
     * It also releases any waiting threads
     */
    public void deactivate() throws InterruptedException {
        mutex.acquire();
        active = false;
        barrier.release(count);
        mutex.release();
    }
}
