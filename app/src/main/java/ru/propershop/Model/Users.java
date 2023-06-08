package ru.propershop.Model;

public class Users {
    private String name, phone, password, image, secret;

    public Users() {

    }

    public Users(String name, String phone, String password, String image, String secret) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.secret = secret;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}