package fpl.md37.genz_fashion.ManagerScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genz_fashion.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import fpl.md37.genz_fashion.adapter.AdapterViewCustomer;
import fpl.md37.genz_fashion.models.Client;

public class InformationFragment extends Fragment {
    FirebaseFirestore db;

    RecyclerView rcv_client;
    SearchView searchCustomer;
    Context context;
    ArrayList<Client> clients = new ArrayList<>();
    AdapterViewCustomer adapter_clients;
    ArrayList<Client> filteredClients = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for the fragment
        View view = inflater.inflate(R.layout.fragment_customer_information, container, false);
        ImageView btnback=view.findViewById(R.id.btnout);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MainActivityManager.class);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);

            }
        });
        rcv_client = view.findViewById(R.id.rcv_client);
        searchCustomer = view.findViewById(R.id.search_Customer);
        db = FirebaseFirestore.getInstance();

        adapter_clients = new AdapterViewCustomer(getActivity(), clients, db);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcv_client.setLayoutManager(linearLayoutManager);
        rcv_client.setAdapter(adapter_clients);

        ListenFirebaseFirestore_Cilent();

        // Thiết lập listener cho SearchView
        searchCustomer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText); // Gọi hàm lọc
                return true;
            }
        });

        return view;
    }

    private void ListenFirebaseFirestore_Cilent() {
        db.collection("Client")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("TAG", "fail", error);
                            return;
                        }
                        if (value != null) {
                            for (DocumentChange dc : value.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED: {
                                        Client newU = dc.getDocument().toObject(Client.class);
                                        clients.add(newU);
                                        adapter_clients.notifyItemInserted(clients.size() - 1);
                                        break;
                                    }
                                    case MODIFIED: {
                                        Client update = dc.getDocument().toObject(Client.class);
                                        if (dc.getOldIndex() == dc.getNewIndex()) {
                                            clients.set(dc.getOldIndex(), update);
                                            adapter_clients.notifyItemChanged(dc.getOldIndex());
                                        } else {
                                            clients.remove(dc.getOldIndex());
                                            clients.add(update);
                                            adapter_clients.notifyItemMoved(dc.getOldIndex(), dc.getNewIndex());
                                        }
                                        break;
                                    }
                                    case REMOVED: {
                                        dc.getDocument().toObject(Client.class);
                                        clients.remove(dc.getOldIndex());
                                        adapter_clients.notifyItemRemoved(dc.getOldIndex());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void filterList(String text) {
        filteredClients.clear();
        if (text.isEmpty()) {
            filteredClients.addAll(clients);
        } else {
            for (Client client : clients) {
                if (client.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredClients.add(client);
                }
            }
        }
        adapter_clients.updateList(filteredClients);
    }
}
