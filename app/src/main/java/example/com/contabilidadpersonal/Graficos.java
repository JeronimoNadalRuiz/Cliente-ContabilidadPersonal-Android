package example.com.contabilidadpersonal;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Graficos extends AppCompatActivity implements OnChartValueSelectedListener {

    private PieChart mChart;
    private String[] mParties = new String[] {
            "Ingresos", "Gastos"
    };
    double ingesos;
    double gastos;
    ArrayList<String> myList = new ArrayList<String>();
    public String url;
    JSONObject jsonObject2;
    String usuarioValue;
    ArrayList<String> operacionesList = new ArrayList<String>();
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usuarioValue = getToken();
        if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("mylist")) {
            myList = getIntent().getExtras().getStringArrayList("mylist");
        }
        ingesos = ingresos();
        gastos = gastos();
        setContentView(R.layout.activity_piechart);

        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        setData(100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        //mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(16f);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buscador, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getOperaciones(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/

        searchView.setOnSearchClickListener(new SearchView.OnClickListener(){

            @Override
            public void onClick(View view) {
                lanzarBusqueda();
            }
        });


        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getOperaciones();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }
    private void setData(float range) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        entries.add(new PieEntry((float) ((ingesos * mult) + mult / 5),
                mParties[0 % mParties.length]));

        entries.add(new PieEntry((float) ((gastos * mult) + mult / 5),
                mParties[1 % mParties.length]));

        PieDataSet dataSet = new PieDataSet(entries, "Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        //Toast.makeText(this, String.valueOf(entries.get(0)), Toast.LENGTH_SHORT).show();

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#8ada0e"));
        colors.add(Color.RED);

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(16f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    private void getOperaciones() {
        url =((MyApplication) this.getApplication()).getIp() + "/operacion/busqueda/" + usuarioValue;
        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        MostrarManager2 mostrarManager2 = new MostrarManager2();
        StringRequest peticion =
                new StringRequest(Request.Method.GET,
                        url,
                        mostrarManager2,
                        mostrarManager2
                );
        colaPeticiones.add(peticion);
    }

    class MostrarManager2 implements Response.Listener<String>, Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), R.string.error_servicio, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response) {
            try {
                operacionesList.clear();
                jsonObject2 = new JSONObject(response);
                JSONArray cast = jsonObject2.getJSONArray("operaciones");

                for (int i=0; i<cast.length(); i++) {
                    JSONObject operaciones = cast.getJSONObject(i);
                    operacionesList.add(operaciones.toString());
                }
                Intent i = new Intent(getApplication(), OperacionesRecycler.class);
                i.putExtra("mylist", operacionesList);
                startActivity(i);
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), R.string.error_servicio, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public String getToken(){
        String s = ((MyApplication) this.getApplication()).getNombre();
        return s;
    }

    public double ingresos()
    {
        double sum = 0;
        for(int i = 0; i < myList.size(); i++)
        {
            try{
                jsonObject2 = new JSONObject(myList.get(i).toString());
                if(jsonObject2.getString("tipo").equals("Ingreso")){
                    sum += Double.parseDouble(jsonObject2.getString("cantidad"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), R.string.error_servicio, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        return sum;
    }

    public double gastos()
    {
        double sum = 0;
        for(int i = 0; i < myList.size(); i++)
        {
            try{
                jsonObject2 = new JSONObject(myList.get(i).toString());
                if(jsonObject2.getString("tipo").equals("Gasto")){
                    sum += Double.parseDouble(jsonObject2.getString("cantidad"));
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), R.string.error_servicio, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        return sum;
    }

    public void lanzarBusqueda(){
        Intent i = new Intent(this, Buscador.class);
        i.putExtra("graficos", "graficos");
        startActivity(i);
        this.finish();
    }
}