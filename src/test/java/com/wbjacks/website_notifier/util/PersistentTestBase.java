package com.wbjacks.website_notifier.util;

import org.junit.After;

import static com.wbjacks.website_notifier.util.HibernateUtil.shutdown;

public class PersistentTestBase {
    @After
    public void afterClass() {
        shutdown();
    }
}
