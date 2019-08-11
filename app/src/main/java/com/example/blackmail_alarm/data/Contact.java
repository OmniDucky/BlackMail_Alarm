package com.example.blackmail_alarm.data;

public class Contact implements Comparable<Contact>{
    private String name;
    private String phoneNumber;
    public Contact(String name,String phoneNumber)
    {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    public String getName()
    {
        return name;
    }
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    @Override
    public int compareTo(Contact o) {
        return this.name.compareTo(o.name);
    }
}
