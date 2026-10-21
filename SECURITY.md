# Data and credential handling

This demonstration runs locally using Java and bundled synthetic data. It does not require an API key, access token, database password, cloud account, or real customer dataset.

- The CSV is generated from a seeded simulator. It contains fictional product observations and no personal customer information.
- Do not commit employer/customer exports, credentials, private keys, personal configuration, or local Maven settings.
- The repository's .gitignore excludes common environment files, private-key formats, credential files, local build output, logs, and IDE configuration.
- .gitignore prevents matching untracked files from being added by ordinary git add; it does not remove a secret that is already tracked or protect an explicit web upload. Review files before every publication.
- Generated examples contain simulated results. They do not demonstrate realised commercial profit.
- Build dependencies are downloaded from the public repositories configured by Maven. Supply your own approved proxy settings locally if your network requires them.

If a credential is ever committed accidentally, revoke/rotate it with its issuer and address its repository history. Do not put the credential itself in a public issue.
