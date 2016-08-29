package com.wbjacks.util;

import com.wbjacks.data.models.Observer;
import com.wbjacks.data.models.Website;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

// TODO: (wbjacks) Use reflection to add classes
// NOTE: Comes from previous project, see https://github.com/wbjacks/photor
public class HibernateUtil {
    private static SessionFactory _sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Observer
                .class).addAnnotatedClass(Website.class);
        StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
        serviceRegistryBuilder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static SessionFactory getSessionFactory() {
        if (_sessionFactory == null || _sessionFactory.isClosed()) {
            _sessionFactory = buildSessionFactory();
        }
        ;
        return _sessionFactory;
    }

    // As there are only a few DB transactions in this app, I'm letting the DAOs handle the session rather than
    // implementing a Transactor pattern to keep things simple.
    public static Session getSession() {
        Session session = getSessionFactory().openSession();
        session.setDefaultReadOnly(false);
        return session;
    }

    public static Session getReadOnlySession() {
        Session session = getSessionFactory().openSession();
        session.setDefaultReadOnly(true);
        return session;
    }

    static void shutdown() {
        getSessionFactory().close();
    }

}