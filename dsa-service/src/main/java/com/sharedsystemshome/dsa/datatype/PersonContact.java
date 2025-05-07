package com.sharedsystemshome.dsa.datatype;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;
import java.util.TreeMap;

public class PersonContact {

    private Map<String, String> personContact;

    public PersonContact(Map<String, String> personContact) {
        this.personContact = personContact;
        this.initialiseDefaultValues();
    }

    public PersonContact() {
        this.initialiseDefaultValues();
    }

    public PersonContact(String name, String email, String phone) {
        this.personContact = new TreeMap<>();
        this.setName(name);
        this.setEmail(email);
        this.setPhone(phone);
        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues() {
        if (this.personContact == null) {
            this.personContact = new TreeMap<>();
        }

        this.personContact.putIfAbsent("name", "");
        this.personContact.putIfAbsent("email", "");
        this.personContact.putIfAbsent("phone", "");
    }

    @JsonIgnore
    public Map<String, String> getPersonContact() {
        return this.personContact;
    }

    public String getName() {
        return this.personContact.get("name");
    }

    public String getEmail() {
        return this.personContact.get("email");
    }

    public String getPhone() {
        return this.personContact.get("phone");
    }

    public void setName(String name) {
        this.personContact.put("name", name);
    }

    public void setEmail(String email) {
        this.personContact.put("email", email);
    }

    public void setPhone(String phone) {
        this.personContact.put("phone", phone);
    }
}
