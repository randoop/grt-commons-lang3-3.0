/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang3.exception.CloneFailedException;
import org.apache.commons.lang3.mutable.MutableObject;

/**
 * Unit tests {@link org.apache.commons.lang3.ObjectUtils}.
 *
 * @version $Id: ObjectUtilsTest.java 1091448 2011-04-12 15:13:36Z scolebourne $
 */
public class ObjectUtilsTest extends TestCase {
    private static final String FOO = "foo";
    private static final String BAR = "bar";

    public ObjectUtilsTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    public void testConstructor() {
        assertNotNull(new ObjectUtils());
        Constructor<?>[] cons = ObjectUtils.class.getDeclaredConstructors();
        assertEquals(1, cons.length);
        assertEquals(true, Modifier.isPublic(cons[0].getModifiers()));
        assertEquals(true, Modifier.isPublic(ObjectUtils.class.getModifiers()));
        assertEquals(false, Modifier.isFinal(ObjectUtils.class.getModifiers()));
    }

    //-----------------------------------------------------------------------
    public void testIsNull() {
        Object o = FOO;
        Object dflt = BAR;
        assertSame("dflt was not returned when o was null", dflt, ObjectUtils.defaultIfNull(null, dflt));
        assertSame("dflt was returned when o was not null", o, ObjectUtils.defaultIfNull(o, dflt));
    }

    public void testFirstNonNull() {
        assertEquals(null, ObjectUtils.firstNonNull(null, null));
        assertEquals("", ObjectUtils.firstNonNull(null, ""));
        String firstNonNullGenerics = ObjectUtils.firstNonNull(null, null, "123", "456");
        assertEquals("123", firstNonNullGenerics);
        assertEquals("123", ObjectUtils.firstNonNull("123", null, "456", null));
        assertEquals(null, ObjectUtils.firstNonNull());
        assertSame(Boolean.TRUE, ObjectUtils.firstNonNull(Boolean.TRUE));
        assertNull(ObjectUtils.firstNonNull());
        assertNull(ObjectUtils.firstNonNull(null, null));
//        assertSame("123", ObjectUtils.firstNonNull(null, ObjectUtils.NULL, "123", "456"));
//        assertSame("456", ObjectUtils.firstNonNull(ObjectUtils.NULL, "456", "123", null));
//        assertNull(ObjectUtils.firstNonNull(null, null, ObjectUtils.NULL));
        assertNull(ObjectUtils.firstNonNull((Object) null));
        assertNull(ObjectUtils.firstNonNull((Object[]) null));
    }

    //-----------------------------------------------------------------------
    public void testEquals() {
        assertTrue("ObjectUtils.equals(null, null) returned false", ObjectUtils.equals(null, null));
        assertTrue("ObjectUtils.equals(\"foo\", null) returned true", !ObjectUtils.equals(FOO, null));
        assertTrue("ObjectUtils.equals(null, \"bar\") returned true", !ObjectUtils.equals(null, BAR));
        assertTrue("ObjectUtils.equals(\"foo\", \"bar\") returned true", !ObjectUtils.equals(FOO, BAR));
        assertTrue("ObjectUtils.equals(\"foo\", \"foo\") returned false", ObjectUtils.equals(FOO, FOO));
    }

    public void testNotEqual() {
        assertFalse("ObjectUtils.notEqual(null, null) returned false", ObjectUtils.notEqual(null, null));
        assertTrue("ObjectUtils.notEqual(\"foo\", null) returned true", ObjectUtils.notEqual(FOO, null));
        assertTrue("ObjectUtils.notEqual(null, \"bar\") returned true", ObjectUtils.notEqual(null, BAR));
        assertTrue("ObjectUtils.notEqual(\"foo\", \"bar\") returned true", ObjectUtils.notEqual(FOO, BAR));
        assertFalse("ObjectUtils.notEqual(\"foo\", \"foo\") returned false", ObjectUtils.notEqual(FOO, FOO));
    }

    public void testHashCode() {
        assertEquals(0, ObjectUtils.hashCode(null));
        assertEquals("a".hashCode(), ObjectUtils.hashCode("a"));
    }

    public void testHashCodeMulti_multiple_emptyArray() {
        Object[] array = new Object[0];
        assertEquals(1, ObjectUtils.hashCodeMulti(array));
    }

    public void testHashCodeMulti_multiple_nullArray() {
        Object[] array = null;
        assertEquals(1, ObjectUtils.hashCodeMulti(array));
    }

    public void testHashCodeMulti_multiple_likeList() {
        List<Object> list0 = new ArrayList<Object>(Arrays.asList());
        assertEquals(list0.hashCode(), ObjectUtils.hashCodeMulti());
        
        List<Object> list1 = new ArrayList<Object>(Arrays.asList("a"));
        assertEquals(list1.hashCode(), ObjectUtils.hashCodeMulti("a"));
        
        List<Object> list2 = new ArrayList<Object>(Arrays.asList("a", "b"));
        assertEquals(list2.hashCode(), ObjectUtils.hashCodeMulti("a", "b"));
        
        List<Object> list3 = new ArrayList<Object>(Arrays.asList("a", "b", "c"));
        assertEquals(list3.hashCode(), ObjectUtils.hashCodeMulti("a", "b", "c"));
    }

//    /**
//     * Show that java.util.Date and java.sql.Timestamp are apples and oranges.
//     * Prompted by an email discussion. 
//     * 
//     * The behavior is different b/w Sun Java 1.3.1_10 and 1.4.2_03.
//     */
//    public void testDateEqualsJava() {
//        long now = 1076957313284L; // Feb 16, 2004 10:49... PST
//        java.util.Date date = new java.util.Date(now);
//        java.sql.Timestamp realTimestamp = new java.sql.Timestamp(now);
//        java.util.Date timestamp = realTimestamp;
//        // sanity check 1:
//        assertEquals(284000000, realTimestamp.getNanos());
//        assertEquals(1076957313284L, date.getTime());
//        //
//        // On Sun 1.3.1_10:
//        //junit.framework.AssertionFailedError: expected:<1076957313284> but was:<1076957313000>
//        //
//        //assertEquals(1076957313284L, timestamp.getTime());
//        //
//        //junit.framework.AssertionFailedError: expected:<1076957313284> but was:<1076957313000>
//        //
//        //assertEquals(1076957313284L, realTimestamp.getTime());
//        // sanity check 2:        
//        assertEquals(date.getDay(), realTimestamp.getDay());
//        assertEquals(date.getHours(), realTimestamp.getHours());
//        assertEquals(date.getMinutes(), realTimestamp.getMinutes());
//        assertEquals(date.getMonth(), realTimestamp.getMonth());
//        assertEquals(date.getSeconds(), realTimestamp.getSeconds());
//        assertEquals(date.getTimezoneOffset(), realTimestamp.getTimezoneOffset());
//        assertEquals(date.getYear(), realTimestamp.getYear());
//        //
//        // Time values are == and equals() on Sun 1.4.2_03 but NOT on Sun 1.3.1_10:
//        //
//        //assertFalse("Sanity check failed: date.getTime() == timestamp.getTime()", date.getTime() == timestamp.getTime());
//        //assertFalse("Sanity check failed: timestamp.equals(date)", timestamp.equals(date));
//        //assertFalse("Sanity check failed: date.equals(timestamp)", date.equals(timestamp));
//        // real test:
//        //assertFalse("java.util.Date and java.sql.Timestamp should be equal", ObjectUtils.equals(date, timestamp));
//    }
    
    public void testIdentityToString() {
        assertEquals(null, ObjectUtils.identityToString(null));
        assertEquals(
            "java.lang.String@" + Integer.toHexString(System.identityHashCode(FOO)),
            ObjectUtils.identityToString(FOO));
        Integer i = new Integer(90);
        String expected = "java.lang.Integer@" + Integer.toHexString(System.identityHashCode(i));
        assertEquals(expected, ObjectUtils.identityToString(i));
        StringBuffer buffer = new StringBuffer();
        ObjectUtils.identityToString(buffer, i);
        assertEquals(expected, buffer.toString());

        try {
            ObjectUtils.identityToString(null, "tmp");
            fail("NullPointerException expected");
        } catch(NullPointerException npe) {
        }
        try {
            ObjectUtils.identityToString(new StringBuffer(), null);
            fail("NullPointerException expected");
        } catch(NullPointerException npe) {
        }
    }

    public void testToString_Object() {
        assertEquals("", ObjectUtils.toString((Object) null) );
        assertEquals(Boolean.TRUE.toString(), ObjectUtils.toString(Boolean.TRUE) );
    }
            
    public void testToString_ObjectString() {
        assertEquals(BAR, ObjectUtils.toString((Object) null, BAR) );
        assertEquals(Boolean.TRUE.toString(), ObjectUtils.toString(Boolean.TRUE, BAR) );
    }

    public void testNull() {
        assertNotNull(ObjectUtils.NULL);
        assertTrue(ObjectUtils.NULL instanceof ObjectUtils.Null);
        assertSame(ObjectUtils.NULL, SerializationUtils.clone(ObjectUtils.NULL));
    }
    
    
    
    public void testMax() {
        Calendar calendar = Calendar.getInstance();
        Date nonNullComparable1 = calendar.getTime();
        Date nonNullComparable2 = calendar.getTime();
        String[] nullAray = null;
        
        calendar.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) -1 );
        Date minComparable = calendar.getTime();
        
        assertNotSame( nonNullComparable1, nonNullComparable2 );
        
        assertNull(ObjectUtils.max( (String) null ) );
        assertNull(ObjectUtils.max( nullAray ) );
        assertSame( nonNullComparable1, ObjectUtils.max( null, nonNullComparable1 ) );
        assertSame( nonNullComparable1, ObjectUtils.max( nonNullComparable1, null ) );
        assertSame( nonNullComparable1, ObjectUtils.max( null, nonNullComparable1, null ) );
        assertSame( nonNullComparable1, ObjectUtils.max( nonNullComparable1, nonNullComparable2 ) );
        assertSame( nonNullComparable2, ObjectUtils.max( nonNullComparable2, nonNullComparable1 ) );
        assertSame( nonNullComparable1, ObjectUtils.max( nonNullComparable1, minComparable ) );
        assertSame( nonNullComparable1, ObjectUtils.max( minComparable, nonNullComparable1 ) );
        assertSame( nonNullComparable1, ObjectUtils.max( null, minComparable, null, nonNullComparable1 ) );

        assertNull( ObjectUtils.max((String)null, (String)null) );
    }
    
    public void testMin() {
        Calendar calendar = Calendar.getInstance();
        Date nonNullComparable1 = calendar.getTime();
        Date nonNullComparable2 = calendar.getTime();
        String[] nullAray = null;
        
        calendar.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) -1 );
        Date minComparable = calendar.getTime();
        
        assertNotSame( nonNullComparable1, nonNullComparable2 );
        
        assertNull(ObjectUtils.min( (String) null ) );
        assertNull(ObjectUtils.min( nullAray ) );
        assertSame( nonNullComparable1, ObjectUtils.min( null, nonNullComparable1 ) );
        assertSame( nonNullComparable1, ObjectUtils.min( nonNullComparable1, null ) );
        assertSame( nonNullComparable1, ObjectUtils.min( null, nonNullComparable1, null ) );
        assertSame( nonNullComparable1, ObjectUtils.min( nonNullComparable1, nonNullComparable2 ) );
        assertSame( nonNullComparable2, ObjectUtils.min( nonNullComparable2, nonNullComparable1 ) );
        assertSame( minComparable, ObjectUtils.min( nonNullComparable1, minComparable ) );
        assertSame( minComparable, ObjectUtils.min( minComparable, nonNullComparable1 ) );
        assertSame( minComparable, ObjectUtils.min( null, nonNullComparable1, null, minComparable ) );

        assertNull( ObjectUtils.min((String)null, (String)null) );
    }

    /**
     * Tests {@link ObjectUtils#compare(Comparable, Comparable, boolean)}.
     */
    public void testCompare() {
        Integer one = Integer.valueOf(1);
        Integer two = Integer.valueOf(2);
        Integer nullValue = null;

        assertEquals("Null Null false", 0, ObjectUtils.compare(nullValue, nullValue));
        assertEquals("Null Null true",  0, ObjectUtils.compare(nullValue, nullValue, true));

        assertEquals("Null one false", -1, ObjectUtils.compare(nullValue, one));
        assertEquals("Null one true",   1, ObjectUtils.compare(nullValue, one, true));
        
        assertEquals("one Null false", 1, ObjectUtils.compare(one, nullValue));
        assertEquals("one Null true", -1, ObjectUtils.compare(one, nullValue, true));

        assertEquals("one two false", -1, ObjectUtils.compare(one, two));
        assertEquals("one two true",  -1, ObjectUtils.compare(one, two, true));
    }

    /**
     * Tests {@link ObjectUtils#clone(Object)} with a cloneable object.
     */
    public void testCloneOfCloneable() {
        final CloneableString string = new CloneableString("apache");
        final CloneableString stringClone = ObjectUtils.clone(string);
        assertEquals("apache", stringClone.getValue());
    }

    /**
     * Tests {@link ObjectUtils#clone(Object)} with a not cloneable object.
     */
    public void testCloneOfNotCloneable() {
        final String string = new String("apache");
        assertNull(ObjectUtils.clone(string));
    }

    /**
     * Tests {@link ObjectUtils#clone(Object)} with an uncloneable object.
     */
    public void testCloneOfUncloneable() {
        final UncloneableString string = new UncloneableString("apache");
        try {
            ObjectUtils.clone(string);
            fail("Thrown " + CloneFailedException.class.getName() + " expected");
        } catch (final CloneFailedException e) {
            assertEquals(NoSuchMethodException.class, e.getCause().getClass());
        }
    }

    /**
     * Tests {@link ObjectUtils#clone(Object)} with an object array.
     */
    public void testCloneOfStringArray() {
        assertTrue(Arrays.deepEquals(
            new String[]{"string"}, ObjectUtils.clone(new String[]{"string"})));
    }

    /**
     * Tests {@link ObjectUtils#clone(Object)} with an array of primitives.
     */
    public void testCloneOfPrimitiveArray() {
        assertTrue(Arrays.equals(new int[]{1}, ObjectUtils.clone(new int[]{1})));
    }

    /**
     * Tests {@link ObjectUtils#cloneIfPossible(Object)} with a cloneable object.
     */
    public void testPossibleCloneOfCloneable() {
        final CloneableString string = new CloneableString("apache");
        final CloneableString stringClone = ObjectUtils.cloneIfPossible(string);
        assertEquals("apache", stringClone.getValue());
    }

    /**
     * Tests {@link ObjectUtils#cloneIfPossible(Object)} with a not cloneable object.
     */
    public void testPossibleCloneOfNotCloneable() {
        final String string = new String("apache");
        assertSame(string, ObjectUtils.cloneIfPossible(string));
    }

    /**
     * Tests {@link ObjectUtils#cloneIfPossible(Object)} with an uncloneable object.
     */
    public void testPossibleCloneOfUncloneable() {
        final UncloneableString string = new UncloneableString("apache");
        try {
            ObjectUtils.cloneIfPossible(string);
            fail("Thrown " + CloneFailedException.class.getName() + " expected");
        } catch (final CloneFailedException e) {
            assertEquals(NoSuchMethodException.class, e.getCause().getClass());
        }
    }

    /**
     * String that is cloneable.
     */
    static final class CloneableString extends MutableObject<String> implements Cloneable {
        private static final long serialVersionUID = 1L;
        CloneableString(final String s) {
            super(s);
        }

        @Override
        public CloneableString clone() throws CloneNotSupportedException {
            return (CloneableString)super.clone();
        }
    }

    /**
     * String that is not cloneable.
     */
    static final class UncloneableString extends MutableObject<String> implements Cloneable {
        private static final long serialVersionUID = 1L;
        UncloneableString(final String s) {
            super(s);
        }
    }

}
