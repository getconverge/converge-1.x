/*
 *  Copyright (C) 2010 - 2011 Interactive Media Management
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge;

import dk.i2m.converge.ejb.services.ConfigurationServiceLocal;
import java.io.OutputStream;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.junit.Before;

/**
 * Abstract test case for unit testing EJB3s using the OpenEJB embedded
 * container.
 *
 * @see <a href="http://openejb.apache.org/examples.html">OpenEJB Examples Index</a>
 * @author Allan Lykke Christensen
 */
public abstract class EjbTestCase {

    /** JDBC driver used for test database. */
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    /** URL for connecting to the test database. */
    public static final String URL = "jdbc:derby:target/converge-db;create=true;user=me;password=mine";

    public static final String DB_UID = "me";

    public static final String DB_PWD = "mine";

    /** {@link OutputStream} that discards all output. */
    public static final OutputStream DEV_NULL = new OutputStream() {

        public void write(int b) {
        }
    };

    private InitialContext initialContext;

    /**
     * Sets up the {@link InitialContext} for the embedded application server
     * and initialises the database with the dataset returned by
     * {@link #getDataSet()}.
     *
     * @throws Exception
     *          If the {@link InitialContext} or dataset could not be set-up
     */
    //@Before
    public void setUp() throws Exception {
        // Avoid creating the Derby log file
        System.setProperty("derby.stream.error.field", getClass().getName() + ".DEV_NULL");

        // Initialise EJB container
        Properties p = new Properties();
        p.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");

        p.put("MyJmsResourceAdapter", "new://Resource?type=ActiveMQResourceAdapter");
        p.put("MyJmsResourceAdapter.BrokerXmlConfig", "");
        p.put("jdbc/converge", "new://Resource?type=DataSource");
        p.put("jdbc/converge.JdbcDriver", DRIVER);
        p.put("jdbc/converge.JdbcUrl", URL);
        p.put("jdbc/converge.username", DB_UID);
        p.put("jdbc/converge.password", DB_PWD);
        p.put("jms/connectionFactory", "new://Resource?type=javax.jms.ConnectionFactory");
        p.put("jms/connectionFactory.ResourceAdapter", "MyJmsResourceAdapter");
        p.put("jms/editionServiceQueue", "new://Resource?type=javax.jms.Queue");
        p.put("jms/outletServiceQueue", "new://Resource?type=javax.jms.Queue");
        p.put("jms/newswireServiceQueue", "new://Resource?type=javax.jms.Queue");
        p.put("jms/catalogueHookQueue", "new://Resource?type=javax.jms.Queue");
        p.put("converge-ejbPU.eclipselink.target-server", "org.apache.openejb.eclipselink.JTATransactionController");
        p.put("converge-ejbPU.eclipselink.ddl-generation", "drop-and-create-tables");

        initialContext = new InitialContext(p);

        initDatabase();
    }

    private void initDatabase() throws Exception {
        // Query the entity manager to force database creation
        ConfigurationServiceLocal cfgService = (ConfigurationServiceLocal) getInitialContext().lookup("ConfigurationServiceBeanLocal");
        System.out.println("Testing version " + cfgService.getVersion());
    }

    /**
     * Gets the {@link InitialContext} of the embedded application server.
     *
     * @return {@link InitialContext} of the embedded application server
     */
    public InitialContext getInitialContext() {
        return initialContext;
    }
}
