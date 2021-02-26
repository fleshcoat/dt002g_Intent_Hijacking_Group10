package se.miun.dt002g.group10.appc;

public class LogItem {
  private String date;
  private String logInfo;

  public LogItem(String pDate, String pLogInfo) {
    date = pDate;
    logInfo = pLogInfo;
  }

  public String getDate() {
    return date;
  }

  public String getLogInfo() {
    return logInfo;
  }
}
