package com.puzzlemaker.unit;
import com.puzzlemaker.comparison.ComparableField;
import com.puzzlemaker.comparison.ComparisonResult;
import com.puzzlemaker.comparison.fields.ComparableDouble;
import com.puzzlemaker.comparison.fields.ComparableInteger;
import com.puzzlemaker.comparison.fields.ComparableList;
import com.puzzlemaker.comparison.fields.ComparableString;
import jdk.jfr.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.List;

public class Comparators {
    ComparableField<Double> cmpDouble1 = new ComparableDouble(10.0);
    ComparableField<Double> cmpDouble2 = new ComparableDouble(10.0);
    ComparableField<Double> cmpDouble3 = new ComparableDouble(11.0);
    ComparableField<Double> cmpDouble4 = new ComparableDouble(9.0);
    ComparableField<Integer> cmpInteger1 = new ComparableInteger(10);
    ComparableField<Integer> cmpInteger2 = new ComparableInteger(10);
    ComparableField<Integer> cmpInteger3 = new ComparableInteger(11);
    ComparableField<Integer> cmpInteger4 = new ComparableInteger(9);
    ComparableField<String> cmpString1 = new ComparableString("10");
    ComparableField<String> cmpString2 = new ComparableString("10.0");
    ComparableField<String> cmpString3 = new ComparableString("test");
    ComparableField<String> cmpString4 = new ComparableString("test");
    ComparableField<String> cmpString5 = new ComparableString("Test");
    ComparableField<String> cmpString6 = new ComparableString("test more");
    ComparableField<List<String>> cmpList1 = new ComparableList(List.of("e1", "e2", "e3", "e4"));
    ComparableField<List<String>> cmpList2 = new ComparableList(List.of("e1", "e2", "e4", "e3"));
    ComparableField<List<String>> cmpList3 = new ComparableList(List.of("e1", "e2", "e3"));
    ComparableField<List<String>> cmpList4 = new ComparableList(List.of("e1", "e2", "e3", "e4"));
    ComparableField<List<String>> cmpList5 = new ComparableList(List.of("e1", "e2", "e4", "e6"));
    ComparableField<List<String>> cmpList6 = new ComparableList(List.of("e7", "e8", "e9", "e10"));
    ComparableField<List<String>> cmpList7 = new ComparableList(List.of("10"));
    ComparableField<List<String>> cmpList8 = new ComparableList(List.of("10.0"));
    ComparableField<List<String>> cmpList9 = new ComparableList(List.of("test"));
    @Test
    @Description("Double comparator same type")
    public void doubleComparatorSameTypeTests(){
        //proper type comparison results
        ComparisonResult result1 = cmpDouble1.compareTo(cmpDouble2);
        ComparisonResult result2 = cmpDouble1.compareTo(cmpDouble3);
        ComparisonResult result3 = cmpDouble1.compareTo(cmpDouble4);


        Assertions.assertEquals(ComparisonResult.MATCH, result1);
        Assertions.assertEquals(ComparisonResult.HIGHER, result2);
        Assertions.assertEquals(ComparisonResult.LOWER, result3);

    }
    @Test
    @Description("Double comparator test type mismatch")
    public void doubleComparatorTypeMismatchTests(){
        ComparisonResult result1 = cmpDouble1.compareTo(cmpInteger1);
        ComparisonResult result2 = cmpDouble1.compareTo(cmpString2);
        ComparisonResult result3 = cmpDouble1.compareTo(cmpList8);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result1);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result2);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result3);
    }
    @Test
    @Description("Integer comparator test correct type")
    public void integerComparatorTypeCorrectTest(){
        ComparisonResult result1 = cmpInteger1.compareTo(cmpInteger2);
        ComparisonResult result2 = cmpInteger1.compareTo(cmpInteger3);
        ComparisonResult result3 = cmpInteger1.compareTo(cmpInteger4);
        Assertions.assertEquals(ComparisonResult.MATCH, result1);
        Assertions.assertEquals(ComparisonResult.HIGHER, result2);
        Assertions.assertEquals(ComparisonResult.LOWER, result3);
    }
    @Test
    @Description("Integer comparator test type mismatch")
    public void integerComparatorTypeMismatchTests(){
        ComparisonResult result1 = cmpInteger1.compareTo(cmpDouble1);
        ComparisonResult result2 = cmpInteger1.compareTo(cmpString1);
        ComparisonResult result3 = cmpInteger1.compareTo(cmpList7);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result1);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result2);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result3);
    }
    @Test
    @Description("String comparator test correct type")
    public void stringComparatorTypeCorrectTest(){
        ComparisonResult result1 = cmpString3.compareTo(cmpString4);
        ComparisonResult result2 = cmpString3.compareTo(cmpString5);
        ComparisonResult result3 = cmpString3.compareTo(cmpString6);
        Assertions.assertEquals(ComparisonResult.MATCH, result1);
        Assertions.assertEquals(ComparisonResult.NO_MATCH, result2);
        Assertions.assertEquals(ComparisonResult.NO_MATCH, result3);
    }
    @Test
    @Description("String comparator test type mismatch")
    public void stringComparatorTypeMismatchTests(){
        ComparisonResult result1 = cmpString1.compareTo(cmpInteger1);
        ComparisonResult result2 = cmpString2.compareTo(cmpDouble1);
        ComparisonResult result3 = cmpString3.compareTo(cmpList9);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result1);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result2);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result3);
    }
    @Test
    @Description("List comparator test correct type")
    public void listComparatorTypeCorrectTest(){
        ComparisonResult result1 = cmpList1.compareTo(cmpList2);
        ComparisonResult result2 = cmpList1.compareTo(cmpList3);
        ComparisonResult result3 = cmpList1.compareTo(cmpList4);
        ComparisonResult result4 = cmpList1.compareTo(cmpList5);
        ComparisonResult result5 = cmpList1.compareTo(cmpList6);
        Assertions.assertEquals(ComparisonResult.MATCH, result1);
        Assertions.assertEquals(ComparisonResult.PARTIAL_MATCH, result2);
        Assertions.assertEquals(ComparisonResult.MATCH, result3);
        Assertions.assertEquals(ComparisonResult.PARTIAL_MATCH, result4);
        Assertions.assertEquals(ComparisonResult.NO_MATCH, result5);
    }
    @Test
    @Description("List comparator test type mismatch")
    public void listComparatorTypeMismatchTests(){
        ComparisonResult result1 = cmpList7.compareTo(cmpInteger1);
        ComparisonResult result2 = cmpList8.compareTo(cmpDouble1);
        ComparisonResult result3 = cmpList9.compareTo(cmpString3);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result1);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result2);
        Assertions.assertEquals(ComparisonResult.TYPE_MISMATCH, result3);
    }
}
