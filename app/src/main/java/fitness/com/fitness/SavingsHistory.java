package fitness.com.fitness;

/**
 * Created by meghajindal on 05/06/15.
 */

public class SavingsHistory {

    private String _id;

    private String location;

    private Period period;

    private SavingsList savingsLists;

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

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public SavingsList getSavingsLists() {
        return savingsLists;
    }

    public void setSavingsLists(SavingsList savingsLists) {
        this.savingsLists = savingsLists;
    }


}
