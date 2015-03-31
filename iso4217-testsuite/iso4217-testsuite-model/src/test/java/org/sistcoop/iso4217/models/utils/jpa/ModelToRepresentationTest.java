package org.sistcoop.iso4217.models.utils.jpa;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sistcoop.iso4217.models.CountryCodeModel;
import org.sistcoop.iso4217.models.CountryCodeProvider;
import org.sistcoop.iso4217.models.jpa.CountryCodeAdapter;
import org.sistcoop.iso4217.models.jpa.JpaCountryCodeProvider;
import org.sistcoop.iso4217.models.jpa.entities.CurrencyEntity;
import org.sistcoop.iso4217.models.utils.ModelToRepresentation;
import org.sistcoop.iso4217.provider.Provider;
import org.sistcoop.iso4217.representations.idm.CountryCodeRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
@UsingDataSet("empty.xml")
public class ModelToRepresentationTest {

	Logger log = LoggerFactory.getLogger(ModelToRepresentationTest.class);

	@PersistenceContext
	private EntityManager em;
		
	@Inject
	private CountryCodeProvider countryCodeProvider;
	
	@Deployment
	public static WebArchive createDeployment() {
		File[] dependencies = Maven.resolver()
				.resolve("org.slf4j:slf4j-simple:1.7.10")
				.withoutTransitivity().asFile();

		WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
				/**Model to representation**/
				.addClass(ModelToRepresentation.class)
				.addClass(CountryCodeRepresentation.class)
				
				/**persona-model-api**/
				.addClass(Provider.class)										
				.addClass(CountryCodeProvider.class)				
				
				.addPackage(CountryCodeModel.class.getPackage())				
												
				/**persona-model-jpa**/				
				.addClass(JpaCountryCodeProvider.class)
				.addClass(CountryCodeAdapter.class)																						
				
				.addPackage(CurrencyEntity.class.getPackage())
				
				.addAsResource("META-INF/jpaTest-persistence.xml", "META-INF/persistence.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsWebInfResource("jpaTest-ds.xml");

		war.addAsLibraries(dependencies);

		return war;
	}				
	
	@Test
	public void commit() {
		CountryCodeModel model1 = countryCodeProvider.addCountryCode("PE", "PER", "051", true, true, "Peru", "PERU", "Republic of Peru");				
		
		CountryCodeRepresentation countryCodeRepresentation = ModelToRepresentation.toRepresentation(model1);
				
		assertThat(countryCodeRepresentation, is(notNullValue()));
		assertThat(countryCodeRepresentation.getAlpha2Code(), is(notNullValue()));
		assertThat(countryCodeRepresentation.getAlpha3Code(), is(notNullValue()));
		assertThat(countryCodeRepresentation.getNumericCode(), is(notNullValue()));
		assertThat(countryCodeRepresentation.getIndependent(), is(notNullValue()));
		assertThat(countryCodeRepresentation.getStatus(), is(notNullValue()));
		assertThat(countryCodeRepresentation.getShortNameEn(), is(notNullValue()));
		assertThat(countryCodeRepresentation.getShortNameUppercaseEn(), is(notNullValue()));
		assertThat(countryCodeRepresentation.getFullNameEn(), is(notNullValue()));
		
	}	
	
}
