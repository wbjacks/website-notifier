package com.wbjacks.website_notifier.data.dao;

import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.data.models.Website;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@PetiteBean("observerDao")
public class ObserverDaoImpl implements ObserverDao {

    @PetiteInject
    ObserverDaoImpl() {
    }

    @Override
    public void saveObserverInsideDbSession(Website website, Observer observer, Session session) {
        Observer existingObserver = (Observer) session.createCriteria(Observer.class).add(Restrictions.eq
                ("email", observer.getEmail())).uniqueResult();
        if (existingObserver == null) {
            website.getObservers().add(observer);
        } else {
            if (isObserverNotPresentInWebsite(website, existingObserver)) {
                website.getObservers().add(existingObserver);
            }
        }
    }

    private boolean isObserverNotPresentInWebsite(Website website, Observer observer) {
        return !website.getObservers().stream().filter(websitesObserver -> websitesObserver.getEmail().equals
                (observer.getEmail())).findFirst().isPresent();
    }
}
