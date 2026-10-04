package io.github.alexxfromgit.taf.mobile.core.lint;

/** One finding of the {@link MetadataLinter}. */
public record LintViolation(String location, String problem) {

    @Override
    public String toString() {
        return location + ": " + problem;
    }
}
