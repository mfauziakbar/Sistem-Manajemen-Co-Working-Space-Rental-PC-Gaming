public class MeetingRoom extends Fasilitas {
    public MeetingRoom(String nama, String jenis, double tarifPerJam, String status) {
        super(nama, jenis, tarifPerJam, status);
    }

    @Override
    public double hitungTarif(int jam) {
        return getTarifPerJam() * jam;
    }
}