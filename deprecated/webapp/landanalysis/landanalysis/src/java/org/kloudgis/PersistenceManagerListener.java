package org.kloudgis;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.persistence.EntityManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.kloudgis.core.persistence.IPersistenceUnitListener;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.feature.AttrTypeDbEntity;
import org.kloudgis.core.persistence.feature.FeatureTypeDbEntity;
import org.kloudgis.core.persistence.feature.PriorityDbEntity;

/**
 * Context listener to listener to persistence manager.
 * This is a workaround to create the spatial indexes on geometry columns.  It's not supported by annotations so far.
 * @author jeanfelixg
 */
public class PersistenceManagerListener implements IPersistenceUnitListener, ServletContextListener {

    private Timer timer = new Timer();

    public void persistenceUnitCreated(String pu) {
        if (pu.equals(PersistenceManager.DATA_PU)) {
            EntityManager em = PersistenceManager.getInstance().getEntityManager(pu);
            try {
                em.getTransaction().begin();
                em.createNativeQuery("CREATE INDEX note_gist_ix ON note USING gist(geom)").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
            }
            em.close();
        } else if (pu.equals(PersistenceManager.ADMIN_PU)) {
            EntityManager em = PersistenceManager.getInstance().getEntityManager(pu);
            try {
                //note
                validateNote(em);
                validateArpenteur(em);
                validatePlaceItemFt("Limit", "limit_items", em, true, "category");
                validatePlaceItemFt("Road", "rue", em, false, null);
                validatePlaceItemFt("Hydro", "hydro", em, false, null);
                validatePlaceItemFt("Lot", "lot", em, false, null);
                validateGoogle(em);
            } catch (Exception e) {
            } finally {
                em.close();
            }
        }
    }

    private void validatePlaceItemFt(String ftName, String table, EntityManager em, boolean chart, String priority) {
        try {
            List result = em.createQuery("from FeatureTypeDbEntity where name=:ft").setParameter("ft", ftName).getResultList();
            FeatureTypeDbEntity ft = null;
            if (result == null || result.isEmpty()) {
                em.getTransaction().begin();
                ft = new FeatureTypeDbEntity();
                ft.setName(ftName);
                ft.setLabel("_" + ftName);
                ft.setTableName(table);
                ft.setClassName("Landanalysis." + ftName);
                ft.setHasGeometry(Boolean.TRUE);
                ft.setIdAttr("id");
                ft.setSearchable(Boolean.TRUE);
                ft.setSelectionable(Boolean.TRUE);
                ft.setPrioritySelectionAttribute(priority);
                ft.setHasChart(chart);
                em.persist(ft);
                em.getTransaction().commit();
            } else {
                ft = (FeatureTypeDbEntity) result.get(0);
            }
            if (ft != null) {
                List attrs = em.createQuery("from AttrTypeDbEntity where featuretype_id=:ft").setParameter("ft", ft.getId()).getResultList();
                if (attrs == null || attrs.isEmpty()) {
                    em.getTransaction().begin();
                    AttrTypeDbEntity atTitle = new AttrTypeDbEntity();
                    atTitle.setName("title");
                    atTitle.setLabel("_" + ftName + "_title");
                    atTitle.setHint("_" + ftName + "_title_hint");
                    ft.addAttrType(atTitle);
                    em.persist(atTitle);
                    AttrTypeDbEntity atDescr = new AttrTypeDbEntity();
                    atDescr.setName("description");
                    atDescr.setLabel("_" + ftName + "_description");
                    atDescr.setHint("_" + ftName + "_description_hint");
                    ft.addAttrType(atDescr);
                    em.persist(atDescr);
                    AttrTypeDbEntity atCategory = new AttrTypeDbEntity();
                    atCategory.setName("category");
                    atCategory.setLabel("_" + ftName + "_category");
                    atCategory.setHint("_" + ftName + "_category_hint");
                    ft.addAttrType(atCategory);
                    em.persist(atCategory);
                    if (priority != null && priority.equals(atCategory.getName())) {
                        validateSelectionPriority(em, atCategory, ftName);
                    }
                    AttrTypeDbEntity atSubCategory = new AttrTypeDbEntity();
                    atSubCategory.setName("subcategory");
                    atSubCategory.setLabel("_" + ftName + "_subcategory");
                    atSubCategory.setHint("_" + ftName + "_subcategory_hint");
                    ft.addAttrType(atSubCategory);
                    em.persist(atSubCategory);
                    em.getTransaction().commit();
                }
            }
        } catch (Exception ex) {//silent ex
        }
    }

    private void validateNote(EntityManager em) {
        List result = em.createQuery("from FeatureTypeDbEntity where name=:ft").setParameter("ft", "Note").getResultList();
        FeatureTypeDbEntity ftNote = null;
        if (result == null || result.isEmpty()) {
            em.getTransaction().begin();
            ftNote = new FeatureTypeDbEntity();
            ftNote.setName("Note");
            ftNote.setLabel("_Note");
            ftNote.setTableName("note");
            ftNote.setHasGeometry(Boolean.TRUE);
            ftNote.setIdAttr("id");
            ftNote.setSearchable(Boolean.TRUE);
            ftNote.setSelectionable(Boolean.TRUE);
            ftNote.setClassName("Landanalysis.Note");
            em.persist(ftNote);
            em.getTransaction().commit();
        } else {
            ftNote = (FeatureTypeDbEntity) result.get(0);
        }
        List attrs = em.createQuery("from AttrTypeDbEntity where featuretype_id=:ft").setParameter("ft", ftNote.getId()).getResultList();
        if (attrs == null || attrs.isEmpty()) {
            em.getTransaction().begin();
            AttrTypeDbEntity atTitle = new AttrTypeDbEntity();
            atTitle.setName("title");
            atTitle.setLabel("_" + ftNote.getName() + "_title");
            atTitle.setHint("_" + ftNote.getName() + "_title_hint");
            atTitle.setEditable(true);
            ftNote.addAttrType(atTitle);
            em.persist(atTitle);
            AttrTypeDbEntity atDescr = new AttrTypeDbEntity();
            atDescr.setName("description");
            atDescr.setLabel("_" + ftNote.getName() + "_description");
            atDescr.setHint("_" + ftNote.getName() + "_description_hint");
            atDescr.setEditable(true);
            ftNote.addAttrType(atDescr);
            em.persist(atDescr);
            em.getTransaction().commit();
        }
    }

    private void validateArpenteur(EntityManager em) {
        //arpenteur
        List result = em.createQuery("from FeatureTypeDbEntity where name=:ft").setParameter("ft", "Arpenteur").getResultList();
        FeatureTypeDbEntity ft = null;
        if (result == null || result.isEmpty()) {
            em.getTransaction().begin();
            ft = new FeatureTypeDbEntity();
            ft.setName("Arpenteur");
            ft.setLabel("_Arpenteur");
            ft.setTableName("arpenteur");
            ft.setHasGeometry(Boolean.TRUE);
            ft.setIdAttr("fid");
            ft.setSearchable(Boolean.TRUE);
            ft.setSelectionable(Boolean.TRUE);
            ft.setClassName("Landanalysis.Arpenteur");
            em.persist(ft);
            em.getTransaction().commit();
        } else {
            ft = (FeatureTypeDbEntity) result.get(0);
        }

        List attrs = em.createQuery("from AttrTypeDbEntity where featuretype_id=:ft").setParameter("ft", ft.getId()).getResultList();
        if (attrs == null || attrs.isEmpty()) {
            em.getTransaction().begin();
            AttrTypeDbEntity at1 = new AttrTypeDbEntity();
            at1.setName("firstname");
            at1.setLabel("_" + ft.getName() + "_firstname");
            at1.setHint("_" + ft.getName() + "_firstname_hint");
            ft.addAttrType(at1);
            em.persist(at1);
            AttrTypeDbEntity at2 = new AttrTypeDbEntity();
            at2.setName("lastname");
            at2.setLabel("_" + ft.getName() + "_lastname");
            at2.setHint("_" + ft.getName() + "_lastname_hint");
            ft.addAttrType(at2);
            em.persist(at2);
            AttrTypeDbEntity at3 = new AttrTypeDbEntity();
            at3.setName("postalcode");
            at3.setLabel("_" + ft.getName() + "_postalcode");
            at3.setHint("_" + ft.getName() + "_postalcode_hint");
            ft.addAttrType(at3);
            em.persist(at3);
            em.getTransaction().commit();
        }
    }

    private void validateGoogle(EntityManager em) {
        String ftName = "Google";
        List result = em.createQuery("from FeatureTypeDbEntity where name=:ft").setParameter("ft", ftName).getResultList();
        FeatureTypeDbEntity ft = null;
        if (result == null || result.isEmpty()) {
            em.getTransaction().begin();
            ft = new FeatureTypeDbEntity();
            ft.setName(ftName);
            ft.setLabel("_" + ftName);
            ft.setClassName("Kloudgis." + ftName);
            ft.setHasGeometry(Boolean.FALSE);
            ft.setIdAttr("id");
            ft.setSearchable(Boolean.TRUE);
            ft.setSelectionable(Boolean.FALSE);
            em.persist(ft);
            em.getTransaction().commit();
        }
    }

    private void validateSelectionPriority(EntityManager em, AttrTypeDbEntity at, String ftName) {
        if (ftName.equals("Limit")) {
            //limit selection priority
            PriorityDbEntity p1 = new PriorityDbEntity();
            p1.setPriority(10);
            p1.setValue("ville");
            p1.setAttribute(at);
            em.persist(p1);
            PriorityDbEntity p2 = new PriorityDbEntity();
            p2.setPriority(20);
            p2.setValue("mrc");
            p2.setAttribute(at);
            em.persist(p2);
            PriorityDbEntity p3 = new PriorityDbEntity();
            p3.setPriority(30);
            p3.setValue("cir_fonc");
            p3.setAttribute(at);
            em.persist(p3);
        }
    }

    public void contextInitialized(ServletContextEvent sce) {
        PersistenceManager.getInstance().addPersistenceUnitListener(this);
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                //force create unit to speed up client first request
                timer.cancel();
                timer = null;
                System.out.println("*** Force PU creation");
                PersistenceManager.getInstance().getEntityManager(PersistenceManager.ADMIN_PU);
                PersistenceManager.getInstance().getEntityManager(PersistenceManager.DATA_PU);
            }
        }, 1000);

    }

    public void contextDestroyed(ServletContextEvent sce) {
        PersistenceManager.getInstance().removePersistenceUnitListener(this);
    }
}
