package fitness.com.fitness;

/**
 * Created by meghajindal on 05/06/15.
 */
public class Savings {

    private String balance;
    private String periodValueName ;
    private ActivityList activityList;
    public String getBalance() {
        return balance;
    }
    public void setBalance(String balance) {
        this.balance = balance;
    }
    public String getPeriodValueName() {
        return periodValueName;
    }
    public void setPeriodValueName(String periodValueName) {
        this.periodValueName = periodValueName;
    }
    public ActivityList getActivityList() {
        return activityList;
    }
    public void setActivityList(ActivityList activityList) {
        this.activityList = activityList;
    }

}
