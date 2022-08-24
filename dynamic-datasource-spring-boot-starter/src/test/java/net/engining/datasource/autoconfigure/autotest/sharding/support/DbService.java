package net.engining.datasource.autoconfigure.autotest.sharding.support;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import com.querydsl.jpa.impl.JPAQueryFactory;
import net.engining.datasource.autoconfigure.aop.SwitchOrg4HibernateFilter;
import net.engining.datasource.autoconfigure.autotest.jpa.support.OperAdtLogJpaRepository;
import net.engining.datasource.autoconfigure.autotest.jpa.support.PgIdSdTestEnt;
import net.engining.datasource.autoconfigure.autotest.jpa.support.PgIdTestEnt1;
import net.engining.datasource.autoconfigure.autotest.jpa.support.QPgIdTestEnt1;
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

    @Transactional(rollbackFor = Exception.class)
    public List<Long> dsTest4sharding(){
        List<Integer> userIds = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            userIds.add(RandomUtil.randomInt(100, 110));
        }

        List<Long> ids = Lists.newArrayList();
        TOrder tOrder = new TOrder();
        tOrder.setUserId(userIds.get(0));
        tOrder.setStatus("status");
        em.persist(tOrder);
        ids.add(tOrder.getOrderId());
        TOrderItem tOrderItem = new TOrderItem();
        tOrderItem.setOrderId(tOrder.getOrderId());
        tOrderItem.setUserId(userIds.get(0));
        tOrderItem.setStatus("status");
        em.persist(tOrderItem);

        tOrder = new TOrder();
        tOrder.setUserId(userIds.get(1));
        tOrder.setStatus("status");
        em.persist(tOrder);
        ids.add(tOrder.getOrderId());
        tOrderItem = new TOrderItem();
        tOrderItem.setOrderId(tOrder.getOrderId());
        tOrderItem.setUserId(userIds.get(1));
        tOrderItem.setStatus("status");
        em.persist(tOrderItem);

        tOrder = new TOrder();
        tOrder.setUserId(userIds.get(2));
        tOrder.setStatus("status");
        em.persist(tOrder);
        ids.add(tOrder.getOrderId());
        tOrderItem = new TOrderItem();
        tOrderItem.setOrderId(tOrder.getOrderId());
        tOrderItem.setUserId(userIds.get(2));
        tOrderItem.setStatus("status");
        em.persist(tOrderItem);

        return ids;
    }

    @Transactional(readOnly = true)
    public List<TOrderItem> doFetch(Long orderId){
        QTOrderItem qtOrderItem = QTOrderItem.tOrderItem;
        List<TOrderItem> tOrderItem = new JPAQueryFactory(em)
                .selectFrom(qtOrderItem)
                .where(qtOrderItem.orderId.eq(orderId))
                .fetch();

        return tOrderItem;
    }


}
