package com.example.bean;

import com.example.entity.User;
import com.example.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private User selectedUser = new User();
    private List<User> userList;
    private UserService userService = new UserService();
    private boolean editing = false;

    @PostConstruct
    public void init() {
        loadUsers();
    }

    // Load all users
    public void loadUsers() {
        userList = userService.getAllUsers();
    }

    // Save user
    public String saveUser() {
        // La méthode merge gère automatiquement la création/mise à jour
        // mais nous devons nous assurer que l'ID est correct

        if (!editing && selectedUser.getId() != 0) {
            selectedUser.setId(0); // Forcer uniquement si l'utilisateur n'est pas en mode édition
        }


        userService.saveUser(selectedUser);
        selectedUser = new User();
        loadUsers();
        editing = false;
        return null;
    }

    // Delete user
    public String deleteUser(int id) {
        userService.deleteUser(id);
        loadUsers();
        return null;
    }

    // Begin edit user
    public String editUser(User user) {
        // Récupérer une copie fraîche de l'utilisateur de la base de données
        // au lieu d'utiliser directement la référence de la liste
        selectedUser = userService.getUserById(user.getId());
        editing = true;
        return null;
    }

    // Cancel edit
    public String cancelEdit() {
        selectedUser = new User();
        editing = false;
        return null;
    }

    // Prepare new user
    public String prepareNewUser() {
        selectedUser = new User();
        editing = false;
        return null;
    }

    // Getters and Setters
    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<User> getUserList() {
        return userList;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }
}