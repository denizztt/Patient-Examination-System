package src;

public class Hasta implements Comparable<Hasta> {
    public int hastaNo;
    public String hastaAdi;
    public int hastaYasi;
    public String cinsiyet;
    public boolean mahkumlukDurumBilgisi;
    public int engellilikOrani;
    public String kanamaliHastaDurumBilgisi;
    public double hastaKayitSaati;
    public double muayeneSaati;
    public int muayeneSuresi;
    public int oncelikPuani;

    public Hasta(int hastaNo, String hastaAdi, int hastaYasi, String cinsiyet, boolean mahkumlukDurumBilgisi,
                 int engellilikOrani, String kanamaliHastaDurumBilgisi, double hastaKayitSaati) {
        this.hastaNo = hastaNo;
        this.hastaAdi = hastaAdi;
        this.hastaYasi = hastaYasi;
        this.cinsiyet = cinsiyet;
        this.mahkumlukDurumBilgisi = mahkumlukDurumBilgisi;
        this.engellilikOrani = engellilikOrani;
        this.kanamaliHastaDurumBilgisi = kanamaliHastaDurumBilgisi;
        this.hastaKayitSaati = hastaKayitSaati;
        this.muayeneSaati = -1;
        this.muayeneSuresi = 0;
        this.oncelikPuani = 0;
    }

    public void hesaplaOncelikPuani() {
        int yasPuani = 0;
        if (0 <= hastaYasi && hastaYasi < 5) yasPuani = 20;
        else if (5 <= hastaYasi && hastaYasi < 45) yasPuani = 0;
        else if (45 <= hastaYasi && hastaYasi < 65) yasPuani = 15;
        else if (hastaYasi >= 65) yasPuani = 25;

        int engellilikPuani = engellilikOrani / 4;
        int mahkumlukPuani = mahkumlukDurumBilgisi ? 50 : 0;
        int kanamaPuani = 0;
        if (kanamaliHastaDurumBilgisi.trim().equalsIgnoreCase("kanamaYok")) kanamaPuani = 0;
        else if (kanamaliHastaDurumBilgisi.trim().equalsIgnoreCase("kanama")) kanamaPuani = 20;
        else if (kanamaliHastaDurumBilgisi.trim().equalsIgnoreCase("agirKanama")) kanamaPuani = 50;

        this.oncelikPuani = yasPuani + engellilikPuani + mahkumlukPuani + kanamaPuani;
    }

    public void hesaplaMuayeneSuresi() {
        int yasPuani = (hastaYasi > 65) ? 15 : 0;
        int engellilikPuani = engellilikOrani / 5;
        int kanamaPuani = 0;
        if (kanamaliHastaDurumBilgisi.trim().equalsIgnoreCase("kanamaYok")) kanamaPuani = 0;
        else if (kanamaliHastaDurumBilgisi.trim().equalsIgnoreCase("kanama")) kanamaPuani = 10;
        else if (kanamaliHastaDurumBilgisi.trim().equalsIgnoreCase("agirKanama")) kanamaPuani = 20;
        this.muayeneSuresi = yasPuani + engellilikPuani + kanamaPuani + 10;
    }

    @Override
    public int compareTo(Hasta o) {
        return Integer.compare(o.oncelikPuani, this.oncelikPuani); // Max heap için
    }

    @Override
    public String toString() {
        return String.format("%02d - %s (Öncelik: %d)", hastaNo, hastaAdi, oncelikPuani);
    }
} 