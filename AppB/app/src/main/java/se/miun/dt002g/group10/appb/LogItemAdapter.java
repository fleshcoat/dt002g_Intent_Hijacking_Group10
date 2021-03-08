package se.miun.dt002g.group10.appb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LogItemAdapter extends RecyclerView.Adapter<LogItemAdapter.LogItemViewHolder> {
  private final ArrayList<LogItem> mLogItemList;

  public static class LogItemViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextViewDate;
    public TextView mTextViewInfo;

    public LogItemViewHolder(@NonNull View itemView) {
      super(itemView);
      mTextViewDate = itemView.findViewById(R.id.log_date);
      mTextViewInfo = itemView.findViewById(R.id.log_info);
    }
  }

  // Pass the data from the ArrayList to the adapter
  public LogItemAdapter(ArrayList<LogItem> logItemArrayList) {
    mLogItemList = logItemArrayList;
  }

  // Get the layout of the log cards
  @NonNull
  @Override
  public LogItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_view, parent, false);
    return new LogItemViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull LogItemViewHolder holder, int position) {
    LogItem currentLogItem = mLogItemList.get(position);

    holder.mTextViewDate.setText(currentLogItem.getDate());
    holder.mTextViewInfo.setText(currentLogItem.getLogInfo());
  }

  @Override
  public int getItemCount() {
    return mLogItemList.size();
  }
}
