# jmx-test1
This is very simple example of how to use JXM API to expose beans and call
arbitrary methods on those beans.

# Build
You need gradle to build this project

```Bash
# Enter in the project root directory
cd jxm-text1

gradle build
```

# Usage
Start the main app

```Bash
java -Dcom.sun.management.jmxremote.port=9999 \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false -jar \
  ./build/libs/jmx-test1-0.0.1.jar
 ```

To print all the stored commands:

```Bash
java -jar ./build/libs/jmx-test1-0.0.1.jar client
```

To cleanup stored commands use:

```Bash
java -jar ./build/libs/jmx-test1-0.0.1.jar client clear
```

# License
This project is licensed under the terms of
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
For more information please see the ```LICENSE``` file.

# Authors
Dzhem Riza
