public class Result {
    private String carName;
    private int carSpeed;
    private long time;

    public Result(String carName, int carSpeed, long time) {
        this.carName = carName;
        this.carSpeed = carSpeed;
        this.time = time;
    }
    public String getCarName() { return carName; }
    public int getCarSpeed() { return carSpeed; }
    public long getTime() { return time; }
}
