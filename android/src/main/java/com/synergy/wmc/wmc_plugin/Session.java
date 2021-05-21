package com.synergy.wmc.wmc_plugin;

public class Session {
    public enum Transport{
        wifi,mobile
    }

    private Transport transport;
    private String gw;
    private String username;
    private String password;
    private String sessionid;
    private long created;
    private long expire_seconds;


    public long getExpire_seconds() {
        return expire_seconds;
    }

    public void setExpire_seconds(long expire_seconds) {
        this.expire_seconds = expire_seconds;
    }

    public String getGw() {
        return gw;
    }

    public void setGw(String gw) {
        this.gw = gw;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public int getPort() {
        String gw = this.gw.replace("http://", "").replace("https://", "");
        if (gw.indexOf(":") >= 0) {
            return Integer.parseInt(gw.substring(gw.indexOf(":") + 1));
        }
        return 0;
    }

    public String getURL() {
        String gw = this.gw.replace("http://", "").replace("https://", "");
        if (gw.indexOf(":") >= 0) {
            return "http://"+gw.substring(0, gw.indexOf(":"));
        }
        return "";
    }


    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Session{" +
                "transport=" + transport +
                ", gw='" + gw + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sessionid='" + sessionid + '\'' +
                ", created=" + created +
                ", expire_seconds=" + expire_seconds +
                '}';
    }
}
