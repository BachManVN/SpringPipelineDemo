package com.pipeline.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GreetingServiceTest {

    private GreetingService greetingService;

    @BeforeEach
    void setUp() {
        greetingService = new GreetingService();
    }

    @Test
    void testGetGreeting_WithValidName() {
        String result = greetingService.getGreeting("John");
        assertThat(result).isEqualTo("Hello, John!");
    }

    @Test
    void testGetGreeting_WithNullName() {
        String result = greetingService.getGreeting(null);
        assertThat(result).isEqualTo("Hello, Guest!");
    }

    @Test
    void testGetGreeting_WithEmptyName() {
        String result = greetingService.getGreeting("");
        assertThat(result).isEqualTo("Hello, Guest!");
    }

    @Test
    void testGetGreeting_WithWhitespaceName() {
        String result = greetingService.getGreeting("   ");
        assertThat(result).isEqualTo("Hello, Guest!");
    }

    @Test
    void testGetFormalGreeting_WithNameAndTitle() {
        String result = greetingService.getFormalGreeting("Doe", "Mr");
        assertThat(result).isEqualTo("Hello, Mr Doe!");
    }

    @Test
    void testGetFormalGreeting_WithNameOnly() {
        String result = greetingService.getFormalGreeting("John", null);
        assertThat(result).isEqualTo("Hello, John!");
    }

    @Test
    void testGetFormalGreeting_WithNullName() {
        String result = greetingService.getFormalGreeting(null, "Dr");
        assertThat(result).isEqualTo("Hello, Guest!");
    }

    @Test
    void testGetFormalGreeting_WithEmptyTitle() {
        String result = greetingService.getFormalGreeting("John", "");
        assertThat(result).isEqualTo("Hello, John!");
    }

    @Test
    void testIsValidName_WithValidName() {
        assertThat(greetingService.isValidName("John")).isTrue();
    }

    @Test
    void testIsValidName_WithNull() {
        assertThat(greetingService.isValidName(null)).isFalse();
    }

    @Test
    void testIsValidName_WithEmptyString() {
        assertThat(greetingService.isValidName("")).isFalse();
    }

    @Test
    void testIsValidName_WithWhitespace() {
        assertThat(greetingService.isValidName("   ")).isFalse();
    }

    @Test
    void testIsValidName_WithLongName() {
        String longName = "a".repeat(101);
        assertThat(greetingService.isValidName(longName)).isFalse();
    }

    @Test
    void testIsValidName_WithMaxLengthName() {
        String maxLengthName = "a".repeat(100);
        assertThat(greetingService.isValidName(maxLengthName)).isTrue();
    }
}
