import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class MainClass {
    public static final int CARS_COUNT = 4;

    public static void main(String[] args) {
        System.out.println(LocalTime.now() + " ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];

        CountDownLatch cdlStart = new CountDownLatch(CARS_COUNT);
        CountDownLatch cdlFinish = new CountDownLatch(CARS_COUNT);
        List<Result> result = new CopyOnWriteArrayList<>();
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10), cdlStart, cdlFinish, result);
        }
        for (int i=0; i<CARS_COUNT; i++){
            new Thread(cars[i]).start();
        }
        try {
            cdlStart.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(LocalTime.now() + " ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        try {
            cdlFinish.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(LocalTime.now() + " ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
        System.out.println(String.format("%-10s%-15s%-10s%-10s", "Место", "Участник", "Скорость", "Время"));
        for(int i=0; i<CARS_COUNT; i++){
            System.out.println(String.format("%-10d%-15s%-10d%-10s",
                    i+1, result.get(i).getCarName(), result.get(i).getCarSpeed(), result.get(i).getTime()));
        }
    }
}