package io.github.alexxfromgit.taf.mobile.core.users;

/** Credentials of a test account. {@link #toString()} never prints the password. */
public record UserCredentials(String alias, String username, String password) {

    @Override
    public String toString() {
        return alias + " (" + username + ")";
    }
}
