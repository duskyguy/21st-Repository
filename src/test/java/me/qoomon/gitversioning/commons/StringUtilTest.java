package me.qoomon.gitversioning.commons;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class StringUtilTest {

    @Test
    void substituteText() {
        // Given
        String givenText = "${type}tale";
        Map<String, Supplier<String>> givenSubstitutionMap = new HashMap<>();
        givenSubstitutionMap.put("type", () -> "fairy");

        // When
        String outputText = StringUtil.substituteText(givenText, givenSubstitutionMap);

        // Then
        assertThat(outputText).isEqualTo("fairytale");
    }

    @Test
    void substituteText_missingValue() {

        // Given
        String givenText = "${missing}tale";
        Map<String, Supplier<String>> givenSubstitutionMap = new HashMap<>();

        // When
        String outputText = StringUtil.substituteText(givenText, givenSubstitutionMap);

        // Then
        assertThat(outputText).isEqualTo("${missing}tale");
    }

    @Test
    void substituteText_handle_replacement_value_with_placeholder_syntax() {

        // Given
        String givenText = "${version}";
        Map<String, Supplier<String>> givenSubstitutionMap = new HashMap<>();
        givenSubstitutionMap.put("version", () -> "${something}");

        // When
        String outputText = StringUtil.substituteText(givenText, givenSubstitutionMap);

        // Then
        assertThat(outputText).isEqualTo("${something}");
    }

    @Test
    void substituteText_default_value() {

        // Given
        String givenText = "${foo:-xxx}";
        Map<String, Supplier<String>> givenSubstitutionMap = emptyMap();

        // When
        String outputText = StringUtil.substituteText(givenText, givenSubstitutionMap);

        // Then
        assertThat(outputText).isEqualTo("xxx");
    }

    @Test
    void substituteText_overwrite_value() {

        // Given
        String givenText = "${foo:+xxx}";
        Map<String, Supplier<String>> givenSubstitutionMap = new HashMap<>();
        givenSubstitutionMap.put("foo", () -> "aaa");

        // When
        String outputText = StringUtil.substituteText(givenText, givenSubstitutionMap);

        // Then
        assertThat(outputText).isEqualTo("xxx");
    }

    @Test
    void substituteText_combined_overwrite_default_value() {
        // Given
        String givenText = "${name:+foo:-bar}";
        Map<String, Supplier<String>> givenSubstitutionMap = new HashMap<>();

        // check existing value
        givenSubstitutionMap.put("name", () -> "aaa");

        // When
        String outputText = StringUtil.substituteText(givenText, givenSubstitutionMap);
        // Then
        assertThat(outputText).isEqualTo("foo");

        // check non-existing value
        givenSubstitutionMap.clear();

        // When
        outputText = StringUtil.substituteText(givenText, givenSubstitutionMap);
        // Then
        assertThat(outputText).isEqualTo("bar");
    }

    @Test
    void valueGroupMap() {

        // Given
        Pattern givenRegex = Pattern.compile("(one) (two) (three)");
        String givenText = "one two three";

        // When
        Map<String, String> valueMap = StringUtil.patternGroupValues(givenRegex, givenText);

        // Then
        assertThat(valueMap).contains(entry("1", "one"), entry("2", "two"), entry("3", "three"));
    }

    @Test
    void valueGroupMap_nested() {

        // Given
        Pattern givenRegex = Pattern.compile("(one) (two (three))");
        String givenText = "one two three";

        // When
        Map<String, String> valueMap = StringUtil.patternGroupValues(givenRegex, givenText);

        // Then
        assertThat(valueMap).contains(entry("1", "one"), entry("2", "two three"), entry("3", "three"));
    }

    @Test
    void valueGroupMap_namedGroup() {

        // Given
        Pattern givenRegex = Pattern.compile("(?<first>one) (?<second>two) (?<third>three)");
        String givenText = "one two three";

        // When
        Map<String, String> valueMap = StringUtil.patternGroupValues(givenRegex, givenText);

        // Then
        assertThat(valueMap).contains(entry("1", "one"), entry("2", "two"), entry("3", "three"));
        assertThat(valueMap).contains(entry("first", "one"), entry("second", "two"), entry("third", "three"));
    }

    @Test
    void valueGroupMap_namedGroupNested() {

        // Given
        Pattern givenRegex = Pattern.compile("(?<first>one) (?<second>two (?<third>three))");
        String givenText = "one two three";

        // When
        Map<String, String> valueMap = StringUtil.patternGroupValues(givenRegex, givenText);

        // Then
        assertThat(valueMap).contains(entry("1", "one"), entry("2", "two three"), entry("3", "three"));
        assertThat(valueMap).contains(entry("first", "one"), entry("second", "two three"), entry("third", "three"));
    }
}
