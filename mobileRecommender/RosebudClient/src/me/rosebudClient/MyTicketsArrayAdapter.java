package me.rosebudClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import transmit.TicketShared;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyTicketsArrayAdapter extends ArrayAdapter<TicketShared> {

	private final Context context;
	private final TicketShared[] tickets;

	private SimpleDateFormat dfm = new SimpleDateFormat("HH:mm");

	public MyTicketsArrayAdapter(Context context, TicketShared[] tickets) {
		super(context, R.layout.show_list_item, tickets);
		this.context = context;
		this.tickets = tickets;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.ticket_list_item, parent, false);

		if (tickets[position].image != null) {
			ImageView imageView = (ImageView) rowView.findViewById(R.id.ticketList_image);
			Bitmap bitmap = BitmapFactory.decodeByteArray(tickets[position].image, 0, tickets[position].image.length);
			imageView.setImageBitmap(bitmap);
		}

		TextView moviename = (TextView) rowView.findViewById(R.id.ticketList_moviename);
		moviename.setText(tickets[position].movieName);

		TextView showList_hall = (TextView) rowView.findViewById(R.id.ticketList_hall);
		showList_hall.setText("Hall: " + tickets[position].hallNr);

		TextView amount = (TextView) rowView.findViewById(R.id.ticketList_amount);
		amount.setText("Amount: " + tickets[position].amount);
		amount.setTag(tickets[position].amount);

		TextView showList_time = (TextView) rowView.findViewById(R.id.ticketList_time);
		showList_time.setTag(tickets[position].time);
		showList_time.setText("Time: " + dfm.format(new Date(tickets[position].time)));

		return rowView;
	}

	@Override
	public long getItemId(int position) {
		return (long) tickets[position].showID;
	}

}
