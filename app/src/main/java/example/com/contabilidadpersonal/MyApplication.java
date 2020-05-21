package example.com.contabilidadpersonal;
import android.app.Application;

public class MyApplication extends Application {

    private String nombre;
    private String ip = "http://192.168.1.103:3000/v1";

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIp() {
        return ip;
    }
}