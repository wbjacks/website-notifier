package com.wbjacks.website_notifier.data.dao;

import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.data.models.Website;
import com.wbjacks.website_notifier.util.HibernateUtil;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collection;

@PetiteBean("websiteDao")
class WebsiteDaoImpl implements WebsiteDao {
    private final ObserverDao _observerDao;

    @PetiteInject
    WebsiteDaoImpl(ObserverDao observerDao) {
        _observerDao = observerDao;
    }

    @Override
    public void saveWebsiteObservation(Website website) {
        // NOTE: For now, only a single website is ever requested at once per email.
        @SuppressWarnings("OptionalGetWithoutIsPresent") Observer observer = website.getObservers().stream()
                .findFirst().get();
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Website existingWebsite = (Website) session.createCriteria(Website.class).add(Restrictions.eq("url",
                    website.getUrl())).uniqueResult();
            if (existingWebsite == null) {
                website.getObservers().clear(); // observerDao will add website
                _observerDao.saveObserverInsideDbSession(website, observer, session);
                session.save(website);
            } else {
                _observerDao.saveObserverInsideDbSession(existingWebsite, observer, session);
            }
        } catch (HibernateException e) {
            transaction.rollback();
            throw e;
        }
        transaction.commit();
        session.close();

    }

    @Override
    @SuppressWarnings("unchecked") // Criteria API is not type-safe
    public Collection<Website> getAllWebsites() {
        Session session = HibernateUtil.getReadOnlySession();
        Transaction transaction = session.beginTransaction();
        Collection<Website> allWebsites = new ArrayList<>();
        try {
            allWebsites.addAll(session.createCriteria(Website.class).list());
        } catch (HibernateException e) {
            transaction.rollback();
            throw e;
        }
        transaction.commit();
        session.close();
        return allWebsites;
    }

    @Override
    public Website getByUrl(String url) {
        Session session = HibernateUtil.getReadOnlySession();
        Transaction transaction = session.beginTransaction();
        Website website;
        try {
            website = (Website) session.createCriteria(Website.class).add(Restrictions.eq("url", url)).uniqueResult();
            Hibernate.initialize(website.getObservers());

        } catch (HibernateException e) {
            transaction.rollback();
            throw e;
        }
        transaction.commit();
        session.close();
        return website;
    }

    @Override
    public void updateWebsiteHash(long websiteId, String hash) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Website website = session.get(Website.class, websiteId);
            website.setHash(hash);
        } catch (HibernateException e) {
            transaction.rollback();
            throw e;
        }
        transaction.commit();
        session.close();
    }
}
