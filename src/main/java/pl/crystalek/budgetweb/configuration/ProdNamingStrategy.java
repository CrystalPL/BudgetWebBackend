package pl.crystalek.budgetweb.configuration;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class ProdNamingStrategy extends CamelCaseToUnderscoresNamingStrategy {

    @Override
    public Identifier toPhysicalTableName(final Identifier logicalName, final JdbcEnvironment jdbcEnvironment) {
        return new Identifier("budget_" + logicalName.getText(), logicalName.isQuoted());
    }
}
