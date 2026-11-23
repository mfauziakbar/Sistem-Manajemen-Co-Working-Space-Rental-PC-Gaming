package PROJEKUAScopy;

public abstract class Fasilitas {
    private int idFasilitas;
    private String nama;
    private String jenis;
    private double tarifPerJam;
    private String status;

    public Fasilitas(String nama, String jenis, double tarifPerJam, String status) {
        this.nama = nama;
        this.jenis = jenis;
        this.tarifPerJam = tarifPerJam;
        this.status = status;
    }

    public int getIdFasilitas() { return idFasilitas; }
    public void setIdFasilitas(int idFasilitas) { this.idFasilitas = idFasilitas; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public double getTarifPerJam() { return tarifPerJam; }
    public void setTarifPerJam(double tarifPerJam) { this.tarifPerJam = tarifPerJam; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public abstract double hitungTarif(int jam);
}