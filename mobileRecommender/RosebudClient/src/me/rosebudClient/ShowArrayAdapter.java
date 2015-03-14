package me.rosebudClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import transmit.ShowShared;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowArrayAdapter extends ArrayAdapter<ShowShared> {

	private final Context context;
	private final ShowShared[] shows;

	private SimpleDateFormat dfm = new SimpleDateFormat("HH:mm");

	public ShowArrayAdapter(Context context, ShowShared[] shows) {
		super(context, R.layout.show_list_item, shows);
		this.context = context;
		this.shows = shows;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.show_list_item, parent, false);

		if (shows[position].image != null) {
			ImageView imageView = (ImageView) rowView.findViewById(R.id.showList_image);
			Bitmap bitmap = BitmapFactory.decodeByteArray(shows[position].image, 0, shows[position].image.length);
			imageView.setImageBitmap(bitmap);
		}

		TextView moviename = (TextView) rowView.findViewById(R.id.showList_moviename);
		moviename.setText(shows[position].movieName);

		TextView showList_hall = (TextView) rowView.findViewById(R.id.showList_hall);
		showList_hall.setText("Hall: " + shows[position].hallNr);

		TextView showList_time = (TextView) rowView.findViewById(R.id.showList_time);
		showList_time.setTag(shows[position].time);
		showList_time.setText("Time: " + dfm.format(new Date(shows[position].time)));

		TextView showList_price = (TextView) rowView.findViewById(R.id.showList_price);
		showList_price.setText(shows[position].price + "€");

		return rowView;
	}

	@Override
	public long getItemId(int position) {
		return shows[position].showID;
	}

	/**
	 * has to return always false, because otherwise the header of ShowOverview
	 * is not shown, when this ArrayAdapter is empty.
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

}
