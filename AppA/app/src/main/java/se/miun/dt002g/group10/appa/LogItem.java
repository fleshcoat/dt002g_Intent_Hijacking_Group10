package se.miun.dt002g.group10.appa;

import android.graphics.Color;

public class LogItem {
  private String date;
  private String logInfo;
  private int textColor;

  public LogItem(String pDate, String pLogInfo) {
    date = pDate;
    logInfo = pLogInfo;
    textColor = Color.BLACK; // Default to black
  }

  public String getDate() {
    return date;
  }

  public String getLogInfo() {
    return logInfo;
  }

  public int getTextColor() { return textColor; }

  public void setTextColor(int color) { this.textColor = color; }
}
