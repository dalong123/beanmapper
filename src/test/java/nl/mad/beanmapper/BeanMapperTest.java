package nl.mad.beanmapper;

import nl.mad.beanmapper.testmodel.defaults.SourceWithDefaults;
import nl.mad.beanmapper.testmodel.defaults.TargetWithDefaults;
import nl.mad.beanmapper.testmodel.encapsulate.*;
import nl.mad.beanmapper.testmodel.encapsulate.sourceAnnotated.Car;
import nl.mad.beanmapper.testmodel.encapsulate.sourceAnnotated.CarDriver;
import nl.mad.beanmapper.testmodel.encapsulate.sourceAnnotated.Driver;
import nl.mad.beanmapper.testmodel.ignore.IgnoreSource;
import nl.mad.beanmapper.testmodel.ignore.IgnoreTarget;
import nl.mad.beanmapper.testmodel.initiallyunmatchedsource.SourceWithUnmatchedField;
import nl.mad.beanmapper.testmodel.initiallyunmatchedsource.TargetWithoutUnmatchedField;
import nl.mad.beanmapper.testmodel.multipleunwrap.AllTogether;
import nl.mad.beanmapper.testmodel.multipleunwrap.LayerA;
import nl.mad.beanmapper.testmodel.nestedclasses.Layer1;
import nl.mad.beanmapper.testmodel.nestedclasses.Layer1Result;
import nl.mad.beanmapper.testmodel.othername.SourceWithOtherName;
import nl.mad.beanmapper.testmodel.othername.TargetWithOtherName;
import nl.mad.beanmapper.testmodel.parentClass.Source;
import nl.mad.beanmapper.testmodel.parentClass.Target;
import nl.mad.beanmapper.testmodel.person.Person;
import nl.mad.beanmapper.testmodel.person.PersonForm;
import nl.mad.beanmapper.testmodel.person.PersonView;
import nl.mad.beanmapper.testmodel.tostring.SourceWithNonString;
import nl.mad.beanmapper.testmodel.tostring.TargetWithString;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BeanMapperTest {

    private BeanMapper beanMapper;
    
    @Before
    public void prepareBeanMapper() {
        beanMapper = new BeanMapper();
        beanMapper.addPackagePrefix(BeanMapper.class);
    }
    
    @Test
    public void copyToNewTargetInstance() throws Exception {
        Person person = createPerson();
        PersonView personView = beanMapper.map(person, PersonView.class);
        assertEquals("Henk", personView.name);
        assertEquals("Zoetermeer", personView.place);
    }

    @Test
    public void copyToExistingTargetInstance() throws Exception {
        Person person = createPerson();
        PersonForm form = createPersonForm();
        person = beanMapper.map(form, person);
        assertEquals(1984L, (long) person.getId());
        assertEquals("Truus", person.getName());
        assertEquals("XHT-8311-t33l-llac", person.getBankAccount());
        assertEquals("Den Haag", person.getPlace());
    }

    @Test
    public void beanIgnore() throws Exception {
        IgnoreSource ignoreSource = new IgnoreSource();
        ignoreSource.setBothIgnore("bothIgnore");
        ignoreSource.setSourceIgnore("sourceIgnore");
        ignoreSource.setTargetIgnore("targetIgnore");
        ignoreSource.setNoIgnore("noIgnore");

        IgnoreTarget ignoreTarget = beanMapper.map(ignoreSource, IgnoreTarget.class);
        assertNull("bothIgnore -> target should be empty", ignoreTarget.getBothIgnore());
        assertNull("sourceIgnore -> target should be empty", ignoreTarget.getSourceIgnore());
        assertNull("targetIgnore -> target should be empty", ignoreTarget.getTargetIgnore());
        assertEquals("noIgnore", ignoreTarget.getNoIgnore());
    }

    @Test
    public void mappingToOtherNames() throws Exception {
        SourceWithOtherName source = new SourceWithOtherName();
        source.setBothOtherName1("bothOtherName");
        source.setSourceOtherName1("sourceOtherName");
        source.setTargetOtherName("targetOtherName");
        source.setNoOtherName("noOtherName");

        TargetWithOtherName target = beanMapper.map(source, TargetWithOtherName.class);
        assertEquals("bothOtherName", target.getBothOtherName2());
        assertEquals("sourceOtherName", target.getSourceOtherName());
        assertEquals("targetOtherName", target.getTargetOtherName1());
        assertEquals("noOtherName", target.getNoOtherName());
    }

    @Test
    public void nonStringToString() throws Exception {
        SourceWithNonString obj = new SourceWithNonString();
        obj.setDate(LocalDate.of(2015, 4, 1));
        TargetWithString view = beanMapper.map(obj, TargetWithString.class);
        assertEquals("2015-04-01", view.getDate());
    }

    @Test
    public void beanDefault() throws Exception {
        SourceWithDefaults source = new SourceWithDefaults();
        source.setNoDefault("value1");
        source.setTargetDefaultWithValue("value2");

        TargetWithDefaults target = beanMapper.map(source, TargetWithDefaults.class);
        assertEquals("bothdefault2", target.getBothDefault());
        assertEquals("sourcedefault", target.getSourceDefault());
        assertEquals("targetdefault", target.getTargetDefault());
        assertEquals("value1", target.getNoDefault());
        assertEquals("targetdefaultwithoutmatch", target.getTargetDefaultWithoutMatch());
        assertEquals("value2", target.getTargetDefaultWithValue());
    }

    @Test
    public void testParentClass() throws Exception {
        Source entity = new Source();
        entity.setId(1L);
        entity.setName("abstractName");
        entity.setStreet("street");
        entity.setHouseNumber(42);

        Target target = beanMapper.map(entity, Target.class);
        assertEquals(1, target.getId(), 0);
        assertEquals("abstractName", target.getName());
        assertEquals("street", target.getStreet());
        assertEquals(42, target.getHouseNumber(), 0);
    }

    @Test
    public void testParentClassReversed() throws Exception {
        Target target = new Target();
        target.setId(1L);
        target.setName("abstractName");
        target.setStreet("street");
        target.setHouseNumber(42);

        Source source = beanMapper.map(target, Source.class);
        assertEquals(1, source.getId(), 0);
        assertEquals("abstractName", source.getName());
        assertEquals("street", source.getStreet());
        assertEquals(42, source.getHouseNumber(), 0);
    }

    @Test
    public void EncapsulateManyToMany() throws Exception {
        House house = createHouse();

        ResultManyToMany result = beanMapper.map(house, ResultManyToMany.class);
        assertEquals("housename", result.getName());
        assertEquals("denneweg", result.getAddressOfTheHouse().getStreet());
        assertEquals(1, result.getAddressOfTheHouse().getNumber());
        assertEquals("Nederland", result.getAddressOfTheHouse().getCountry().getCountryName());
    }

    @Test
    public void EncapsulateManyToOne() throws Exception {
        House house = createHouse();

        ResultManyToOne result = beanMapper.map(house, ResultManyToOne.class);
        assertEquals("housename", result.getName());
        assertEquals("denneweg", result.getStreet());
        assertEquals(1, result.getNumber());
        assertEquals("Nederland", result.getCountryName());
    }

    @Test
    public void EncapsulateOneToMany() throws Exception {
        Country country = new Country("Nederland");

        ResultOneToMany result = beanMapper.map(country, ResultOneToMany.class);
        assertEquals("Nederland", result.getResultCountry().getCountryName());
    }

    @Test
    public void sourceAnnotated() throws Exception {
        // One to Many & Many to One
        Driver driver = new Driver("driverName");
        Car car = new Car("Opel", 4);
        driver.setCar(car);
        driver.setMonteurName("monteur");

        CarDriver target = beanMapper.map(driver, CarDriver.class);
        assertEquals("driverName", target.getName());
        assertEquals("Opel", target.getBrand());
        assertEquals(4, target.getWheels());
        assertEquals("monteur", target.getMonteur().getName());
    }

    @Test
    public void initiallyUnmatchedSourceMustBeUsed() throws Exception {
        SourceWithUnmatchedField swuf = new SourceWithUnmatchedField();
        swuf.setName("Henk");
        swuf.setCountry("NL");
        TargetWithoutUnmatchedField twuf = beanMapper.map(swuf, new TargetWithoutUnmatchedField());
        assertEquals("Henk", twuf.getName());
        assertEquals("NL", twuf.getNation());
    }

    @Test
    public void nestedClasses() throws Exception {
        Layer1 layer1 = Layer1.createNestedClassObject();
        Layer1Result result = beanMapper.map(layer1, Layer1Result.class);
        assertEquals("layer1", result.getName1());
        assertEquals("layer2", result.getLayer2().getName2());
        assertEquals("name3", result.getLayer2().getLayer3().getName3());
    }

    @Test
    public void multipleUnwrap() throws Exception {
        LayerA source = LayerA.create();
        AllTogether target = beanMapper.map(source, AllTogether.class);
        assertEquals("name1", target.getName1());
        assertEquals("name2", target.getName2());
        assertEquals("name3", target.getName3());
    }

    public Person createPerson() {
        Person person = new Person();
        person.setId(1984L);
        person.setName("Henk");
        person.setPlace("Zoetermeer");
        person.setBankAccount("THX-1138-l33t-call");
        return person;
    }

    public PersonForm createPersonForm() {
        PersonForm person = new PersonForm();
        person.setName("Truus");
        person.setPlace("Den Haag");
        person.setBankAccount("XHT-8311-t33l-llac");
        person.setUnidentifiableFluff("0xCAFEBABE");
        return person;
    }

    public House createHouse() {
        House house = new House();
        house.setName("housename");
        Address address = new Address();
        address.setNumber(1);
        address.setStreet("denneweg");
        address.setCountry(new Country("Nederland"));
        house.setAddress(address);
        return house;
    }

}