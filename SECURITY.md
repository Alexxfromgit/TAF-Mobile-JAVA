# Security policy

## Reporting a vulnerability

Please **do not open a public issue** for security problems. Use GitHub's
[private vulnerability reporting](https://github.com/Alexxfromgit/TAF-Mobile-JAVA/security/advisories/new) instead.
You will get a response within a few days.

## Secrets in tests

- User passwords and cloud credentials are read only from environment variables;
- configuration loading fails if a `.properties` file contains a secret-like value, and the linter checks the same;
- typed passwords are masked in Allure steps and `UserCredentials.toString()` never prints them.

If you find a way these safeguards can be bypassed, please report it as above.
