//rcr26662

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FairUnifanBathroom {   
  private ReentrantLock lock;
  private PriorityBlockingQueue<Integer> queue;
  private Condition utCondition;
  private Condition ouCondition;
  private int count;
  private Team team;
  private int ticketNumber;
  private enum Team {
    UT,
    OU
  }

  public FairUnifanBathroom() {
    lock = new ReentrantLock();
    queue = new PriorityBlockingQueue<>();
    utCondition = lock.newCondition();
    ouCondition = lock.newCondition();
    count = 0;
    ticketNumber = 0;
  }

  public void enterBathroomUT() {
    // Called when a UT fan wants to enter bathroom	
    lock.lock();
    try {
      int myTicket = ticketNumber++;
      queue.add(myTicket);
      while (count == 7 || (count > 0 && team == Team.OU)|| myTicket != queue.peek()) {
        utCondition.await();
      }
      count++;
      queue.remove(myTicket);
      team = Team.UT;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
  }
    
    public void enterBathroomOU() {
    // Called when a OU fan wants to enter bathroom
      lock.lock();
      try {
        int myTicket = ticketNumber++;
        queue.add(myTicket);
        while (count == 7 || (count > 0 && team == Team.UT )|| myTicket != queue.peek()) {
          ouCondition.await();
        }
        count++;
        queue.remove(myTicket);
        team = Team.OU;
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        lock.unlock();
      }
    }
    
    public void leaveBathroomUT() {
    // Called when a UT fan wants to leave bathroom
      lock.lock();
      try {
        count--;
        if (count == 0) {
          team = null;
          ouCondition.signalAll();
        } else {
          utCondition.signalAll();
        }
      } finally {
        lock.unlock();
      }
    }

    public void leaveBathroomOU() {
    // Called when a OU fan wants to leave bathroom
      lock.lock();
      try {
        count--;
        if (count == 0) {
          team = null;
          utCondition.signalAll();
        } else {
          ouCondition.signalAll();
        }
      } finally {
        lock.unlock();
      }
    }
  }