//rcr2662

import java.util.concurrent.CountDownLatch;

public class FairUnifanBathroomTest {
  
  private static final int NUM_FANS = 100;
  
  public static void main(String[] args) throws InterruptedException {
    FairUnifanBathroom bathroom = new FairUnifanBathroom();
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch finishLatch = new CountDownLatch(NUM_FANS * 2);
    
    // Create UT fans
    for (int i = 0; i < NUM_FANS; i++) {
      new Thread(() -> {
        try {
          startLatch.await();
          bathroom.enterBathroomUT();
          Thread.sleep(100);
          bathroom.leaveBathroomUT();
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          finishLatch.countDown();
        }
      }).start();
    }
    
    // Create OU fans
    for (int i = 0; i < NUM_FANS; i++) {
      new Thread(() -> {
        try {
          startLatch.await();
          bathroom.enterBathroomOU();
          Thread.sleep(100);
          bathroom.leaveBathroomOU();
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          finishLatch.countDown();
        }
      }).start();
    }
    
    startLatch.countDown();
    finishLatch.await();
    
    System.out.println("Test passed");
  }
}
