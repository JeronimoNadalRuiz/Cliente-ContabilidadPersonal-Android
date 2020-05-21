package example.com.contabilidadpersonal;

import android.content.DialogInterface;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;


public class OperacionHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView Tfecha = null;
    TextView Tconcepto = null;
    TextView Tcantidad = null;
    ImageView icono = null;
    TextView Tid = null;
    FrameLayout eliminarLayout, editLayout;

    OperacionHolder(View fila) {
        super(fila);
        Tid = (TextView)fila.findViewById(R.id.id);
        Tfecha = (TextView)fila.findViewById(R.id.fecha);
        Tconcepto = (TextView)fila.findViewById(R.id.concepto);
        Tcantidad = (TextView)fila.findViewById(R.id.precio);
        icono = (ImageView)fila.findViewById(R.id.icono);
        eliminarLayout = (FrameLayout)fila.findViewById(R.id.delete_layout);
        editLayout = (FrameLayout)fila.findViewById(R.id.edit_layout);
        fila.setOnClickListener(this);
        eliminarLayout.setOnClickListener(this);
        editLayout.setOnClickListener(this);
    }

    public void bindModel(String item) {
        try{
            JSONObject x = new JSONObject(item);
            String id = x.getString("_id");
            String fecha = x.getString("fecha");
            String fecha_aux =  fecha.substring(0, Math.min(fecha.length(), 10));
            String concepto = x.getString("concepto");
            String cantidad = x.getString("cantidad");
            Tid.setText(id);
            Tfecha.setText(fecha_aux);
            Tconcepto.setText(concepto);
            Tcantidad.setText(cantidad + "€");
            String tipo = x.getString("tipo");
            switch (tipo){
                case "Ingreso": icono.setImageResource(R.drawable.ingreso);
                    break;
                case "Gasto": icono.setImageResource(R.drawable.gasto);
                    break;
                default: icono.setImageResource(R.drawable.ingreso);
                    break;
            }
        } catch (JSONException e) {}
    }

    @Override
    public void onClick(View view) {
    }
}