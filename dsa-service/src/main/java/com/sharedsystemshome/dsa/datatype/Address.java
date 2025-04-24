package com.sharedsystemshome.dsa.datatype;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Address {

    private Map address;

    public Address(Map address) {

        this.address = address;

        this.initialiseDefaultValues();

    }

    public Address() {

        this.initialiseDefaultValues();
    }

    public Address(
            String addressLine1,
            String addressLine2,
            String addressLine3,
            String addressLine4,
            String addressLine5,
            String postalCode

    ) {
        this.address = new TreeMap<>();

        this.setAddressLine1(addressLine1);
        this.setAddressLine2(addressLine2);
        this.setAddressLine3(addressLine3);
        this.setAddressLine4(addressLine4);
        this.setAddressLine5(addressLine5);
        this.setPostalCode(postalCode);

        this.initialiseDefaultValues();
    }

    // First line of address and postal code
    public Address(
            String addressLine1,
            String postalCode
    ) {
        this.address = new TreeMap<>();

        this.setAddressLine1(addressLine1);
        this.setAddressLine2("");
        this.setAddressLine3("");
        this.setAddressLine4("");
        this.setAddressLine5("");
        this.setPostalCode(postalCode);

        this.initialiseDefaultValues();
    }

    private void initialiseDefaultValues() {

        if (null == this.address) {
            this.address = new TreeMap<>();

            this.setAddressLine1("");
            this.setAddressLine2("");
            this.setAddressLine3("");
            this.setAddressLine4("");
            this.setAddressLine5("");
            this.setPostalCode("");
        }
    }

    @JsonIgnore
    public Map getAddress() {
        return this.address;
    }

    public String getAddressLine1() {
        return (String) this.address.get("addressLine1");
    }

    public String getAddressLine2() {
        return (String) this.address.get("addressLine2");
    }

    public String getAddressLine3() {
        return (String) this.address.get("addressLine3");
    }

    public String getAddressLine4() {
        return (String) this.address.get("addressLine4");
    }

    public String getAddressLine5() {
        return (String) this.address.get("addressLine5");
    }

    public String getPostalCode() {
        return (String) this.address.get("postalCode");
    }

    public void setAddressLine1(String addressLine) {
        this.address.put("addressLine1", addressLine);
    }

    public void setAddressLine2(String addressLine) {
        this.address.put("addressLine2", addressLine);
    }

    public void setAddressLine3(String addressLine) {
        this.address.put("addressLine3", addressLine);
    }

    public void setAddressLine4(String addressLine) {
        this.address.put("addressLine4", addressLine);
    }

    public void setAddressLine5(String addressLine) {
        this.address.put("addressLine5", addressLine);
    }

    public void setPostalCode(String postalCode) {
        this.address.put("postalCode", postalCode);
    }


}




