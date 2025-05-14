package com.example.service;



import com.example.entity.Auto;
import com.example.entity.User;
import com.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class AutoService {

    // Create or Update auto
    public void saveAuto(Auto auto) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(auto);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Get auto by id
    public Auto getAutoById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Auto.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all autos
    public List<Auto> getAllAutos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Auto> query = session.createQuery("FROM Auto", Auto.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get autos by user
    public List<Auto> getAutosByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Auto> query = session.createQuery("FROM Auto WHERE user.id = :userId", Auto.class);
            query.setParameter("userId", user.getId());
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Delete auto
    public void deleteAuto(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Auto auto = session.get(Auto.class, id);
            if (auto != null) {
                session.remove(auto);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}