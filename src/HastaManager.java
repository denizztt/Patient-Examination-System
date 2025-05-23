package src;

import java.io.*;
import java.util.*;

public class HastaManager {
    public List<Hasta> tumHastalar = new ArrayList<>();
    public MaxHeap muayeneSirasi = new MaxHeap();
    public List<Hasta> muayeneEdilenler = new ArrayList<>();
    public List<Hasta> eklenmemisHastalar = new ArrayList<>();
    private int hastaNoSayac = 1;

    public void dosyadanOku(String dosyaYolu) throws IOException {
        tumHastalar.clear();
        muayeneSirasi = new MaxHeap();
        muayeneEdilenler.clear();
        eklenmemisHastalar.clear();
        hastaNoSayac = 1;
        BufferedReader br = new BufferedReader(new FileReader(dosyaYolu));
        String satir;
        while ((satir = br.readLine()) != null) {
            if (satir.trim().isEmpty()) continue;
            String[] parca = satir.split(",");
            if (parca.length < 8) continue;
            String ad = parca[1].trim();
            int yas = Integer.parseInt(parca[2].trim());
            String cinsiyet = parca[3].trim();
            boolean mahkum = parca[4].trim().equalsIgnoreCase("true");
            int engelli = Integer.parseInt(parca[5].trim());
            String kanama = parca[6].trim();
            double kayitSaat = Double.parseDouble(parca[7].trim().replace(":", "."));
            Hasta h = new Hasta(hastaNoSayac++, ad, yas, cinsiyet, mahkum, engelli, kanama, kayitSaat);
            h.hesaplaOncelikPuani();
            h.hesaplaMuayeneSuresi();
            tumHastalar.add(h);
            eklenmemisHastalar.add(h);
        }
        br.close();
    }

    public void yeniHastaEkle(Hasta h) {
        h.hesaplaOncelikPuani();
        h.hesaplaMuayeneSuresi();
        tumHastalar.add(h);
        eklenmemisHastalar.add(h);
    }

    public Hasta siradakiHastayiMuayeneyeAl(double simdikiSaat) {
        Hasta h = muayeneSirasi.cikar();
        if (h != null) {
            h.muayeneSaati = simdikiSaat;
            muayeneEdilenler.add(h);
        }
        return h;
    }

    public List<Hasta> bekleyenHastalar() {
        return muayeneSirasi.getHeapList();
    }

    public boolean bekleyenHastaVar() {
        return !muayeneSirasi.bosMu();
    }
} 