package example.com.contabilidadpersonal;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

public class RecyclerViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView = null;
    public RecyclerView getRecyclerView() {
        if (recyclerView == null) {
            synchronized (RecyclerViewActivity.class) {
                if (recyclerView == null) {
                    recyclerView = new RecyclerView(this);
                    recyclerView.setHasFixedSize(true);
                    setContentView(recyclerView);
                }
            }
        }
        return(recyclerView);
    }
    public void setAdapter(RecyclerView.Adapter adapter) {
        getRecyclerView().setAdapter(adapter);
    }
    public RecyclerView.Adapter getAdapter() {
        return(getRecyclerView().getAdapter());
    }
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        getRecyclerView().setLayoutManager(manager);
    }
}