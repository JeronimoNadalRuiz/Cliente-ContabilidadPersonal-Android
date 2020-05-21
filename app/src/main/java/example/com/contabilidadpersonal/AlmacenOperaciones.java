package example.com.contabilidadpersonal;

import java.util.Vector;


public interface AlmacenOperaciones {
    public Vector<String> listaOperaciones(int cantidad);
    public boolean hayOperaciones();
    public void guardarOperacion(String datos);
    public void vaciaOperaciones();
}
