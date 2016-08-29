package com.wbjacks.data.dao;

import com.wbjacks.data.models.Observer;
import com.wbjacks.data.models.Website;
import com.wbjacks.util.HibernateUtil;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@PetiteBean("observerDao")
public class ObserverDaoImpl implements ObserverDao {
    private final WebsiteDao _websiteDao;

    @PetiteInject
    ObserverDaoImpl(WebsiteDao websiteDao) {
        _websiteDao = websiteDao;
    }

    @Override
    public void saveObserver(Observer observer) throws HibernateException {
        Website website = observer.getWebsites().stream().findFirst().get();
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Observer existingObserver = (Observer) session.createCriteria(Observer.class).add(Restrictions.eq
                    ("email", observer.getEmail())).uniqueResult();
            if (existingObserver == null) {
                observer.getWebsites().clear(); // websiteDao will add website
                _websiteDao.saveWebsiteInsideDbSession(website, observer, session, false);
                session.save(observer);
            } else {
                _websiteDao.saveWebsiteInsideDbSession(website, existingObserver, session, true);
            }
            // NOTE: For now, only a single website is ever requested at once per email.
        } catch (HibernateException e) {
            transaction.rollback();
            throw e;
        }
        transaction.commit();
        session.close();
    }
}
