package com.wbjacks.website_notifier.data.dao;

import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.data.models.Website;
import com.wbjacks.website_notifier.util.HibernateUtil;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@PetiteBean("websiteDao")
public class WebsiteDaoImpl implements WebsiteDao {
    private final ObserverDao _observerDao;

    @PetiteInject
    WebsiteDaoImpl(ObserverDao observerDao) {
        _observerDao = observerDao;
    }

    @Override
    public void saveWebsiteObservation(Website website) {
        // NOTE: For now, only a single website is ever requested at once per email.
        Observer observer = website.getObservers().stream().findFirst().get();
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

}
