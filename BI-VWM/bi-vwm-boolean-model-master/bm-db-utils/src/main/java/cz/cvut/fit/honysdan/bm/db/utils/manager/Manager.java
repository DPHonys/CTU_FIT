package cz.cvut.fit.honysdan.bm.db.utils.manager;

import cz.cvut.fit.honysdan.bm.db.utils.entity.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Manager {
    protected SessionFactory sessionFactory;

    // create connection to database
    public void setup() throws ExceptionInInitializerError {
            sessionFactory = new Configuration().configure()
                    .addAnnotatedClass(Article.class)
                    .addAnnotatedClass(Term.class)
                    .buildSessionFactory();
    }

    // close connection to database
    public void exit() throws HibernateException {
        sessionFactory.close();
    }

    // save object to database
    public void createObject(Object object) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.save(object);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    // updates the count of occurrences
    public void updateTermCount(int id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Term term = session.get(Term.class, id);
            int ret = term.getCount() + 1;
            term.setCount(ret);
            session.update(term);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
