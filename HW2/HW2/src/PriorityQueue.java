//rcr2662

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityQueue {
        private List<Node> queue;
        private int maxSize;
        private ReentrantLock lock;
        private Condition notFull;
        private Condition notEmpty;
    
        public class Node {
            String name;
            int priority;
    
            Node(String name, int priority) {
                this.name = name;
                this.priority = priority;
            }
        }

	public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
                this.queue = new LinkedList<>();
                this.maxSize = maxSize;
                this.lock = new ReentrantLock();
                this.notFull = lock.newCondition();
                this.notEmpty = lock.newCondition();
	}

	public int add(String name, int priority) {
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
                lock.lock();
                try {
                        while (queue.size() == maxSize) {
                                notFull.await();
                        }
                        int pos = 0;
                        for (int i = 0; i < queue.size(); i++) {
                                if (queue.get(i).priority <= priority) {
                                pos = i;
                                break;
                                }
                        }
                        queue.add(pos, new Node(name, priority));
                        notEmpty.signalAll();
                        return pos;
                } catch (InterruptedException e) {
                        e.printStackTrace();
                } finally {
                        lock.unlock();
                }
                return -1;
	}

	public int search(String name) {
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.
                lock.lock();
                try {
                        for (int i = 0; i < queue.size(); i++) {
                                if (queue.get(i).name.equals(name)) {
                                return i;
                                }
                        }
                } finally {
                        lock.unlock();
                }
                return -1;
	}

	public String getFirst() {
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
                lock.lock();
                try {
                        while (queue.isEmpty()) {
                                notEmpty.await();
                        }
                        Node node = queue.remove(0);
                        notFull.signalAll();
                        return node.name;
                } catch (InterruptedException e) {
                        e.printStackTrace();
                } finally {
                        lock.unlock();
                }
                return null;
	}
}