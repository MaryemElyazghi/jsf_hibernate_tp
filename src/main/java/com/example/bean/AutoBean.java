package com.example.bean;

import com.example.entity.Auto;
import com.example.entity.User;
import com.example.service.AutoService;
import com.example.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class AutoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Auto selectedAuto = new Auto();
    private List<Auto> autoList;
    private AutoService autoService = new AutoService();
    private UserService userService = new UserService();
    private int selectedUserId;

    @PostConstruct
    public void init() {
        loadAutos();
    }

    // Load all autos
    public void loadAutos() {
        autoList = autoService.getAllAutos();
    }

    // Save auto
    public String saveAuto() {
        User user = userService.getUserById(selectedUserId);
        selectedAuto.setUser(user);
        autoService.saveAuto(selectedAuto);
        selectedAuto = new Auto();
        loadAutos();
        return null;
    }

    // Delete auto
    public String deleteAuto(int id) {
        autoService.deleteAuto(id);
        loadAutos();
        return null;
    }

    // Get all users for dropdown
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Getters and Setters
    public Auto getSelectedAuto() {
        return selectedAuto;
    }

    public void setSelectedAuto(Auto selectedAuto) {
        this.selectedAuto = selectedAuto;
    }

    public List<Auto> getAutoList() {
        return autoList;
    }

    public int getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(int selectedUserId) {
        this.selectedUserId = selectedUserId;
    }
}