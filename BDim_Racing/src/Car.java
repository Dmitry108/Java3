import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Car implements Runnable {
    private static int CARS_COUNT;
    static {
        CARS_COUNT = 0;
    }
    private Race race;
    private int speed;
    private String name;
    private static CountDownLatch cdlStart;
    private static CountDownLatch cdlFinish;
    private static CyclicBarrier cb;
    private static List<Result> resultList;

    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed, CountDownLatch cdlStart, CountDownLatch cdlFinish, List<Result> resultList) {
        this.race = race;
        this.speed = speed;
        Car.cdlStart = cdlStart;
        Car.cdlFinish = cdlFinish;
        Car.cb = new CyclicBarrier(MainClass.CARS_COUNT);
        Car.resultList = resultList;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }
    @Override
    public void run() {
        try {
            System.out.println(LocalTime.now() + " " + this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(LocalTime.now() + " " + this.name + " готов");
            cdlStart.countDown();
            cb.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long t0 = System.currentTimeMillis();
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        Long t = System.currentTimeMillis() - t0;
        resultList.add(new Result(this.name, this.speed, t));
        if (resultList.size()==1){
            System.out.println(LocalTime.now() + " " + this.name + " WIN");
        }
        cdlFinish.countDown();
    }
}