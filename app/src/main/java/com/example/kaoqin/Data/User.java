package com.example.kaoqin.Data;

import org.litepal.crud.DataSupport;

public class User extends DataSupport {
    private int id;
    private String account;
    private String password;
    private String checkindate;
    private boolean ifcheckin;
    private boolean ifcheckout;
    private String checkintime;
    private String checkouttime;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCheckindate(){
        return checkindate;
    }

    public void setCheckindate(String checkindate){
        this.checkindate = checkindate;
    }

    public boolean getIfcheckin(){
        return ifcheckin;
    }

    public void setIfcheckin(boolean ifcheckin){
        this.ifcheckin = ifcheckin;
    }

    public boolean getIfcheckout(){
        return ifcheckout;
    }

    public void setIfcheckout(boolean ifcheckout){
        this.ifcheckout = ifcheckout;
    }

    public String getCheckintime(){
        return checkintime;
    }

    private void setCheckintime(String checkintime){
        this.checkintime = checkintime;
    }

    public String getCheckouttime(){
        return checkouttime;
    }

    private void setCheckouttime(String checkouttime){
        this.checkouttime = checkouttime;
    }
}
