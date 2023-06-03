public class CurrentStatus implements Runnable {

    private Results results;

    public CurrentStatus(Results results) {
        this.results = results;
    }

    @Override
    public void run() {
        while (results.getSize() < 100) {
            System.out.println("Got " + results.getSize() + " so far...");
            results.print();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
