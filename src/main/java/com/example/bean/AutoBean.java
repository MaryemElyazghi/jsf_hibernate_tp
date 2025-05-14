package com.example.bean;

import com.example.entity.Auto;
import com.example.entity.User;
import com.example.service.AutoService;
import com.example.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class AutoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Auto selectedAuto = new Auto();
    private List<Auto> autoList;
    private AutoService autoService = new AutoService();
    private UserService userService = new UserService();
    private int selectedUserId;
    private boolean editing = false;  // Added editing flag

    @PostConstruct
    public void init() {
        loadAutos();
    }

    // Load all autos
    public void loadAutos() {
        autoList = autoService.getAllAutos();
    }

    // Save auto (create or update)
    public String saveAuto() {
        User user = userService.getUserById(selectedUserId);
        selectedAuto.setUser(user);
        autoService.saveAuto(selectedAuto);
        selectedAuto = new Auto();
        loadAutos();
        editing = false;  // Reset editing flag after save
        return null;
    }

    // Delete auto
    public String deleteAuto(int id) {
        autoService.deleteAuto(id);
        loadAutos();
        return null;
    }

    // Edit auto (added new method)
    public String editAuto(Auto auto) {
        // Get a fresh copy from the database
        selectedAuto = autoService.getAutoById(auto.getId());

        // Set the selectedUserId to maintain the dropdown selection
        if (selectedAuto.getUser() != null) {
            selectedUserId = selectedAuto.getUser().getId();
        }

        editing = true;
        return null;
    }

    // Cancel edit (added new method)
    public String cancelEdit() {
        selectedAuto = new Auto();
        selectedUserId = 0;
        editing = false;
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

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }
}