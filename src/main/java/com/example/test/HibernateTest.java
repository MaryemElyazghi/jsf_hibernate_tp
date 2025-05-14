package com.example.test;

import com.example.entity.Auto;
import com.example.entity.User;
import com.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Classe de test pour vérifier le bon fonctionnement de Hibernate
 * Cette classe permet de tester les opérations CRUD sur les entités User et Auto
 */
public class HibernateTest {

    public static void main(String[] args) {
        // Test de la connexion à la base de données
        testConnection();

        // Test des opérations CRUD sur User
        testUserCRUD();

        // Test des opérations CRUD sur Auto avec relation
        testAutoUserRelation();

        // Test de requêtes HQL
        testHQLQueries();

        // Fermeture de la SessionFactory
        HibernateUtil.shutdown();

        System.out.println("\n*** Tous les tests sont terminés ***");
    }

    /**
     * Test de la connexion à la base de données
     */
    private static void testConnection() {
        System.out.println("\n=== Test de connexion ===");
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            System.out.println("Connection établie avec succès!");
            session.close();
        } catch (Exception e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test des opérations CRUD sur User
     */
    private static void testUserCRUD() {
        System.out.println("\n=== Test des opérations CRUD sur User ===");

        // Création d'un nouvel utilisateur
        User user = new User("Test User", 30);
        Integer userId = null;

        // Create
        System.out.println("Création d'un utilisateur...");
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            userId = (Integer) session.merge(user).getId();
            transaction.commit();
            System.out.println("Utilisateur créé avec ID: " + userId);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("Erreur lors de la création de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
        }

        // Read
        System.out.println("Lecture de l'utilisateur...");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User retrievedUser = session.get(User.class, userId);
            System.out.println("Utilisateur lu: " + retrievedUser);
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
        }

        // Update
        System.out.println("Mise à jour de l'utilisateur...");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User retrievedUser = session.get(User.class, userId);
            retrievedUser.setName("Updated User");
            retrievedUser.setAge(35);
            session.merge(retrievedUser);
            transaction.commit();
            System.out.println("Utilisateur mis à jour: " + retrievedUser);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
        }

        // Delete
        System.out.println("Suppression de l'utilisateur...");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User retrievedUser = session.get(User.class, userId);
            session.remove(retrievedUser);
            transaction.commit();
            System.out.println("Utilisateur supprimé");

            // Vérification de la suppression
            User deletedUser = session.get(User.class, userId);
            if (deletedUser == null) {
                System.out.println("Vérification réussie: l'utilisateur a bien été supprimé");
            } else {
                System.out.println("Erreur: l'utilisateur existe toujours");
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("Erreur lors de la suppression de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test des opérations CRUD sur Auto avec relation User
     */
    private static void testAutoUserRelation() {
        System.out.println("\n=== Test de la relation User-Auto ===");

        Transaction transaction = null;
        Integer userId = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Création d'un utilisateur
            transaction = session.beginTransaction();
            User user = new User("Car Owner", 40);
            userId = (Integer) session.merge(user).getId();

            // Ajout d'autos à l'utilisateur
            Auto auto1 = new Auto("Tesla Model S", "Rouge");
            Auto auto2 = new Auto("Toyota Prius", "Bleu");

            user.addAuto(auto1);
            user.addAuto(auto2);

            session.merge(user);
            transaction.commit();
            System.out.println("Utilisateur avec autos créé, ID: " + userId);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("Erreur lors de la création des relations User-Auto: " + e.getMessage());
            e.printStackTrace();
        }

        // Vérification de la relation
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, userId);
            System.out.println("Utilisateur: " + user.getName());
            System.out.println("Nombre d'autos: " + user.getAutos().size());

            System.out.println("Liste des autos:");
            for (Auto auto : user.getAutos()) {
                System.out.println(" - " + auto.getModel() + " (" + auto.getColor() + ")");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture des relations User-Auto: " + e.getMessage());
            e.printStackTrace();
        }

        // Suppression d'une auto
        System.out.println("Suppression d'une auto...");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, userId);

            if (user.getAutos().size() > 0) {
                Auto autoToRemove = user.getAutos().get(0);
                user.removeAuto(autoToRemove);
                session.merge(user);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("Erreur lors de la suppression d'une auto: " + e.getMessage());
            e.printStackTrace();
        }

        // Vérification après suppression
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, userId);
            System.out.println("Utilisateur après suppression d'une auto:");
            System.out.println("Nombre d'autos: " + user.getAutos().size());

            System.out.println("Liste des autos:");
            for (Auto auto : user.getAutos()) {
                System.out.println(" - " + auto.getModel() + " (" + auto.getColor() + ")");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification après suppression: " + e.getMessage());
            e.printStackTrace();
        }

        // Suppression de l'utilisateur et de ses autos (cascade)
        System.out.println("Suppression de l'utilisateur et de ses autos...");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, userId);
            session.remove(user);
            transaction.commit();
            System.out.println("Utilisateur et ses autos supprimés");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("Erreur lors de la suppression de l'utilisateur et ses autos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test de requêtes HQL
     */
    private static void testHQLQueries() {
        System.out.println("\n=== Test des requêtes HQL ===");

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Création de quelques utilisateurs pour les tests
            User user1 = new User("Alice", 25);
            User user2 = new User("Bob", 30);
            User user3 = new User("Charlie", 35);

            session.merge(user1);
            session.merge(user2);
            session.merge(user3);

            // Ajout d'autos
            Auto auto1 = new Auto("Renault Clio", "Noir");
            Auto auto2 = new Auto("Peugeot 308", "Blanc");
            Auto auto3 = new Auto("Citroën C3", "Rouge");
            Auto auto4 = new Auto("BMW X5", "Gris");

            user1.addAuto(auto1);
            user1.addAuto(auto2);
            user2.addAuto(auto3);
            user3.addAuto(auto4);

            session.merge(user1);
            session.merge(user2);
            session.merge(user3);

            transaction.commit();
            System.out.println("Données de test créées pour les requêtes HQL");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("Erreur lors de la création des données de test: " + e.getMessage());
            e.printStackTrace();
        }

        // Test de requêtes HQL
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Requête 1: Tous les utilisateurs
            System.out.println("\nRequête 1: Tous les utilisateurs");
            Query<User> query1 = session.createQuery("FROM User", User.class);
            List<User> users = query1.list();
            for (User user : users) {
                System.out.println(user);
            }

            // Requête 2: Utilisateurs filtrés par âge
            System.out.println("\nRequête 2: Utilisateurs de plus de 30 ans");
            Query<User> query2 = session.createQuery("FROM User WHERE age > :age", User.class);
            query2.setParameter("age", 30);
            List<User> olderUsers = query2.list();
            for (User user : olderUsers) {
                System.out.println(user);
            }

            // Requête 3: Nombre d'autos par utilisateur
            System.out.println("\nRequête 3: Nombre d'autos par utilisateur");
            Query<Object[]> query3 = session.createQuery(
                    "SELECT u.name, COUNT(a) FROM User u LEFT JOIN u.autos a GROUP BY u.name", Object[].class);
            List<Object[]> results = query3.list();
            for (Object[] result : results) {
                System.out.println("Utilisateur: " + result[0] + ", Nombre d'autos: " + result[1]);
            }

            // Requête 4: Utilisateurs qui possèdent des autos d'une certaine couleur
            System.out.println("\nRequête 4: Utilisateurs avec des autos de couleur Rouge");
            Query<User> query4 = session.createQuery(
                    "SELECT DISTINCT u FROM User u JOIN u.autos a WHERE a.color = :color", User.class);
            query4.setParameter("color", "Rouge");
            List<User> usersWithRedCars = query4.list();
            for (User user : usersWithRedCars) {
                System.out.println(user);
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution des requêtes HQL: " + e.getMessage());
            e.printStackTrace();
        }

        // Nettoyage des données de test
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<?> query = session.createQuery("DELETE FROM Auto");
            query.executeUpdate();
            query = session.createQuery("DELETE FROM User");
            query.executeUpdate();
            transaction.commit();
            System.out.println("\nDonnées de test nettoyées");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("Erreur lors du nettoyage des données de test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}