package model.pojo;

import java.time.LocalDateTime;

public class Notification implements Comparable<Notification> {

    private long id;
    private User reciever;
    private User causer;
    private String description;
    private LocalDateTime date;
    private boolean seen;

    //New notification creation
    public Notification(User reciever, User causer, String description){
        this.reciever = reciever;
        this.causer = causer;
        this.description = description;
        this.date = LocalDateTime.now();
        this.seen = false;
    }

    //Notification creation from DB
    public Notification(long id, User reciever, User causer, String description, LocalDateTime date, boolean seen) {
        this.id = id;
        this.reciever = reciever;
        this.causer = causer;
        this.description = description;
        this.date = date;
        this.seen = seen;
    }

    //========================== Setters ==========================//

    public void setId(long id) {
        this.id = id;
    }

    //========================== Getters ==========================//

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public boolean isSeen() {
        return seen;
    }

    public User getReciever() {
        return reciever;
    }

    public User getCauser() {
        return causer;
    }

    @Override
    public int compareTo(Notification notification) {
        return this.date.compareTo(notification.date) > 0 ? -1 : 1;
    }
}
