package com.mukadobo.propertyobject

import org.junit.Ignore;
import org.junit.Test

import static org.junit.Assert.*;

class PropertyObjectOLDTest
{
    static class Person extends PropertyObjectOLD.Base
    {
        Person(Map<String,Object> jsonDom)
        {
            super(jsonDom)
        }

        static {
            PropertyScheme.register (Person.class.getCanonicalName(),
            [
                name : status(),
                addr : status(),
            ])
        }
    }

    @Ignore
    @Test(expected = IllegalArgumentException.class)
    void construct_FromJson_Empty_Exception() {
        Map<String, Object> jsonDom = new LinkedHashMap<>()

        PropertyObjectOLD pobj = new Person(jsonDom)

        assertNotNull("pobj", pobj)
    }
}