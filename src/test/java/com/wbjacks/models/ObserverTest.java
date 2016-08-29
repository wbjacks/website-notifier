package com.wbjacks.models;

import com.wbjacks.PersistentTestBase;
import org.hibernate.Transaction;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ObserverTest extends PersistentTestBase {
    @Test
    public void testPersistence() {
        Observer observer = new Observer();
        Website website = new Website("www.foo.com");
        observer.setEmail("foo@bar.com");
        observer.getWebsites().add(website);
        website.getObservers().add(observer);
        Transaction transaction = _session.beginTransaction();
        _session.save(website);
        _session.save(observer);
        transaction.commit();
        assertEquals(1, _session.createCriteria(Observer.class).list().size());
    }
}