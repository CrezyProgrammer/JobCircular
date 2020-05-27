package com.mssoftinc.jobcircular.ui.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mssoftinc.jobcircular.R;
import com.mssoftinc.jobcircular.ViewActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {


  private Context mCtx;
  private List<Product> productList;

  public ProductsAdapter(Context mCtx, List<Product> productList) {
    this.mCtx = mCtx;
    this.productList = productList;
  }

  @Override
  public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(mCtx);
    View view = inflater.inflate(R.layout.product_list, null);
    return new ProductViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ProductViewHolder holder, int position) {
    Product product = productList.get(position);
    holder.setup(product);
    //loading the image

  }

  @Override
  public int getItemCount() {
    //Log.i("123321", "45:"+productList.size());
    return productList.size();

  }

  class ProductViewHolder extends RecyclerView.ViewHolder {

    TextView title,time,edu;
    LinearLayout layout;
    CardView cardView;

    public ProductViewHolder(View itemView) {
      super(itemView);
      title=itemView.findViewById(R.id.titleText);
      time=itemView.findViewById(R.id.time);
      layout=itemView.findViewById(R.id.main);
      cardView=itemView.findViewById(R.id.card);
      edu=itemView.findViewById(R.id.link);

    }

    public void setup(final Product product) {
      cardView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent=new Intent(v.getContext(), ViewActivity.class);
          intent.putExtra("id",product.getId());
          v.getContext().startActivity(intent);
        }
      });
      layout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


        }
      });
      title.setText(product.getTitle());
      Log.i("1233210", "******************\ntitle:"+product.getTitle()+"\nsubtitle:"+product.getSubtitle()+"\nid:"+product.getId()+"\nslug:"+product.getSlug()+"\npublish_date:"+product.getDate()+"\n*****************************");

      edu.setText(product.getSubtitle());
      try {
        String s=StringUtils.substringBetween(product.getSlug(), "<p>আবেদনের শেষ তারিখঃ", "</p>");
        String s2=StringUtils.substringBetween(product.getSlug(), "<p>শিক্ষাগত যোগ্যতাঃ", "</p>");

        time.setText(s!=null||!s.equals("")? s :"na");




      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}