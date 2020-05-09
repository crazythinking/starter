package net.engining.datasource.autoconfigure.autotest.cases;

import net.engining.datasource.autoconfigure.aop.SpecifiedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Eric Lu
 * @create 2019-11-05 18:56
 **/
@Service
public class DbService {
    /** logger */
    private static final Logger log = LoggerFactory.getLogger(DbService.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional(rollbackFor = Exception.class)
    public Long dsTest(){
        PgIdTestEnt pgIdTestEnt = new PgIdTestEnt();
        pgIdTestEnt.setBatchNumber("aa1111111");
        em.persist(pgIdTestEnt);
        log.debug("dsTest");
        return pgIdTestEnt.getSnowFlakeId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long dsTest4defautDataSource(){
        PgIdTestEnt pgIdTestEnt = new PgIdTestEnt();
        pgIdTestEnt.setBatchNumber("aa1111111");
        em.persist(pgIdTestEnt);
        log.debug("dsTest");
        return pgIdTestEnt.getSnowFlakeId();
    }

}
