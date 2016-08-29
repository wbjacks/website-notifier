package com.wbjacks.util;

import org.junit.After;

import static com.wbjacks.util.HibernateUtil.shutdown;

public class PersistentTestBase {
    @After
    public void afterClass() {
        shutdown();
    }
}
