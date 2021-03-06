package org.sistcoop.iso4217.admin.client.resource;

import java.io.File;

import org.sistcoop.iso4217.admin.client.Config;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.runner.RunWith;
import org.sistcoop.iso4217.JaxRsActivator;
import org.sistcoop.iso4217.models.CurrencyModel;
import org.sistcoop.iso4217.models.jpa.JpaCurrencyProvider;
import org.sistcoop.iso4217.models.jpa.entities.CurrencyEntity;
import org.sistcoop.iso4217.models.jpa.search.filters.JpaCurrencyFilterProvider;
import org.sistcoop.iso4217.models.search.SearchCriteriaFilterModel;
import org.sistcoop.iso4217.models.search.filters.CurrencyFilterProvider;
import org.sistcoop.iso4217.models.utils.ModelToRepresentation;
import org.sistcoop.iso4217.provider.Provider;
import org.sistcoop.iso4217.representations.idm.CurrencyRepresentation;
import org.sistcoop.iso4217.representations.idm.search.SearchResultsRepresentation;
import org.sistcoop.iso4217.services.messages.Messages;
import org.sistcoop.iso4217.services.resources.admin.CurrenciesResourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
@UsingDataSet("empty.xml")
public abstract class AbstractTest {

    protected Logger log = LoggerFactory.getLogger(AbstractTest.class);

    @Deployment
    public static WebArchive createDeployment() {
        File[] logger = Maven.resolver().resolve("org.slf4j:slf4j-simple:1.7.10").withoutTransitivity()
                .asFile();

        File[] restAssured = Maven.resolver().resolve("com.jayway.restassured:rest-assured:2.4.1")
                .withTransitivity().asFile();

        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "test.war")

                /** model-api **/
                .addPackage(Provider.class.getPackage())
                .addPackage(CurrencyModel.class.getPackage())

                .addPackage(ModelToRepresentation.class.getPackage())

                .addPackage(SearchCriteriaFilterModel.class.getPackage())
                .addPackage(CurrencyFilterProvider.class.getPackage())

                /** model-jpa **/
                .addPackage(JpaCurrencyProvider.class.getPackage())
                .addPackage(JpaCurrencyFilterProvider.class.getPackage())
                .addPackage(CurrencyEntity.class.getPackage())

                /** client */
                .addPackage(Config.class.getPackage())
                .addPackage(CurrenciesResource.class.getPackage())

                /** services */
                .addPackage(Messages.class.getPackage())
                .addPackage(CurrenciesResourceImpl.class.getPackage())

                /** core */
                .addPackage(CurrencyRepresentation.class.getPackage())
                .addPackage(SearchResultsRepresentation.class.getPackage())

                /** core jaxrs */
                .addPackage(JaxRsActivator.class.getPackage())

                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource("META-INF/test-jboss-deployment-structure.xml",
                        "jboss-deployment-structure.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "web.xml").addAsWebInfResource("test-ds.xml");

        war.addAsLibraries(logger);
        war.addAsLibraries(restAssured);

        return war;
    }
}
