public class Main {
    private final char[] ch = {'A', 'B', 'C'};
    private final int n = 5;
    private volatile int inter = 0;
    private final Object monitor = new Object();

    public static void main(String[] args) {
        Main m = new Main();
        new Thread(()-> m.abc(0)).start();
        new Thread(()-> m.abc(1)).start();
        new Thread(()-> m.abc(2)).start();
    }
    private void abc(int p){
        synchronized (monitor){
            try {
                for (int i=0; i<n; i++){
                    while (inter!=p) {
                        monitor.wait();
                    }
                    System.out.print(ch[p]);
                    inter = inter == ch.length-1 ? 0 : ++inter;
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}