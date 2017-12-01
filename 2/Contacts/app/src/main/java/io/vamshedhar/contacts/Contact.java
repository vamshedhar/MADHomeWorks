package io.vamshedhar.contacts;

import java.io.Serializable;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/18/17 2:00 PM.
 * vchinta1@uncc.edu
 */

public class Contact implements Serializable {
    String firstName, lastName, company, email, phone,
            url, address, birthday, nickname, fbURL,
            twitterURL, skype, youtubeChannel, profileImagePath;

    public Contact(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public Contact(String firstName, String lastName, String phone, String profileImagePath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.profileImagePath = profileImagePath;
    }

    public String getFullName(){
        String fname = this.firstName != null ? this.firstName : "";
        String lname = this.lastName != null ? this.lastName : "";
        return fname + " " + lname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        return phone != null ? phone.equals(contact.phone) : contact.phone == null;

    }

    @Override
    public int hashCode() {
        return phone != null ? phone.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
