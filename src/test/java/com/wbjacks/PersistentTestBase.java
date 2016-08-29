package com.wbjacks;

import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import static com.wbjacks.util.HibernateUtil.getSessionFactory;
import static com.wbjacks.util.HibernateUtil.shutdown;

public class PersistentTestBase {

    protected static Session _session;

    @BeforeClass
    public static void beforeClass() {
        _session = getSessionFactory().openSession();
    }

    @AfterClass
    public static void afterClass() {
        shutdown();
    }
}
