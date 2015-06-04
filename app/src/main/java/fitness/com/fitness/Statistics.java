package fitness.com.fitness;

/**
 * Created by meghajindal on 04/06/15.
 */

public class Statistics {

    private String _id;

    private String location;

    private String lastUpdatedDate;

    private String balance;

    private Period period;

    private ActivityList activityList;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public ActivityList getActivityList() {
        return activityList;
    }

    public void setActivityList(ActivityList activityList) {
        this.activityList = activityList;
    }

}
