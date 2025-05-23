package src;

import java.util.Timer;
import java.util.TimerTask;

public class SanalSaat {
    private double saat; // 8.00 ile başla
    private Timer timer;
    private double hiz; // 1 sn = 1 dk gibi
    private boolean calisiyor = false;

    public SanalSaat(double baslangic, double hiz) {
        this.saat = baslangic;
        this.hiz = hiz;
    }

    public void baslat(Runnable tickCallback) {
        if (calisiyor) return;
        calisiyor = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                saat += 0.01; // 1 dk ilerlet
                if (tickCallback != null) tickCallback.run();
            }
        }, 0, (long)(1000 / hiz));
    }

    public double getSaat() { return saat; }
    public void durdur() { 
        if (timer != null) timer.cancel();
        calisiyor = false;
    }
    public void sifirla(double yeniSaat) {
        durdur();
        this.saat = yeniSaat;
    }
} 