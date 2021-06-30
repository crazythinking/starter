package net.engining.datasource.autoconfigure.autotest.jpa.support;

import com.google.common.collect.Lists;
import com.querydsl.jpa.impl.JPAQueryFactory;
import net.engining.gm.aop.SpecifiedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

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

    @PersistenceUnit
    private EntityManagerFactory emf;

    @SpecifiedDataSource(value = "one")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Long dsTest(){
        PgIdTestEnt1 pgIdTestEnt1 = new PgIdTestEnt1();
        pgIdTestEnt1.setBatchNumber("aa1111111");
        em.persist(pgIdTestEnt1);
        log.debug("datasource one : dsTest");
        return pgIdTestEnt1.getSnowFlakeId();
    }

    @SpecifiedDataSource(value = "one")
    public Long dsTest2(){
        PgIdTestEnt1 pgIdTestEnt1 = new PgIdTestEnt1();
        pgIdTestEnt1.setBatchNumber("aa1111111");
        em.persist(pgIdTestEnt1);
        log.debug("datasource one : dsTest");
        return pgIdTestEnt1.getSnowFlakeId();
    }

    @SpecifiedDataSource(value = "one")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    //@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Long dsTestQuery(){
        QPgIdTestEnt1 qPgIdTestEnt1 = QPgIdTestEnt1.pgIdTest;
        List<PgIdTestEnt1> rs = new JPAQueryFactory(em).select(qPgIdTestEnt1).from(qPgIdTestEnt1).fetch();
        log.debug("datasource one : dsTestQuery");
        return rs.get(0).getSnowFlakeId();
    }

    @SpecifiedDataSource(value = "one")
    public Long dsTestQuery2(){
        QPgIdTestEnt1 qPgIdTestEnt1 = QPgIdTestEnt1.pgIdTest;
        List<PgIdTestEnt1> rs = new JPAQueryFactory(em).select(qPgIdTestEnt1).from(qPgIdTestEnt1).fetch();
        log.debug("datasource one : dsTestQuery");
        return rs.get(0).getSnowFlakeId();
    }

    public List<PgIdTestEnt1> dsTestQuery3(){
        QPgIdTestEnt1 qPgIdTestEnt1 = QPgIdTestEnt1.pgIdTest;
        List<PgIdTestEnt1> rs = new JPAQueryFactory(em).select(qPgIdTestEnt1).from(qPgIdTestEnt1).fetch();
        log.debug("datasource one : dsTestQuery");
        return rs;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long dsTest4defautDataSource(){
        PgIdTestEnt1 pgIdTestEnt1 = new PgIdTestEnt1();
        pgIdTestEnt1.setBatchNumber("aa1111111");
        em.persist(pgIdTestEnt1);
        log.debug("datasource default : dsTest4defautDataSource");
        return pgIdTestEnt1.getSnowFlakeId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void merge(){
        log.debug("datasource default : merge");
        this.dsTest4defautDataSource();
        this.dsTest2();
    }

    @Transactional(rollbackFor = Exception.class)
    public void merge2(){
        log.debug("datasource default : merge2");
        this.dsTest4defautDataSource();
        this.dsTestQuery();
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> dsTest4sharding(){
        List<Long> ids = Lists.newArrayList();
        PgIdSdTestEnt pgIdTestEnt = new PgIdSdTestEnt();
        pgIdTestEnt.setBatchNumber("aa1111111");
        em.persist(pgIdTestEnt);
        ids.add(pgIdTestEnt.getSnowFlakeId());

        pgIdTestEnt = new PgIdSdTestEnt();
        pgIdTestEnt.setBatchNumber("aa1111111");
        em.persist(pgIdTestEnt);
        ids.add(pgIdTestEnt.getSnowFlakeId());

        pgIdTestEnt = new PgIdSdTestEnt();
        pgIdTestEnt.setBatchNumber("aa1111111");
        em.persist(pgIdTestEnt);
        ids.add(pgIdTestEnt.getSnowFlakeId());

        return ids;
    }


}
