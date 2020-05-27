package com.mssoftinc.jobcircular.ui.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.mssoftinc.jobcircular.MainActivity;
import com.mssoftinc.jobcircular.R;
import com.mssoftinc.jobcircular.ViewActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  // A menu item view type.
  private static final int MENU_ITEM_VIEW_TYPE = 0;

  // The banner ad view type.
  private static final int BANNER_AD_VIEW_TYPE = 1;

  // An Activity's Context.
  private final Context context;

  // The list of banner ads and menu items.
  private final List<Object> recyclerViewItems;

  /**
   */
  public RecyclerViewAdapter(Context context, List<Object> recyclerViewItems) {
    this.context = context;
    this.recyclerViewItems = recyclerViewItems;
  }

  /**
   * The {@link MenuItemViewHolder} class.
   * Provides a reference to each view in the menu item view.
   */
  public class MenuItemViewHolder extends RecyclerView.ViewHolder {
    TextView title,time,edu;
    LinearLayout layout;
    CardView cardView;

    MenuItemViewHolder(View view) {
      super(view);
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

  /**
   * The {@link AdViewHolder} class.
   */
  public class AdViewHolder extends RecyclerView.ViewHolder {

    AdViewHolder(View view) {
      super(view);
    }
  }

  @Override
  public int getItemCount() {
    return recyclerViewItems.size();
  }

  /**
   * Determines the view type for the given position.
   */
  @Override
  public int getItemViewType(int position) {
    return (position % MainActivity.ITEMS_PER_AD == 0) ? BANNER_AD_VIEW_TYPE
            : MENU_ITEM_VIEW_TYPE;
  }

  /**
   * Creates a new view for a menu item view or a banner ad view
   * based on the viewType. This method is invoked by the layout manager.
   */
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    switch (viewType) {
      case MENU_ITEM_VIEW_TYPE:
        View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.product_list, viewGroup, false);
        return new MenuItemViewHolder(menuItemLayoutView);
      case BANNER_AD_VIEW_TYPE:
        // fall through
      default:
        View bannerLayoutView = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.banner_ad_container,
                viewGroup, false);
        return new AdViewHolder(bannerLayoutView);
    }
  }

  /**
   * Replaces the content in the views that make up the menu item view and the
   * banner ad view. This method is invoked by the layout manager.
   */
  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    int viewType = getItemViewType(position);

    switch (viewType) {
      case MENU_ITEM_VIEW_TYPE:
        MenuItemViewHolder menuItemHolder = (MenuItemViewHolder) holder;
        try {
          Product product = (Product) recyclerViewItems.get(position);
          menuItemHolder.setup(product);
        } catch (Exception e) {
          e.printStackTrace();
        }
        //  menuItemHolder.setup(product);
        // Get the menu item image resource ID.


        // Add the menu item details to the menu item view.

        break;
      case BANNER_AD_VIEW_TYPE:
        // fall through
        Log.i("1233211", "161: "+recyclerViewItems.get(position));
      default:
        AdViewHolder bannerHolder = (AdViewHolder) holder;
        Log.i("1233211", "168: "+recyclerViewItems.get(position));

         AdView adView = (AdView) recyclerViewItems.get(0);
        ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;
        // The AdViewHolder recycled by the RecyclerView may be a different
        // instance than the one used previously for this position. Clear the
        // AdViewHolder of any subviews in case it has a different
        // AdView associated with it, and make sure the AdView for this position doesn't
        // already have a parent of a different recycled AdViewHolder.
        if (adCardView.getChildCount() > 0) {
          adCardView.removeAllViews();
        }
       if (adView.getParent() != null) {
         ((ViewGroup) adView.getParent()).removeView(adView);
       }

       // Add the banner ad to the ad view.
       adCardView.addView(adView);
    }
  }

}