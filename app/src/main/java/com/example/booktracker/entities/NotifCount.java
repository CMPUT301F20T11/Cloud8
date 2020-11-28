package com.example.booktracker.entities;

public class NotifCount {
    private long incoming;
    private long accepted;
    public NotifCount(){
        incoming = 0;
        accepted = 0;
    }

    public void setAccepted(long accepted) {
        this.accepted = accepted;
    }

    public long getAccepted() {
        return accepted;
    }

    public long getIncoming() {
        return incoming;
    }

    public void setIncoming(long incoming) {
        this.incoming = incoming;
    }

    public long getTotal(){return  incoming+accepted;}
}
