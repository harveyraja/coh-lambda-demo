# Coherence Distributed Lambdas Demo

This demo was shown at Oracle Code 2017 London. It illustrates using Coherence
Distributed Lambdas with the following capabilities:
 - Use serialization other than Java serialization
 - Send the lambda definition to the servers that own the data thus achieve 
   in-place processing without requiring server restart.
 
To run the Demo ([`main.F1`](src/main/java/main/F1.java)) you will need to start 
a storage server. During the talk this was performed using an IntelliJ run configuration
with the following args:
```text
Main Class: com.tangosol.net.DefaultCacheServer
VM Arguments: -Dcoherence.localhost=127.0.0.1 -Djava.net.preferIPv4Stack=true
```

> Note: by adding the JVM argument `-Dcoherence.distributed.persistence.mode=active`
> to the run configuration, then killing the server does not require reloading
> the data :wink: :astonished:

The [presentation](Cloud%20Ready%20Distributed%20Lambdas.pdf) is also in this
GIT repo.