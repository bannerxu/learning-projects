package io.github.mockito.bean;


import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PersonTest {

    @Test
    public void verifyTest() {
        final Person mockPerson = mock(Person.class);

        mockPerson.setId(1);
        mockPerson.setName("TestOps");

        verify(mockPerson).setId(1);
        verify(mockPerson).setName("TestOps");
    }

}