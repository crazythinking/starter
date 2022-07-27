package net.engining.spl.autoconfigure.autotest.cases;


import cn.hutool.core.lang.Console;
import net.engining.spl.autoconfigure.autotest.support.AbstractTestCaseTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eric Lu
 **/
public class SimpleTest extends AbstractTestCaseTemplate {

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {

    }

    @Override
    public void testProcess() throws ClassNotFoundException, SQLException {
        Connection con = null;
        PreparedStatement st;
        ResultSet rs;
        String jsonResult = null;
        Class.forName("com.esproc.jdbc.InternalDriver");
        StringBuilder url = new StringBuilder("jdbc:esproc:local://");
        url.append("?");
        url.append("config=D:\\idea-workspace\\power-gears\\starter\\spl-spring-boot-starter\\spl\\raqsoftConfig.xml");
        url.append("&");
        url.append("debugmode=true");
        con = DriverManager.getConnection(url.toString());

        //System.out.println("code:"+"110330");
        st = con.prepareCall("call stock()");
        //st.setObject(1, "110330");
        st.execute();
        rs = st.getResultSet();
        while (rs.next()) {
            jsonResult = rs.getObject(1).toString();
            Console.log(jsonResult);
        }

        st = con.prepareCall("call stock_aapl()");
        st.execute();
        rs = st.getResultSet();
        while (rs.next()) {
            jsonResult = rs.getObject(1).toString();
            Console.log(jsonResult);
        }

        if (con != null) {
            con.close();
        }

    }

    @Override
    public void end() {

    }

}
