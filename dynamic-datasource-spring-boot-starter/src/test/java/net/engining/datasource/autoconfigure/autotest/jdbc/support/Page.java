package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import java.util.List;

/**
 * SQL 查询分页信息
 *
 * @param <R>
 */
public class Page<R>{

    /**
     * 页码，从1开始
     */
    private int pageNo = 1;
    /**
     * 页面大小
     */
    private int pageSize = 10;
    /**
     * 总数
     */
    private long total;
    /**
     * 结果
     */
    private List<R> rows;

    public Page() {
        super();
    }

    public Page(int pageNo, int pageSize) {
//        super(0);
        setPageSize(pageSize);
        setPageNo(pageNo);
    }

    public int getPageNo() {
        return pageNo;
    }

    public Page<R> setPageNo(int pageNo) {
        //分页合理化，针对不合理的页码自动处理
        this.pageNo = pageNo <= 0 ? 1 : pageNo;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Page<R> setPageSize(int pageSize) {
        this.pageSize = (pageSize == Integer.MAX_VALUE || pageSize < 0) ? 10 : pageSize;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<R> getRows() {
        return rows;
    }

    public void setRows(List<R> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageVO{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", rows=" + rows +
                '}';
    }
}
