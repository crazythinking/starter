package net.engining.datasource.autoconfigure.autotest.jpa.support;

import com.google.common.collect.Lists;
import net.engining.datasource.autoconfigure.autotest.support.LogRepositoriesService;
import net.engining.datasource.autoconfigure.autotest.support.OperAdtLogProjection;
import net.engining.gm.aop.SpecifiedDataSource;
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

    @SpecifiedDataSource(value = "one")
    @Transactional(rollbackFor = Exception.class)
    public Long dsTest(){
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

    @SpecifiedDataSource(value = "one")
    public OperAdtLogProjection fetchByLogin(String login){
        List<OperAdtLogProjection> operAdtLogDtos = operAdtLogJpaRepository.findByLoginId(
                login,
                OperAdtLogProjection.class
        );
        return operAdtLogDtos.iterator().next();
    }


}
