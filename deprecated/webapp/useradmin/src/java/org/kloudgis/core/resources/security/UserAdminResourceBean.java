package org.kloudgis.core.resources.security;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.core.persistence.PersistenceManager;
import org.kloudgis.core.persistence.security.UserDbEntity;
import org.kloudgis.core.persistence.security.UserRoleDbEntity;
import org.kloudgis.core.pojo.security.User;

/**
 *
 * @author jeanfelixg
 */
@Path("/admin/users")
public class UserAdminResourceBean {

    /*
     * GET AN IMAGE
    @GET
    @Produces("image/jpg")
    public Response imageDownload() {
    try {
    InputStream stream = new FileInputStream(new File("/Users/jeanfelixg/Pictures/t1.jpg"));
    return Response.ok().entity(stream).build();
    } catch (IOException ex) {
    Logger.getLogger(UserAdminResourceBean.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
    }*/
    @GET
    @Produces({"application/json"})
    public List<User> getUsers(@QueryParam("filter") String filter) throws WebApplicationException {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        Criteria cr = em.getSession().createCriteria(UserDbEntity.class);
        cr.addOrder(Order.asc("fullName"));
        if (filter != null && filter.length() > 0) {
            LogicalExpression orLike = Restrictions.or(Restrictions.like("fullName", "%" + filter + "%"), Restrictions.like("email", "%" + filter + "%"));
            cr.add(orLike);
        }
        List<UserDbEntity> lstDb = cr.list();
        List<User> lstU = new ArrayList();
        for (UserDbEntity uDb : lstDb) {
            lstU.add(uDb.toPojo(em));
        }
        em.close();
        return lstU;
    }

    /*
    @GET
    @Produces({"application/json"})
    public QueryResult getUsers(
    @DefaultValue("0") @QueryParam("start") Integer start,
    @DefaultValue("-1") @QueryParam("length") Integer length,
    @QueryParam("filter") String filter) {
    HibernateEntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
    Criteria cr = em.getSession().createCriteria(UserDbEntity.class).setFirstResult(start);
    if (length >= 0) {
    cr.setMaxResults(length);
    }
    cr.addOrder(Order.asc("fullName"));
    if(filter != null && filter.length() > 0){
    cr.add(Restrictions.like("fullName", "%" + filter + "%"));
    }
    List<UserDbEntity> lstDb = cr.list();
    List<User> lstU = new ArrayList();
    for(UserDbEntity uDb : lstDb){
    lstU.add(uDb.toPojo());
    }
    Long count = new Integer(lstDb.size()).longValue();
    if (start.intValue() > 0 || length.intValue() != -1) {
    Query qCount = em.createQuery(
    "SELECT COUNT(e) FROM UserDbEntity e");
    count = (Long) qCount.getSingleResult();
    }
    QueryResult qResult = new QueryResult(lstU, count);
    em.close();
    return qResult;
    }
     */
    @POST
    @Produces({"application/json"})
    public User addUser(User user, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        boolean bNil = user.email == null || user.email.isEmpty();
        if (bNil || testUnique(user, em)) {
            em.getTransaction().begin();
            UserDbEntity uDb = user.toDbEntity(em);
            em.persist(uDb);
            if (!bNil) {
                UserRoleDbEntity role = new UserRoleDbEntity();
                role.setRoleName("role_user");
                uDb.addRole(role);
                em.persist(role);
            }
            em.getTransaction().commit();
            user.guid = uDb.getId();
            em.close();
        }
        return user;
    }

    @PUT
    @Path("{userId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public User updateUser(User user, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        UserDbEntity uDb = em.find(UserDbEntity.class, user.guid);
        if (uDb != null) {
            if (testUnique(user, em)) {
                em.getTransaction().begin();
                if (user.email != null && (uDb.getEmail() == null || !user.email.equals(uDb.getEmail()))) {
                    List<UserRoleDbEntity> roles = uDb.getRoles(em);
                    for (UserRoleDbEntity r : roles) {
                        r.setEmail(user.email);
                    }
                }
                uDb.updateFrom(user, em);
                em.getTransaction().commit();
            }
        }else{
            throw new EntityNotFoundException("User with id=" + user.guid + " is not found.");
        }
        User pojo = uDb.toPojo(em);
        em.close();
        return pojo;
    }

    @Path("{userId}")
    @GET
    @Produces({"application/json"})
    public User getUser(@PathParam("userId") Long uId, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        UserDbEntity uDb = em.find(UserDbEntity.class, uId);
        if (uDb != null) {
            User user = uDb.toPojo(em);
            em.close();
            return user;
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + uId);
    }

    @Path("{userId}")
    @DELETE
    @Produces({"application/json"})
    public Response deleteUser(@PathParam("userId") Long uId, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerAdmin();
        UserDbEntity uDb = em.find(UserDbEntity.class, uId);
        if (uDb != null) {
            em.getTransaction().begin();
            for (UserRoleDbEntity role : uDb.getRoles(em)) {
                em.remove(role);
            }
            em.remove(uDb);
            em.getTransaction().commit();
            em.close();
            return Response.ok().build();
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + uId);
    }

    private Boolean testUnique(User user, EntityManager em) {
        String uName = user.email;
        Long id;
        if (user.guid == null) {
            id = -1L;
        } else {
            id = user.guid;
        }
        Query query = em.createQuery("SELECT u from UserDbEntity u where u.id != :uid AND u.email = :m");
        query = query.setParameter("uid", id).setParameter("m", uName);
        List lstR = query.getResultList();
        return lstR.isEmpty();
    }
}
