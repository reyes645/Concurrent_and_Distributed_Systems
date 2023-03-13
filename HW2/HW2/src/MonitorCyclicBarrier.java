//rcr26662

public class MonitorCyclicBarrier implements CyclicBarrier {

    private int parties;
    private int count;
    private boolean active;

    public MonitorCyclicBarrier(int parties) {
        this.parties = parties;
        this.count = 0;
        this.active = true;
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
    public synchronized int await() throws InterruptedException {
        int index = -1;
        if(active){
            count++;
            if(count >= parties){
                index = parties - 1;
                notifyAll();
                count = 0;
            } else {
                wait();
                index = count - 1;
            }
        }
        return index;
    }

    /*
     * This method activates the cyclic barrier. If it is already in
     * the active state, no change is made.
     * If the barrier is in the inactive state, it is activated and
     * the state of the barrier is reset to its initial value.
     */
    public synchronized void activate() throws InterruptedException {
        if(!active){
            active = true;
            count = 0;
        }
    }

    /*
     * This method deactivates the cyclic barrier.
     * It also releases any waiting threads
     */
    public synchronized void deactivate() throws InterruptedException {
        active = false;
        notifyAll();
    }
}
