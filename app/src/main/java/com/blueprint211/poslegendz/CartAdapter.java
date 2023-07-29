package com.blueprint211.poslegendz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {


    List<Cart> cartList;
    Context ctx;




    public CartAdapter (Context ctx,List <Cart> cartList){
        this.ctx = ctx;
        this.cartList = cartList;

    }





    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new CartAdapter.CartHolder(LayoutInflater.from(ctx).inflate(R.layout.cartliststyle, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartHolder holder, int position) {

        holder.refname.setText("Item:" +  cartList.get(position).getRefname());
        holder.items.setText("Ordered Items:" +  cartList.get(position).getItems()+"");
        holder.price.setText("Ordered Items Cost:" + cartList.get(position).getSales() + "");




    }

    @Override
    public int getItemCount() {



            return cartList.size();


    }

      class CartHolder  extends RecyclerView.ViewHolder {


        TextView refname, price,items;
        ImageButton deleteItem;
        float cusitem;
          boolean isConnected = false;
          ConnectivityManager connectivityManager;


        public CartHolder(@NonNull View itemView) {
            super(itemView);

            refname = itemView.findViewById(R.id.cartproducttitle);
            items = itemView.findViewById(R.id.cartitem);
            price = itemView.findViewById(R.id.cartdesc);
            deleteItem = itemView.findViewById(R.id.deleteitem);
            registerNetworkCallback();
            new ConnectivityManager.NetworkCallback();


            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isConnected) {



                   cusitem = cartList.get(getAbsoluteAdapterPosition()).getItems();
                    String title = cartList.get(getAbsoluteAdapterPosition()).getRefname();
                        String beer = "Keg Beer";




                    FirebaseFirestore.getInstance().collection("Products").whereEqualTo("reference", title).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot document = task.getResult();
                                for (DocumentSnapshot shs : document.getDocuments()) {
                                    Products products = shs.toObject(Products.class);
                                    assert products != null;
                                    float numitems = products.getItems();
                                    float returnitems = (cusitem + numitems);
                                    shs.getReference().update("items", returnitems);


                                }
                            }
                        }
                    });



                    FirebaseFirestore.getInstance().collection("Cart" ).document(title).delete();
                        FirebaseFirestore.getInstance().collection("Cart" ).document(beer).delete();

                    cartList.remove(cartList.get(getAbsoluteAdapterPosition()));
                    notifyItemRemoved(getAbsoluteAdapterPosition());
                    Toast.makeText(CartAdapter.CartHolder.this.itemView.getContext(), "The Item" +  title + "is deleted from Cart list Successfully!", Toast.LENGTH_SHORT).show();
                    if (cartList.isEmpty()){
                        ((Activity)ctx).finish();


                    }
                    }else{
                        ctx. startActivity(new Intent(ctx, noInternetActivity.class));
                    }

                }

            });


        }
          private void registerNetworkCallback(){


              try {

                  connectivityManager = (ConnectivityManager) itemView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                  connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){

                      @Override
                      public void onAvailable(@NonNull Network network) {
                          isConnected = true;
                      }

                      @Override
                      public void onLost(@NonNull Network network) {
                          isConnected = false;
                      }
                  });




              }catch (Exception e){

                  isConnected = false;

              }

          }
    }
}





