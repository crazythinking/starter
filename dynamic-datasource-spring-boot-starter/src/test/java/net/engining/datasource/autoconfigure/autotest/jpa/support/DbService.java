package net.engining.datasource.autoconfigure.autotest.jpa.support;

import com.google.common.collect.Lists;
import com.querydsl.jpa.impl.JPAQueryFactory;
import net.engining.gm.aop.SpecifiedDataSource;
import net.engining.gm.aop.SwitchOrg4HibernateFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @Autowired
    OperAdtLogJpaRepository operAdtLogJpaRepository;

    private PgIdTestEnt1 doFetch(Long snowFlakeId){
        QPgIdTestEnt1 qPgIdTestEnt1 = QPgIdTestEnt1.pgIdTest;
        PgIdTestEnt1 pgIdTestEnt1 = new JPAQueryFactory(em)
                .selectFrom(qPgIdTestEnt1)
                .where(qPgIdTestEnt1.snowFlakeId.eq(snowFlakeId))
                .fetchOne();

        return pgIdTestEnt1;
    }

    @Transactional(readOnly = true)
    public PgIdTestEnt1 fetch(Long snowFlakeId){
        return doFetch(snowFlakeId);
    }

    @Transactional(readOnly = true)
    public PgIdTestEnt1 fetch4Org(Long snowFlakeId){
        //确保 org filter 的操作被包在 Transaction 内
        return doFetch4Org(snowFlakeId);
    }

    @SwitchOrg4HibernateFilter
    public PgIdTestEnt1 doFetch4Org(Long snowFlakeId){
        //OrganizationContextHolder.enableOrgFilter(em);
        PgIdTestEnt1 pgIdTestEnt1 = doFetch(snowFlakeId);
        //OrganizationContextHolder.disableOrgFilter(em);
        return pgIdTestEnt1;
    }

    //注意使用切换数据库AOP时，应该确保其在开启事务外，因为切换数据库之后，就是一个新的事务了
    @SpecifiedDataSource(value = "one")
    public Long dbTest() {
        return doDsTest();
    }

    @Transactional(rollbackFor = Exception.class)
    private Long doDsTest(){
        PgIdTestEnt1 pgIdTestEnt1 = new PgIdTestEnt1();
        pgIdTestEnt1.setBatchNumber("aa1111111");
        em.persist(pgIdTestEnt1);
        log.debug("dsTest");
        return pgIdTestEnt1.getSnowFlakeId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long dsTest4defaultDataSource(){
        PgIdTestEnt1 pgIdTestEnt1 = new PgIdTestEnt1();
        pgIdTestEnt1.setBatchNumber("aa1111111");
        em.persist(pgIdTestEnt1);
        log.debug("dsTest");
        return pgIdTestEnt1.getSnowFlakeId();
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
