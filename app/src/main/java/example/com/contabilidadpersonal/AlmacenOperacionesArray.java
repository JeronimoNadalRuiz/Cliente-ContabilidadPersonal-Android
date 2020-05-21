package example.com.contabilidadpersonal;

import android.content.Context;
import java.util.Vector;

public class AlmacenOperacionesArray implements AlmacenOperaciones {
    private static Vector<String> operaciones;
    private static AlmacenOperacionesArray instancia = null;
    private Context context;

    @Override
    public Vector<String> listaOperaciones(int cantidad) {
        return operaciones;
    }

    @Override
    public void guardarOperacion(String datos) {
        operaciones.add(datos);
    }
    public void vaciaOperaciones()
    {
        operaciones.clear();
    }

    private AlmacenOperacionesArray(Context context) {
        this.context = context;
    }

    // Costructor privado para patrón singleton
    private AlmacenOperacionesArray() {

    }

    public static AlmacenOperacionesArray getInstance(Context context) {
        if (instancia == null) {
            synchronized (AlmacenOperacionesArray.class) {
                if (instancia == null) {
                    instancia = new AlmacenOperacionesArray(context);
                    operaciones = new Vector<>();
                }
            }
        }else{
            operaciones.clear();
        }
        return instancia;
    }

    @Override
    public boolean hayOperaciones() {
        return (!operaciones.isEmpty());
    }
}
