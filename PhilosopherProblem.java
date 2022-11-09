import java.util.concurrent.Semaphore;

public class PhilosopherProblem {
    static Semaphore semaphore = new Semaphore(4);
    public static void main(String args[]) throws InterruptedException {
        int n=5;
        Fork[] forks=new Fork[n];
        for(int i=0;i<n;i++){
            forks[i]=new Fork(i);
        }
        Philosopher[] philosophers=new Philosopher[n];
        for(int i=1;i<n;i++){
            philosophers[i]=new Philosopher(forks[i-1],forks[i],false,"philosopher"+i);
        }
        philosophers[0]=new Philosopher(forks[4],forks[0],false,"philosopher0");
        Thread[] threads=new Thread[n];
        for(int i=0;i<n;i++){
            threads[i]=new Thread(philosophers[i]);
            threads[i].start();
            int finalI = i;
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        philosophers[finalI].surviving();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        }
    }
}
class Philosopher implements Runnable{
    private Object mutex = new Object();
    private Fork rightFork;
    private Fork leftFork;
    private boolean stop;
    private int hungerLevel =9;
    private String name;
    public Philosopher(Fork leftFork, Fork rightFork, boolean stop,String name) {
        this.rightFork = rightFork;
        this.leftFork = leftFork;
        this.stop = stop;
        this.name=name;
    }

    @Override
    public void run() {
        while(!stop){
            //access mutex to feed
            try {
                PhilosopherProblem.semaphore.acquire(); //assign one semaphore to a philosopher
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (leftFork){//get left fork
                System.out.println(this.name+" picked up fork "+leftFork.id);
                synchronized (rightFork){//get right fork
                    System.out.println(this.name+" picked up fork "+rightFork.id+" and begun to eat");
                    try {
                        Thread.sleep(5000);// //eating
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(this.name+" dropped off forks");
                    System.out.println(this.name+" is thinking");
                }
            }
            PhilosopherProblem.semaphore.release();

            synchronized (mutex){
                this.hungerLevel =10;
            }
            try {
                this.think();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public void surviving() throws InterruptedException {
        while(true){
            synchronized (mutex){
                //System.out.println(starvingCount);
                hungerLevel--;
                if(hungerLevel <0) break;
            }
            Thread.sleep(3000);
        }
        this.die();
    }
    public void think() throws InterruptedException {

        Thread.sleep(8000);
    }
    private void die(){
        System.out.println(this.name+" is died");
        synchronized (mutex){
            this.stop=true;
        }
    }
}
class Fork{
    public Fork(int id) {
        this.id = id;
    }
    int id;
}