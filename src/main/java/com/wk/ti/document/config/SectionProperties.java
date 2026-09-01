package com.wk.ti.document.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration
@ConfigurationProperties(prefix = "question-generation.section")
public class SectionProperties {

    private int targetChars = 12000;
    private int maxChars = 18000;
    private int minChars = 2000;
    private String headingPattern = "^(?:\\d+(?:\\.\\d+)*[.)]?\\s+.+|(?:CHAPTER|Chapter)\\s+.+)$";
    private Pattern compiledPattern;

    public int getTargetChars() { return targetChars; }
    public void setTargetChars(int targetChars) { this.targetChars = targetChars; }

    public int getMaxChars() { return maxChars; }
    public void setMaxChars(int maxChars) { this.maxChars = maxChars; }

    public int getMinChars() { return minChars; }
    public void setMinChars(int minChars) { this.minChars = minChars; }

    public String getHeadingPattern() { return headingPattern; }
    public void setHeadingPattern(String headingPattern) {
        this.headingPattern = headingPattern;
        this.compiledPattern = Pattern.compile(headingPattern, Pattern.MULTILINE);
    }

    public Pattern getCompiledPattern() {
        if (compiledPattern == null) {
            compiledPattern = Pattern.compile(headingPattern, Pattern.MULTILINE);
        }
        return compiledPattern;
    }
}
