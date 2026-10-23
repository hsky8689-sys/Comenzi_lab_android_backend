package facultate.android_backend.Repository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateUtils {
    private static SessionFactory sessionFactory;
    public HibernateUtils(SessionFactory factory){
        sessionFactory = factory;
    }
    public static Session getSession(){
        return sessionFactory.getCurrentSession();
    }
}
