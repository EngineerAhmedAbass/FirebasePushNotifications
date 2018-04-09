package abass.com.firebasepushnotifications.Request;

/**
 * Created by ahmed on 27-Mar-18.
 */

public class Notifications extends NotificationId {
    String Name  , type ;

    public Notifications(){

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

}
