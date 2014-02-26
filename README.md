CDIProperties
=================

CDIProperties is a CDI extension that introduces an annotation based and extensiable approach for using properties read from a properties file.

It's mainly introduced for usage in CDI-SE context, but is also usefull in the context of JEE applications.

### Requirements

* Java 1.6+
* CDI 1.0

### Artifact coordinates

* **Group**  com.coders-kitchen 
* **Artifact**  cdi-properties 
* **Version**  1.0.1

Features
----

 1. Load property files from the classpath or file system
 2. Caching of loaded properties available
 3. Build in and extensible mechanism of converting properties to required types
 3. Shipped with converters for all basic types of java including wrapper classes
 4. Configurable via system properties

Basic Usage
----

When the library is on the compile classpath, two new annotations become available

* @PropertyFile(value = ...) - specifies the file where the properties are located
* @Property(value = ...) - specifies the name of the property which should be injected

Using a property from a file into a class becomes now easy

1. Create the properties file and put it in the root of the classpath
2. Annotate the class which should use values from the file with @PropertyFile and specifiy the name of the file as the value
3. Annotate the field of the class that should contain the value with @Property. Set the value of the annotation to the key of the property.

### An example

**application.properties**
```ini
name=MySampleApp
version=1.0.0
productive=true
```

**ApplicationProperties.java**
```java
@PropertyFile("application.properties")
public class ApplicationProperties {

  @Property("name")
  private String name;

  @Property("version")
  private String version;

  @Property("productive")
  private boolean productive;

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public boolean isProductive() {
    return productive;
  }
}
```

Advanced usage
--

### Change lookup order

The extension does the lookup in both, the classpath and the filesystem. By default the classpath is searched first, and than the filesystem. 

This behavior can be changed by setting the system property ```com.coderskitchen.cdiproperties.preferFileSystem``` to ```true```.

### Set base lookup path in file system

The default basic path for the search on the filesystem is ```/``` (on Linux, MAC) and installation HDD on Windows. There are two options to specify the location of the properties file on the file system

1. Hardcode the value in the ```@PropertyFile``` annotation - not recommended
2. Use the system property ```com.coderskitchen.cdiproperties.baseFolder``` - recommended

### Enable caching of already loaded properties

By default, CDIProperties doesn't cache loaded properties. This is to enable e.g. war archives in ear deployments to have properties files with same name but with different content per archive

For enabling properties caching set the value of the system property ```com.coderskitchen.cdiproperties.useCaching``` to ```true```.

Adding new value converter
--

For adding a new value converter you must implement the interface ```com.coderskitchen.cdiproperties.converter.spi.ValueConverter``` and add the file ```com.coderskitchen.cdiproperties.converter.spi.ValueConverter``` to ```META-INF/services```. This file must contain the full-qualified-name of your implementation.

At the moment only type based decisions are available. In a later release also property name,  property file name and/or target class based decision may be come available.


General hints
--

It's recommended to use classes like ```ApplicationProperties``` in the example to store and access the values of a properties file.

### EAR archives

#### Properties as common resources.

For EAR archives I recommend to follow this approach:

1. Add the CDIProperties archive as an earlib dependency
2. Create a simple war archive that only contains your common resources and a empty beans.xml in WEB-INF folder
3. Add CDIProperties archive as provided dependency to other war artifacts and the war archive from step 2 as runtime dependency.