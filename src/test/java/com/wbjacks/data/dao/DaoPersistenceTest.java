package com.wbjacks.data.dao;

import com.wbjacks.data.models.Observer;
import com.wbjacks.data.models.Website;
import com.wbjacks.util.HibernateUtil;
import com.wbjacks.util.PersistentTestBase;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DaoPersistenceTest extends PersistentTestBase {
    @Test
    public void testSaveObserverSavesNewObserverAndNewWebsite() {
        Observer observer = Observer.forEmail("foo@bar.com");
        Website website = Website.forUrl("www.foo.com");
        observer.getWebsites().add(website);
        website.getObservers().add(observer);

        ObserverDaoImpl observerDao = new ObserverDaoImpl(new WebsiteDaoImpl());
        observerDao.saveObserver(observer);
        Session session = HibernateUtil.getReadOnlySession();
        Transaction transaction = session.beginTransaction();
        assertEquals(1, session.createCriteria(Observer.class).list().size());
        assertEquals(1, session.createCriteria(Website.class).list().size());
        transaction.commit();
        session.close();
    }

    @Test
    public void testSaveObserverSavesNewObserverAndExistingWebsite() {
        Observer observer1 = Observer.forEmail("foo@bar.com");
        Website website1 = Website.forUrl("www.foo.com");
        observer1.getWebsites().add(website1);
        website1.getObservers().add(observer1);

        ObserverDaoImpl observerDao = new ObserverDaoImpl(new WebsiteDaoImpl());
        observerDao.saveObserver(observer1);

        Observer observer2 = Observer.forEmail("bar@bar.com");
        Website website2 = Website.forUrl("www.foo.com");
        observer2.getWebsites().add(website2);
        website2.getObservers().add(observer2);
        observerDao.saveObserver(observer2);

        Session session = HibernateUtil.getReadOnlySession();
        Transaction transaction = session.beginTransaction();
        assertEquals(2, session.createCriteria(Observer.class).list().size());
        assertEquals(1, session.createCriteria(Website.class).list().size());
        transaction.commit();
        session.close();
    }

    @Test
    public void testSaveObserverSavesExistingObserverAndNewWebsite() {
        Observer observer1 = Observer.forEmail("foo@bar.com");
        Website website1 = Website.forUrl("www.foo.com");
        observer1.getWebsites().add(website1);
        website1.getObservers().add(observer1);

        ObserverDaoImpl observerDao = new ObserverDaoImpl(new WebsiteDaoImpl());
        observerDao.saveObserver(observer1);

        Observer observer2 = Observer.forEmail("foo@bar.com");
        Website website2 = Website.forUrl("www.baz.com");
        observer2.getWebsites().add(website2);
        website2.getObservers().add(observer2);
        observerDao.saveObserver(observer2);

        Session session = HibernateUtil.getReadOnlySession();
        Transaction transaction = session.beginTransaction();
        assertEquals(1, session.createCriteria(Observer.class).list().size());
        assertEquals(2, session.createCriteria(Website.class).list().size());
        transaction.commit();
        session.close();
    }

    @Test
    public void testSaveObserverSavesExistingObserverAndExistingWebsite() {
        Observer observer1 = Observer.forEmail("foo@bar.com");
        Website website1 = Website.forUrl("www.foo.com");
        observer1.getWebsites().add(website1);
        website1.getObservers().add(observer1);

        ObserverDaoImpl observerDao = new ObserverDaoImpl(new WebsiteDaoImpl());
        observerDao.saveObserver(observer1);

        Observer observer2 = Observer.forEmail("foo@bar.com");
        Website website2 = Website.forUrl("www.foo.com");
        observer2.getWebsites().add(website2);
        website2.getObservers().add(observer2);
        observerDao.saveObserver(observer2);

        Session session = HibernateUtil.getReadOnlySession();
        Transaction transaction = session.beginTransaction();
        assertEquals(1, session.createCriteria(Observer.class).list().size());
        assertEquals(1, session.createCriteria(Website.class).list().size());
        transaction.commit();
        session.close();
    }
}