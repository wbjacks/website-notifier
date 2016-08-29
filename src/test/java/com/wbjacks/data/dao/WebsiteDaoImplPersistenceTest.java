package com.wbjacks.data.dao;

import com.wbjacks.data.models.Observer;
import com.wbjacks.data.models.Website;
import com.wbjacks.util.HibernateUtil;
import com.wbjacks.util.PersistentTestBase;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WebsiteDaoImplPersistenceTest extends PersistentTestBase {
    @Test
    public void testSaveObserverSavesNewObserverAndNewWebsite() {
        Observer observer = Observer.forEmail("foo@bar.com");
        Website website = Website.forUrl("www.foo.com");
        website.getObservers().add(observer);

        WebsiteDaoImpl websiteDao = new WebsiteDaoImpl(new ObserverDaoImpl());
        websiteDao.saveWebsiteObservation(website);

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
        website1.getObservers().add(observer1);

        WebsiteDaoImpl websiteDao = new WebsiteDaoImpl(new ObserverDaoImpl());
        websiteDao.saveWebsiteObservation(website1);

        Observer observer2 = Observer.forEmail("bar@bar.com");
        Website website2 = Website.forUrl("www.foo.com");
        website2.getObservers().add(observer2);
        websiteDao.saveWebsiteObservation(website2);

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
        website1.getObservers().add(observer1);

        WebsiteDaoImpl websiteDao = new WebsiteDaoImpl(new ObserverDaoImpl());
        websiteDao.saveWebsiteObservation(website1);

        Observer observer2 = Observer.forEmail("foo@bar.com");
        Website website2 = Website.forUrl("www.baz.com");
        website2.getObservers().add(observer2);
        websiteDao.saveWebsiteObservation(website2);

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
        website1.getObservers().add(observer1);

        WebsiteDaoImpl websiteDao = new WebsiteDaoImpl(new ObserverDaoImpl());
        websiteDao.saveWebsiteObservation(website1);

        Observer observer2 = Observer.forEmail("foo@bar.com");
        Website website2 = Website.forUrl("www.foo.com");
        website2.getObservers().add(observer2);
        websiteDao.saveWebsiteObservation(website2);

        Session session = HibernateUtil.getReadOnlySession();
        Transaction transaction = session.beginTransaction();
        assertEquals(1, session.createCriteria(Observer.class).list().size());
        assertEquals(1, session.createCriteria(Website.class).list().size());
        transaction.commit();
        session.close();
    }
}