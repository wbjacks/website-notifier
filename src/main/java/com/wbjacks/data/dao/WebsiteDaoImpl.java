package com.wbjacks.data.dao;

import com.wbjacks.data.models.Observer;
import com.wbjacks.data.models.Website;
import jodd.petite.meta.PetiteBean;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Optional;

@PetiteBean
public class WebsiteDaoImpl implements WebsiteDao {
    WebsiteDaoImpl() {
    }

    @Override
    public void saveWebsiteInsideDbSession(Website website, Observer observer, Session session, boolean isObserverExisting) {
        Website existingWebsite = (Website) session.createCriteria(Website.class).add(Restrictions.eq("url", website
                .getUrl())).uniqueResult();
        website.getObservers().clear();
        website.getObservers().add(observer);
        if (existingWebsite == null) {
            observer.getWebsites().add(website);
        } else {
            if (isWebsiteNotPresentInObserver(existingWebsite, observer)) {
                existingWebsite.getObservers().add(observer);
            }
        }
    }

    private boolean isWebsiteNotPresentInObserver(Website website, Observer observer) {
        return !observer.getWebsites().stream().filter(observerWebsite -> observerWebsite.getUrl().equals(website
                .getUrl())).findFirst().isPresent();
    }
}
